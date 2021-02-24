package com.c8y.ms.templates.agent;

import org.springframework.boot.SpringApplication;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import com.cumulocity.microservice.context.annotation.EnableContextSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Starter class of template microservice.
 *
 * @author alexander.pester@softwareag.com
 * @version 0.0.1
 * <p>
 * 07.08.2018
 */
@MicroserviceApplication
@EnableContextSupport
@EnableScheduling
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}