package com.c8y.ms.templates.basic;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import org.springframework.boot.SpringApplication;

@MicroserviceApplication
public class App {
    public static void main (String[] args) {
        SpringApplication.run(App.class, args);
    }
}