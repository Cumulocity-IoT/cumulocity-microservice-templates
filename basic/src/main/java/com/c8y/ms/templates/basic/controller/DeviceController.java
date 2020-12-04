package com.c8y.ms.templates.basic.controller;

import java.util.List;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.c8y.ms.templates.basic.service.C8YDeviceService;

/**
 * This is an example controller. This should be removed for your real project!
 *
 * @author APES
 */
@RestController
@RequestMapping("/devices")
public class DeviceController {

    private C8YDeviceService deviceService;

    public DeviceController(C8YDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping(path = "/names", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAllDeviceNames() {
        List<String> response = deviceService.getAllDeviceNames();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/{deviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDeviceRepresentationById(final @PathVariable String deviceId) {
        final ManagedObjectRepresentation deviceRepresentation = deviceService.getDeviceRepresentation(deviceId);
        return new ResponseEntity<>(deviceRepresentation.toJSON(), HttpStatus.OK);
    }
}
