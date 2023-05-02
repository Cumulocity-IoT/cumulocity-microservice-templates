package com.c8y.ms.templates.context.controller;

import com.c8y.ms.templates.context.service.MeasurementService;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller to demonstrate Tenant Context & User Context in Microservices
 *
 * @author SWIT
 */
@RestController
@RequestMapping("/measurements")
public class MeasurementController {

    @Autowired
    MeasurementService measurementService;

    /**
     * Retrieves first page of measurements with the User Context using qualified userMeasurementApi Bean
     *
     * @return
     */
    @GetMapping(path = "/measurementsUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MeasurementRepresentation>> getMeasurementsUser() {
        List<MeasurementRepresentation> response = measurementService.getMeasurementsUser();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves first page of measurements with the Tenant Context (Service User) using default tenant context of RestController
     *
     * @return
     */
    @GetMapping(path = "/measurementsTenant", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MeasurementRepresentation>> getEventsTenant() {
        List<MeasurementRepresentation> response = measurementService.getMeasurementsTenant();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
