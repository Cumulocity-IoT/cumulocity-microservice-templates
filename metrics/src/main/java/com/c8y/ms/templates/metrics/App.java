package com.c8y.ms.templates.metrics;

import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;

@MicroserviceApplication
@EnableScheduling
public class App {
	public static void main(final String[] args) {
		SpringApplication.run(App.class, args);
	}
}