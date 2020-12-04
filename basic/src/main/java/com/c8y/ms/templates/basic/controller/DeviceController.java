package com.c8y.ms.templates.basic.controller;

import com.c8y.ms.templates.basic.service.DeviceService;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.google.common.base.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This is an example controller. This should be removed for your real project!
 *
 * @author APES
 */
@RestController
@RequestMapping("/devices")
public class DeviceController {

    private DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping(path = "/names", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAllDeviceNames() {
        List<String> response = deviceService.getAllDeviceNames();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/{deviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDeviceRepresentationById(final @PathVariable String deviceId) {
        final Optional<ManagedObjectRepresentation> deviceRepresentationOptional = deviceService.getDeviceRepresentation(deviceId);

        if (!deviceRepresentationOptional.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(deviceRepresentationOptional.get().toJSON(), HttpStatus.OK);
    }
}
