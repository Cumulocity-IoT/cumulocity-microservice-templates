package com.c8y.ms.templates.ipc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c8y.ms.templates.ipc.service.DeviceService;

/**
 * This is an example controller. This should be removed for your real project!
 *
 * @author APES
 */
@RestController
@RequestMapping("/devices")
public class DeviceController {

    private DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping(path = "/names", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAllDeviceNames() {
        List<String> response = deviceService.getAllDeviceNames();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
