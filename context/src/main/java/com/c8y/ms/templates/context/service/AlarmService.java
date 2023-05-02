package com.c8y.ms.templates.context.service;

import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlarmService {

    @Autowired
    @Qualifier("userAlarmApi")
    AlarmApi userAlarmApi;

    @Autowired
    AlarmApi tenantAlarmApi;

    @Autowired
    MicroserviceSubscriptionsService subscriptionsService;

    public List<AlarmRepresentation> getAlarmsUser() {
        return userAlarmApi.getAlarms().get().getAlarms();
    }

    public List<AlarmRepresentation> getAlarmsTenant() {
        return tenantAlarmApi.getAlarms().get().getAlarms();
    }
}
