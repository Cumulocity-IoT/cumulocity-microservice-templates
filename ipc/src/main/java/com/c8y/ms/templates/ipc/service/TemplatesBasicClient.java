package com.c8y.ms.templates.ipc.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "basic", url = "https://localhost:8080/service/templates-basic", configuration = TemplatesBasicClientConfig.class)
public interface TemplatesBasicClient {
	
	@RequestMapping(method = RequestMethod.GET, value = "/devices/names")
	List<String> getDeviceNames();
}
