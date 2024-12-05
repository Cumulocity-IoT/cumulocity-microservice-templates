package com.c8y.ms.templates.metrics.controller;

import com.c8y.ms.templates.metrics.model.Device;
import com.c8y.ms.templates.metrics.service.DeviceService;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.google.common.base.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createNewDevice(final @RequestBody @Valid Device device) {
        final Optional<ManagedObjectRepresentation> deviceCreated = deviceService.createDevice(device);

        if (!deviceCreated.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(deviceCreated.get().toJSON(), HttpStatus.OK);
    }
}
