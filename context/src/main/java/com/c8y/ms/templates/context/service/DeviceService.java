package com.c8y.ms.templates.context.service;

import c8y.IsDevice;
import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.annotation.EnableContextSupport;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.context.credentials.UserCredentials;
import com.cumulocity.microservice.context.inject.UserScope;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@EnableContextSupport
@Service
public class DeviceService {

    private static final Logger log = LoggerFactory.getLogger(DeviceService.class);
    @Autowired
    private ContextService<MicroserviceCredentials> tenantContextService;

    @Autowired
    private ContextService<UserCredentials> userContextService;

    @Autowired
    @Qualifier("userInventoryApi")
    InventoryApi userInventoryApi;

    @Autowired
    InventoryApi inventoryApi;

    @Autowired
    MicroserviceSubscriptionsService subscriptionsService;

    @Autowired(required = true)
    @Qualifier("userPlatform")
    private Platform platformApi;

    @Autowired
    private Platform platform;

    @UserScope
    @Bean(name = "scopedUserInventoryApi")
    public InventoryApi scopedUserInventoryApi(Platform platform) throws SDKException {
        return platform.getInventoryApi();
    }

    /** Get all Devices using the userInventoryApi **/
    public List<ManagedObjectRepresentation> getAllDevicesUser() {
        List<ManagedObjectRepresentation> morList = new ArrayList<>();
        InventoryFilter filter = new InventoryFilter().byFragmentType(IsDevice.class);
        log.info("Retrieving devices using InventoryAPI with User Qualifier");
        userInventoryApi.getManagedObjectsByFilter(filter).get().allPages().forEach(mor -> {
            log.info("Found device {} with id {}", mor.getName(), mor.getId().getValue());
            morList.add(mor);
        });
        return morList;
    }

    /** Get all Devices using the userPlatform **/
    public List<ManagedObjectRepresentation> getAllDevicesUser2() {
        List<ManagedObjectRepresentation> morList = new ArrayList<>();
        InventoryFilter filter = new InventoryFilter().byFragmentType(IsDevice.class);
        log.info("Retrieving devices using user Platform");
        platformApi.getInventoryApi().getManagedObjectsByFilter(filter).get().allPages().forEach(mor -> {
            log.info("Found device {} with id {}", mor.getName(), mor.getId().getValue());
            morList.add(mor);
        });
        return morList;
    }

    /** !! Not Working !! Get all Devices using the contextService with User Credentials **/
    public List<ManagedObjectRepresentation> getAllDevicesUser3() {
        List<ManagedObjectRepresentation> morList = new ArrayList<>();
        InventoryFilter filter = new InventoryFilter().byFragmentType(IsDevice.class);
        log.info("Retrieving devices using contextService with User Credentials");
        userContextService.runWithinContext(userContextService.getContext(), () -> {
            inventoryApi.getManagedObjectsByFilter(filter).get().allPages().forEach(mor -> {
                log.info("Found device {} with id {}", mor.getName(), mor.getId().getValue());
                morList.add(mor);
            });
        });
        return morList;
    }

    /** !! Not Working !! Get all Devices a user scoped inventory API **/
    public List<ManagedObjectRepresentation> getAllDevicesUser4() {
        List<ManagedObjectRepresentation> morList = new ArrayList<>();
        InventoryFilter filter = new InventoryFilter().byFragmentType(IsDevice.class);
        log.info("Retrieving devices using user scoped Inventory API");
        scopedUserInventoryApi(platform).getManagedObjectsByFilter(filter).get().allPages().forEach(mor -> {
            log.info("Found device {} with id {}", mor.getName(), mor.getId().getValue());
            morList.add(mor);
        });
        return morList;
    }

    /** Get all Devices assuming context is already given either by RestController or Event Listener **/
    public List<ManagedObjectRepresentation> getAllDevicesTenant() {
        List<ManagedObjectRepresentation> morList = new ArrayList<>();
        InventoryFilter filter = new InventoryFilter().byFragmentType(IsDevice.class);
        log.info("Retrieving devices using service user from Rest Controller");
        inventoryApi.getManagedObjectsByFilter(filter).get().allPages().forEach(mor -> {
            log.info("Found device {} with id {}", mor.getName(), mor.getId().getValue());
            morList.add(mor);
        });
        return morList;
    }

    /**  Get all Devices using the subscriptionService and service user for each tenant **/
    public List<ManagedObjectRepresentation> getAllDevicesTenant2() {
        List<ManagedObjectRepresentation> morList = new ArrayList<>();
        InventoryFilter filter = new InventoryFilter().byFragmentType(IsDevice.class);
        log.info("Retrieving devices using service user from Subscription Service");
        subscriptionsService.runForEachTenant(() -> {
            inventoryApi.getManagedObjectsByFilter(filter).get().allPages().forEach(mor -> {
                log.info("Found device {} with id {}", mor.getName(), mor.getId().getValue());
                morList.add(mor);
            });
        });
        return morList;
    }


}
