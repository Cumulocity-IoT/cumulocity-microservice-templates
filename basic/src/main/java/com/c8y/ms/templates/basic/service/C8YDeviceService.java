package com.c8y.ms.templates.basic.service;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.pagination.RestPageRequest;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.QueryParam;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import com.cumulocity.sdk.client.inventory.ManagedObjectCollection;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an example service. This should be removed for your real project!
 *
 * @author APES
 */
@Service
public class C8YDeviceService {

    private static final Logger LOG = LoggerFactory.getLogger(C8YDeviceService.class);

    private InventoryApi inventoryApi;

    public C8YDeviceService(InventoryApi inventoryApi) {
        this.inventoryApi = inventoryApi;
    }

    public List<String> getAllDeviceNames() {
        List<String> allDeviceNames = new ArrayList<>();

        try {
            InventoryFilter inventoryFilter = new InventoryFilter();
            inventoryFilter.byFragmentType("c8y_IsDevice");

            ManagedObjectCollection managedObjectsByFilter = inventoryApi.getManagedObjectsByFilter(inventoryFilter);
            List<ManagedObjectRepresentation> allObjects = Lists.newArrayList(managedObjectsByFilter.get(RestPageRequest.MAX_PAGE_SIZE).allPages());

            for (ManagedObjectRepresentation managedObjectRepresentation : allObjects) {
                allDeviceNames.add(managedObjectRepresentation.getName());
            }
        } catch (SDKException exception) {
            LOG.error("Error while loading devices from Cumulocity", exception);
        }

        return allDeviceNames;
    }

    public ManagedObjectRepresentation getDeviceRepresentation(final String deviceId) {
        ManagedObjectRepresentation deviceRepresentation = new ManagedObjectRepresentation();

        if (StringUtils.isEmpty(deviceId)) {
            return deviceRepresentation;
        }

        try {
            deviceRepresentation = inventoryApi.get(new GId(deviceId));
        } catch (SDKException exception) {
            LOG.error("Error occurred while loading device representation", exception);
        }

        return deviceRepresentation;
    }

}
