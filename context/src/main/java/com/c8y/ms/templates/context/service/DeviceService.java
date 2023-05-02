package com.c8y.ms.templates.context.service;

import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeviceService {

    @Autowired
    @Qualifier("userInventoryApi")
    InventoryApi userInventoryApi;

    @Autowired
    InventoryApi tenantInventoryApi;

    @Autowired
    MicroserviceSubscriptionsService subscriptionsService;

    @Autowired(required = true)
    @Qualifier("userPlatform")
    private Platform platformApi;

    public List<ManagedObjectRepresentation> getAllDevicesUser() {
        List<ManagedObjectRepresentation> morList = new ArrayList<>();
        userInventoryApi.getManagedObjects().get().allPages().forEach(mor -> {
            morList.add(mor);
        });
        return morList;
    }

    public List<ManagedObjectRepresentation> getAllDevicesUser2() {
        List<ManagedObjectRepresentation> morList = new ArrayList<>();
        platformApi.getInventoryApi().getManagedObjects().get().allPages().forEach(mor -> {
            morList.add(mor);
        });
        return morList;
    }

    public List<ManagedObjectRepresentation> getAllDevicesTenant() {
        List<ManagedObjectRepresentation> morList = new ArrayList<>();
        tenantInventoryApi.getManagedObjects().get().allPages().forEach(mor -> {
            morList.add(mor);
        });
        return morList;
    }

    public List<ManagedObjectRepresentation> getAllDevicesTenant2() {
        List<ManagedObjectRepresentation> morList = new ArrayList<>();
        subscriptionsService.runForEachTenant(() -> {
            tenantInventoryApi.getManagedObjects().get().allPages().forEach(mor -> {
                morList.add(mor);
            });
        });
        return morList;
    }


}
