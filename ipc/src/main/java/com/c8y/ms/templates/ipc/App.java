package com.c8y.ms.templates.ipc;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;

@MicroserviceApplication
@EnableFeignClients
@EnableScheduling
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}