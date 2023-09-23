package com.c8y.ms.templates.notification2.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.c8y.ms.templates.notification2.model.Device;
import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.model.Agent;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import com.cumulocity.sdk.client.inventory.ManagedObjectCollection;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import c8y.IsDevice;

/**
 * This is an example service. This should be removed for your real project!
 *
 * @author APES
 */
@Service
public class DeviceService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceService.class);

    private final InventoryApi inventoryApi;

    private final ContextService<MicroserviceCredentials> contextService;

    private final MicroserviceSubscriptionsService subscriptions;

    public DeviceService(InventoryApi inventoryApi, ContextService<MicroserviceCredentials> contextService,
                         MicroserviceSubscriptionsService subscriptions) {
        this.inventoryApi = inventoryApi;
        this.contextService = contextService;
        this.subscriptions = subscriptions;
    }

    /**
     * Query a list of devices from the Inventory by using the InventoryFilter and
     * fragmentType filter "c8y_IsDevice". In order to get the max page size use
     * RestPageRequest.MAX_PAGE_SIZE on the list.
     *
     * @return List<String>, a list of existing devices and their names.
     */
    public List<String> getAllDeviceNames() {
        List<String> allDeviceNames = new ArrayList<>();

        try {
            InventoryFilter inventoryFilter = new InventoryFilter();
            inventoryFilter.byFragmentType(IsDevice.class);

            ManagedObjectCollection managedObjectsByFilter = inventoryApi.getManagedObjectsByFilter(inventoryFilter);
            List<ManagedObjectRepresentation> allObjects = Lists.newArrayList(managedObjectsByFilter.get(2000).allPages());

            for (ManagedObjectRepresentation managedObjectRepresentation : allObjects) {
                allDeviceNames.add(managedObjectRepresentation.getName());
            }
        } catch (SDKException exception) {
            LOG.error("Error while loading devices from Cumulocity", exception);
        }

        return allDeviceNames;
    }

    public Optional<ManagedObjectRepresentation> getDeviceRepresentation(final String deviceId) {
        if (Strings.isNullOrEmpty(deviceId)) {
            return Optional.absent();
        }

        try {
            final ManagedObjectRepresentation deviceRepresentation = inventoryApi.get(new GId(deviceId));

            return Optional.of(deviceRepresentation);
        } catch (SDKException exception) {
            LOG.error("Error occurred while loading device representation", exception);
        }

        return Optional.absent();
    }

    /**
     * Create a new device in Cumulocity. This device will have the name and type coming from the initial request.
     * In addition, by using the device-capability-model library the managedObject will be defined as a device
     * (IsDevice.class) and agent (Agent.class).
     *
     * @param device provides the name and the type for the device, which should be created
     * @return ManagedObjectRepresentation of the newly created device
     */
    public Optional<ManagedObjectRepresentation> createDevice(final Device device) {
        final ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();
        managedObjectRepresentation.setName(device.getName());
        managedObjectRepresentation.setType(device.getType());
        managedObjectRepresentation.set(new IsDevice());
        managedObjectRepresentation.set(new Agent());

        try {
            final ManagedObjectRepresentation response = inventoryApi.create(managedObjectRepresentation);

            return Optional.of(response);
        } catch (SDKException exception) {
            LOG.error("Error occurred while create a new device", exception);
        }

        return Optional.absent();
    }

    /**
     * A simple scheduler which is configured using the annotation @Scheduled. Using the parameters fixedDelayString and
     * initialDelayString you can specify in what interval the scheduler should be executed and the initial delay. The parameters
     * can be updated within the application.properties.
     * <p>
     * The scheduler is running in a separate background thread. To access Cumulocity in such a background thread you must
     * provide the correct context/tenant for which a request should be executed. In this example subscriptions.runForEachTenant()
     * is being used which will run the request for all currently subscribed tenants. Another possibility is to use
     * ContextService<MicroserviceCredentials> and provide the context manually by running contextService.runWithinContext().
     */
    @Scheduled(fixedDelayString = "${scheduled.delay.millis:60000}", initialDelayString = "${scheduled.delay.millis:1000}")
    public void printDeviceNameFirstFoundToConsole() {
        subscriptions.runForEachTenant(() -> {
            final List<String> deviceNames = getAllDeviceNames();

            if (deviceNames == null || deviceNames.isEmpty()) {
                LOG.info("Devices couldn't be found!");
                return;
            }

            LOG.info("Found devices: {}", deviceNames);
        });
    }
}
