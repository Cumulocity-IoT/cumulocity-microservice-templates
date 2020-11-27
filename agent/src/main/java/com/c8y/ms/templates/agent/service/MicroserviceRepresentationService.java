package com.c8y.ms.templates.agent.service;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.model.Agent;
import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.google.common.base.Optional;

import c8y.IsDevice;
import c8y.SoftwareList;
import c8y.SoftwareListEntry;
import c8y.SupportedOperations;

/**
 * Service for add microservice as representation in cumulocity als
 * managedobject with agent fragment in order to store events etc. to it.
 * 
 * @author alexander.pester@softwareag.com
 * @version 0.0.1
 *
 *          03.09.2019
 */
@Service
public class MicroserviceRepresentationService {
	private static final Logger LOG = LoggerFactory.getLogger(MicroserviceRepresentationService.class);
	
	private static final String MICROSERVICE_TYPE = "nx_Agent";
	private static final String EVENT_TYPE = "service_monitoring";
	private final InventoryApi inventory;
	private final MicroserviceSubscriptionsService subscriptions;
	private final IdentityApi identityApi;
	private final EventApi eventApi;
	private final MeasurementApi measurementApi;
	private final MicroserviceOperationListenerService operationListenerService;
	private final MicroserviceConfigurationService configService;

	@Value("${application.name}")
	protected String applicationName;

	protected String masterTenant;

	@Value("${microservice.version}")
	protected String applicationVersion;

	@Value("${c8y.version}")
	protected String c8yVersion;

	private long count = 0;

	private final Map<String, ManagedObjectRepresentation> agentRepresentations = new ConcurrentHashMap<>();

	@Autowired
	public MicroserviceRepresentationService(InventoryApi inventory, MicroserviceSubscriptionsService subscriptions,
			IdentityApi identityApi, EventApi eventApi, MeasurementApi measurementApi,
			MicroserviceOperationListenerService operationListenerService,
			MicroserviceConfigurationService configService) {
		this.inventory = inventory;
		this.subscriptions = subscriptions;
		this.identityApi = identityApi;
		this.eventApi = eventApi;
		this.measurementApi = measurementApi;
		this.operationListenerService = operationListenerService;
		this.configService = configService;
	}

	/**
	 * Register microservice as an agent in every tenant.
	 *
	 * @param event
	 */
	@EventListener
	private void onSubscriptionEvent(final MicroserviceSubscriptionAddedEvent event) {
		masterTenant = event.getCredentials().getTenant();

		subscriptions.runForTenant(masterTenant, new Runnable() {

			@Override
			public void run() {
				ManagedObjectRepresentation serviceRepresentation = findOrCreateSource(masterTenant);
				long id = agentRepresentations.get(masterTenant) != null
						? agentRepresentations.get(masterTenant).getId().getLong()
						: 0L;
				createEvent(serviceRepresentation, EVENT_TYPE,
						"Microservice representation created or updated (onSubsription)!",
						"Tenant: " + masterTenant + " ApplicationId: " + id);
			}
		});

	}

	/**
	 * Find or create managed object for this microservice.
	 *
	 * @param tenant
	 * @return
	 */
	private ManagedObjectRepresentation findOrCreateSource(final String tenant) {
		Optional<GId> gId = findIdentity(MICROSERVICE_TYPE, getManagedObjectName());
		ManagedObjectRepresentation microserviceRepresentation = null;
		if (gId.isPresent()) {
			LOG.info("Microservice representation found: " + gId.get().getValue());
			microserviceRepresentation = findManagedObjectById(tenant, gId.get());
		} else {
			LOG.info("Microservice representation not found. Create");
			microserviceRepresentation = createManagedObject(MICROSERVICE_TYPE, getManagedObjectName());
			createIdentity(microserviceRepresentation.getId(), MICROSERVICE_TYPE, getManagedObjectName());
		}

		configService.setMicroserviceAgentRepresentation(microserviceRepresentation.getId());
		operationListenerService.registerForOperations(microserviceRepresentation.getId());
		agentRepresentations.put(tenant, microserviceRepresentation);
		return updateManagedObject(microserviceRepresentation);
	}

