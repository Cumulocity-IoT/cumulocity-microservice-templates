package com.c8y.ms.templates.ipc.service;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cumulocity.microservice.api.CumulocityClientProperties;
import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.UserCredentials;

import feign.RequestInterceptor;

@Configuration
public class TemplatesBasicClientConfig {

	private final ContextService<UserCredentials> userContextService;
	
    private final CumulocityClientProperties clientProperties;

	@Autowired
	public TemplatesBasicClientConfig(ContextService<UserCredentials> userContextService, CumulocityClientProperties clientProperties) {
		this.userContextService = userContextService;
		this.clientProperties = clientProperties;
	}

	@Bean
	public RequestInterceptor requestInterceptor() {
		return requestTemplate -> {
			String tenant = userContextService.getContext().getTenant();
			String username = userContextService.getContext().getUsername();
			String password = userContextService.getContext().getPassword();
			
			
			String credentials = tenant + "/" + username + ":" + password;
			String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());
			requestTemplate.header("Authorization", "Basic " + base64Credentials);
	    	String url = clientProperties.getBaseURL() + "/service/templates-basic";
			requestTemplate.target(url);
		};
	}
}
