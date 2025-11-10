package com.c8y.ms.templates.agent.service;

import c8y.IsDevice;
import c8y.SoftwareList;
import c8y.SoftwareListEntry;
import c8y.SupportedOperations;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.model.Agent;
import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.google.common.base.Optional;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for add microservice as representation in cumulocity als
 * managedobject with agent fragment in order to store events etc. to it.
 *
 * @author alexander.pester@cumulocity.com
 * @version 0.0.1
 * <p>
 * 03.09.2019
 */
@Service
public class MicroserviceRepresentationService {
    private static final Logger LOG = LoggerFactory.getLogger(MicroserviceRepresentationService.class);

    private static final String MICROSERVICE_TYPE = "template_Microservice";
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

    @Value("${microservice.version}")
    protected String applicationVersion;

    @Value("${c8y.version}")
    protected String c8yVersion;

    private final Map<String, ManagedObjectRepresentation> agentRepresentations = new ConcurrentHashMap<>();

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
     * Register microservice as an agent for subscribed tenant only.
     *
     * @param event
     */
    @EventListener
    private void onSubscriptionEvent(final MicroserviceSubscriptionAddedEvent event) {
        String subscriptTenant = event.getCredentials().getTenant();

        subscriptions.runForTenant(subscriptTenant, new Runnable() {

            @Override
            public void run() {
                ManagedObjectRepresentation serviceRepresentation = findOrCreateSource(subscriptTenant);
                long id = agentRepresentations.get(subscriptTenant) != null
                        ? agentRepresentations.get(subscriptTenant).getId().getLong()
                        : 0L;
                createEvent(serviceRepresentation, EVENT_TYPE,
                        "Microservice representation created or updated (onSubsription)!",
                        "Tenant: " + subscriptTenant + " ApplicationId: " + id);
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
        Optional<GId> gId = findIdentity(MICROSERVICE_TYPE, applicationName);
        ManagedObjectRepresentation microserviceRepresentation = null;
        if (gId.isPresent()) {
            LOG.info("Microservice representation found: " + gId.get().getValue());
            microserviceRepresentation = findManagedObjectById(tenant, gId.get());
        } else {
            LOG.info("Microservice representation not found. Create");
            microserviceRepresentation = createManagedObject(MICROSERVICE_TYPE, applicationName);
            createIdentity(microserviceRepresentation.getId(), MICROSERVICE_TYPE, applicationName);
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

    private ManagedObjectRepresentation findManagedObjectById(final String tenant, final GId id) {
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

    private ManagedObjectRepresentation updateManagedObject(ManagedObjectRepresentation currentManagedObject) {
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

    private void createEvent(ManagedObjectRepresentation source, String type, String text, Object details) {
        EventRepresentation event = new EventRepresentation();
        event.setText(text);
        event.setType(type);
        event.setSource(source);
        event.setDateTime(new DateTime());
        event.set(details, "nx_details");
        eventApi.create(event);
    }

    @Scheduled(fixedDelayString = "${diagnostic.logging.delay.millis:60000}", initialDelay = 10000)
    public void microserviceDiagnosticLogging() {
        subscriptions.runForEachTenant(() -> {
            ManagedObjectRepresentation source = agentRepresentations.get(subscriptions.getTenant());

            Runtime rt = Runtime.getRuntime();
            long total = rt.totalMemory();
            long free = rt.freeMemory();
            long used = total - free;

            Map<String, MeasurementValue> seriesMap = new HashMap<>();
            seriesMap.put("totalMemory", new MeasurementValue(new BigDecimal(total), "byte"));
            seriesMap.put("freeMemory", new MeasurementValue(new BigDecimal(free), "byte"));
            seriesMap.put("usedMemory", new MeasurementValue(new BigDecimal(used), "byte"));
            createMeasurementWithSeriesMap(source, "ms_DiagnosticLogging", seriesMap);
        });
    }

    private void createMeasurementWithSeriesMap(ManagedObjectRepresentation source, String type, Map<String, MeasurementValue> seriesMap) {
        MeasurementRepresentation measurement = new MeasurementRepresentation();
        measurement.setSource(source);
        measurement.setDateTime(new DateTime());
        measurement.setType(type);
        measurement.set(seriesMap, type);
        measurementApi.create(measurement);
    }
}
