package com.c8y.ms.templates.context.controller;

import com.c8y.ms.templates.context.service.AlarmService;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
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
@RequestMapping("/alarms")
public class AlarmController {

    @Autowired
    AlarmService alarmService;

    /**
     * Retrieves first page of alarms with the User Context using qualified userAlarmsApi Bean
     *
     * @return
     */
    @GetMapping(path = "/alarmsUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AlarmRepresentation>> getMeasurementsUser() {
        List<AlarmRepresentation> response = alarmService.getAlarmsUser();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves first page of measurements with the Tenant Context (Service User) using default tenant context of RestController
     *
     * @return
     */
    @GetMapping(path = "/alarmsTenant", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AlarmRepresentation>> getEventsTenant() {
        List<AlarmRepresentation> response = alarmService.getAlarmsTenant();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
