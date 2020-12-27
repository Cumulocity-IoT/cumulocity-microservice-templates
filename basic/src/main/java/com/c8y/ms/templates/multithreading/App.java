package com.c8y.ms.templates.multithreading;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MicroserviceApplication
@EnableScheduling
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}