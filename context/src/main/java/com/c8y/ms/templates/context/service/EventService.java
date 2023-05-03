package com.c8y.ms.templates.context.service;

import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.sdk.client.event.EventApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    @Qualifier("userEventApi")
    EventApi userEventApi;

    @Autowired
    EventApi tenantEventApi;

    public List<EventRepresentation> getEventsUser() {
        return userEventApi.getEvents().get().getEvents();
    }

    public List<EventRepresentation> getEventsTenant() {
        return tenantEventApi.getEvents().get().getEvents();
    }
}
