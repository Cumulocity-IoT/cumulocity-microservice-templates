package com.c8y.ms.templates.basic.service;

import c8y.IsDevice;
import com.c8y.ms.templates.basic.model.Device;
import com.cumulocity.model.Agent;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.pagination.RestPageRequest;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import com.cumulocity.sdk.client.inventory.ManagedObjectCollection;
import com.google.common.base.Optional;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an example service. This should be removed for your real project!
 *
 * @author APES
 */
@Service
public class DeviceService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceService.class);

    private final InventoryApi inventoryApi;

    public DeviceService(InventoryApi inventoryApi) {
        this.inventoryApi = inventoryApi;
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
            inventoryFilter.byFragmentType("c8y_IsDevice");

            ManagedObjectCollection managedObjectsByFilter = inventoryApi.getManagedObjectsByFilter(inventoryFilter);
            List<ManagedObjectRepresentation> allObjects = managedObjectsByFilter.get(RestPageRequest.MAX_PAGE_SIZE).getManagedObjects();

            for (ManagedObjectRepresentation managedObjectRepresentation : allObjects) {
                allDeviceNames.add(managedObjectRepresentation.getName());
            }
        } catch (SDKException exception) {
            LOG.error("Error while loading devices from Cumulocity", exception);
        }

        return allDeviceNames;
    }

    public Optional<ManagedObjectRepresentation> getDeviceRepresentation(final String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
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
}
