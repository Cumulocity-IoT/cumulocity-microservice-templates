package com.c8y.ms.templates.agent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c8y.ms.templates.agent.service.MicroserviceConfigurationService;

@RestController
@RequestMapping("/api")
public class ConfigRESTController {

	private MicroserviceConfigurationService microserviceConfigurationService;
	
	@Autowired
	public ConfigRESTController(MicroserviceConfigurationService microserviceConfigurationService) {
		this.microserviceConfigurationService = microserviceConfigurationService;
	}

	@GetMapping(path = "/config/{key:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String greeting(@PathVariable String key) {
		return microserviceConfigurationService.getPropertyValue(key);
    }
}
