package com.c8y.ms.templates.ehcache;

import org.springframework.boot.SpringApplication;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import com.cumulocity.microservice.context.annotation.EnableContextSupport;

/**
 * Starter class of template microservice. For starting localy please set environment variable C8Y_MICROSERVICE_ISOLATION=MULTI_TENANT
 *  
 * @author alexander.pester@softwareag.com
 * @version 0.0.1
 *
 * 07.08.2018
 */
@MicroserviceApplication
@EnableContextSupport
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}