package com.c8y.ms.templates.encrypted.secret.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c8y.ms.templates.encrypted.secret.service.DeviceService;

@RestController
@RequestMapping("/api")
public class DeviceRESTController {

	private DeviceService deviceService;
	
	@Autowired
	public DeviceRESTController(DeviceService deviceService) {
		this.deviceService = deviceService;
	}

	@GetMapping(path = "/inventory/{deviceid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeviceInfoResponse> greeting(@PathVariable String deviceid) {
        DeviceInfoResponse response = new DeviceInfoResponse();
        response.setId(deviceid);
        response.setName(deviceService.getDeviceName(deviceid));
		return new ResponseEntity<DeviceInfoResponse>(response, HttpStatus.OK);
    }
}
