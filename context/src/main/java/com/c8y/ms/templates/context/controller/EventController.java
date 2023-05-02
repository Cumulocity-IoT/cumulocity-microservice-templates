package com.c8y.ms.templates.context.controller;

import com.c8y.ms.templates.context.service.EventService;
import com.cumulocity.rest.representation.event.EventRepresentation;
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
@RequestMapping("/events")
public class EventController {

    @Autowired
    EventService eventService;

    /**
     * Retrieves first page of events with the User Context using qualified userEventApi Bean
     *
     * @return
     */
    @GetMapping(path = "/eventsUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EventRepresentation>> getEventsUser() {
        List<EventRepresentation> response = eventService.getEventsUser();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves first page of events with the Tenant Context (Service User) using default tenant context of RestController
     *
     * @return
     */
    @GetMapping(path = "/eventsTenant", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EventRepresentation>> getEventsTenant() {
        List<EventRepresentation> response = eventService.getEventsTenant();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
