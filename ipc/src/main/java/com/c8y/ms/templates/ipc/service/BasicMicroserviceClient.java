package com.c8y.ms.templates.ipc.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("basic")
public interface BasicMicroserviceClient {
	
	@RequestMapping(method = RequestMethod.GET, value = "/devices/names")
	List<String> getDeviceNames();
}
