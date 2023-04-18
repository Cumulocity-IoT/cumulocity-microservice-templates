package com.c8y.ms.templates.ipc.service;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cumulocity.microservice.api.CumulocityClientProperties;
import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.context.credentials.UserCredentials;

import feign.RequestInterceptor;

@Configuration
public class TemplatesBasicClientConfig {

	private final Logger log = LoggerFactory.getLogger(TemplatesBasicClient.class);

	private final ContextService<UserCredentials> userContextService;

	private final ContextService<MicroserviceCredentials> microserviceContextService;

	private final CumulocityClientProperties clientProperties;
	
	private final String microserviceName = "templates-basic";

	@Autowired
	public TemplatesBasicClientConfig(ContextService<UserCredentials> userContextService,
			ContextService<MicroserviceCredentials> microserviceContextService,
			CumulocityClientProperties clientProperties) {
		this.userContextService = userContextService;
		this.microserviceContextService = microserviceContextService;
		this.clientProperties = clientProperties;
	}

	@Bean
	public RequestInterceptor requestInterceptor() {
		return requestTemplate -> {

			String tenant;
			String username;
			String password;

			// Use user credentials if available otherwise use microservice credentials
			if (userContextService.isInContext()) {
				log.info("User context!!!");
				tenant = userContextService.getContext().getTenant();
				username = userContextService.getContext().getUsername();
				password = userContextService.getContext().getPassword();
			} else {
				log.info("Microservice context!!!");
				tenant = microserviceContextService.getContext().getTenant();
				username = microserviceContextService.getContext().getUsername();
				password = microserviceContextService.getContext().getPassword();
			}

			String credentials = tenant + "/" + username + ":" + password;
			String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());
			requestTemplate.header("Authorization", "Basic " + base64Credentials);
			String url = clientProperties.getBaseURL() + "/service/" + microserviceName;
			requestTemplate.target(url);
		};
	}
}
