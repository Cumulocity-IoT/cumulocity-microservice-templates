package com.c8y.ms.templates.context.controller;

import com.c8y.ms.templates.context.service.DeviceService;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller to demonstrate Tenant Context & User Context in Microservices
 *
 * @author SWIT
 */
@RestController
@RequestMapping("/devices")
public class DeviceController {

    @Autowired
    DeviceService deviceService;

    /**
     * Retrieves all devices with the User Context using qualified userInventoryApi Bean
     *
     * @return
     */
    @GetMapping(path = "/devicesUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ManagedObjectRepresentation>> getAllDevicesUser() {
        List<ManagedObjectRepresentation> response = deviceService.getAllDevicesUser();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all devices with the User Context using userPlatform Bean
     *
     * @return
     */
    @GetMapping(path = "/devicesUser2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ManagedObjectRepresentation>> getAllDevicesUser2() {
        List<ManagedObjectRepresentation> response = deviceService.getAllDevicesUser2();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all devices using context Service with User Credentials
     *
     * @return
     */
    @GetMapping(path = "/devicesUser3", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ManagedObjectRepresentation>> getAllDevicesUser3() {
        List<ManagedObjectRepresentation> response = deviceService.getAllDevicesUser3();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all devices using a scoped inventory API
     *
     * @return
     */
    @GetMapping(path = "/devicesUser4", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ManagedObjectRepresentation>> getAllDevicesUser4() {
        List<ManagedObjectRepresentation> response = deviceService.getAllDevicesUser4();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all devices with the Tenant Context (Service User) using default tenant context of RestController
     *
     * @return
     */
    @GetMapping(path = "/devicesTenant", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ManagedObjectRepresentation>> getAllDevicesTenant() {
        List<ManagedObjectRepresentation> response = deviceService.getAllDevicesTenant();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all devices with the Tenant Context (Service User) using explicitly MicroserviceSubscriptionService
     *
     * @return
     */
    @GetMapping(path = "/devicesTenant2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ManagedObjectRepresentation>> getAllDeviceTenant2() {
        List<ManagedObjectRepresentation> response = deviceService.getAllDevicesTenant2();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