	private ExternalIDRepresentation createIdentity(final GId sourceId, final String type, final String identifier) {
		final ManagedObjectRepresentation source = new ManagedObjectRepresentation();
		source.setId(sourceId);

		final ExternalIDRepresentation externalId = new ExternalIDRepresentation();
		externalId.setManagedObject(source);
		externalId.setExternalId(identifier);
		externalId.setType(type);
		return identityApi.create(externalId);
	}

	private Optional<GId> findIdentity(final String type, final String identifier) {
		try {
			final ExternalIDRepresentation externalId = identityApi.getExternalId(new ID(type, identifier));
			return Optional.of(externalId.getManagedObject().getId());
		} catch (final SDKException ex) {
			if (ex.getHttpStatus() != 404) {
				throw ex;
			}
		}
		return Optional.absent();
	}

	/**
	 * Find managed object by id
	 *
	 * @param tenant
	 * @param id
	 * @return
	 */
	public ManagedObjectRepresentation findManagedObjectById(final String tenant, final GId id) {
		return subscriptions.callForTenant(tenant, new Callable<ManagedObjectRepresentation>() {

			@Override
			public ManagedObjectRepresentation call() throws Exception {
				return inventory.get(id);
			}
		});
	}

	private ManagedObjectRepresentation createManagedObject(final String type, final String name) {
		final ManagedObjectRepresentation managedObject = new ManagedObjectRepresentation();
		managedObject.setType(type);
		managedObject.setName(name);
		managedObject.set(new Agent());
		managedObject.set(new IsDevice());

		managedObject.set(createSoftwareListEntry());
		managedObject.set(configService.createConfigurationByProperties());

		SupportedOperations so = new SupportedOperations();
		so.add("c8y_Configuration");
		managedObject.set(so);

		return inventory.create(managedObject);
	}

	public ManagedObjectRepresentation updateManagedObject(ManagedObjectRepresentation currentManagedObject) {
		ManagedObjectRepresentation managedObject = new ManagedObjectRepresentation();
		managedObject.setId(currentManagedObject.getId());

		managedObject.set(createSoftwareListEntry());

		configService.loadConfiguration();
		managedObject.set(configService.createConfigurationByProperties());

		return inventory.update(managedObject);
	}

	private SoftwareList createSoftwareListEntry() {
		SoftwareListEntry softwareEntryApp = new SoftwareListEntry();
		softwareEntryApp.setVersion(applicationVersion);
		softwareEntryApp.setName(applicationName);

		SoftwareListEntry softwareEntrySDK = new SoftwareListEntry();
		softwareEntrySDK.setVersion(c8yVersion);
		softwareEntrySDK.setName("c8y_SDK_version");

		SoftwareList softwareList = new SoftwareList();
		softwareList.add(softwareEntryApp);
		softwareList.add(softwareEntrySDK);
		return softwareList;
	}

	public void createEvent(final String text, final Object details) {
		subscriptions.runForTenant(masterTenant, new Runnable() {
			@Override
			public void run() {
				ManagedObjectRepresentation source = agentRepresentations.get(masterTenant);
				createEvent(source, EVENT_TYPE, text, details);
			}
		});
	}

	public ManagedObjectRepresentation getManagedObjectRepresentation() {
		return agentRepresentations.get(masterTenant);
	}

	private void createEvent(ManagedObjectRepresentation source, String type, String text, Object details) {
		EventRepresentation event = new EventRepresentation();
		event.setText(text);
		event.setType(type);
		event.setSource(source);
		event.setDateTime(new DateTime());
		event.set(details, "nx_details");
		eventApi.create(event);
	}

	private String getManagedObjectName() {
		return applicationName;
	}

	public String getApplicationName() {
		return applicationName;
	}

}
