package com.c8y.ms.templates.context.service;

import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeasurementService {

    @Autowired
    @Qualifier("userMeasurementApi")
    MeasurementApi userMeasurementApi;

    @Autowired
    MeasurementApi tenantMeasurementApi;

    @Autowired
    MicroserviceSubscriptionsService subscriptionsService;

    public List<MeasurementRepresentation> getMeasurementsUser() {
        return userMeasurementApi.getMeasurements().get().getMeasurements();
    }

    public List<MeasurementRepresentation> getMeasurementsTenant() {
        return tenantMeasurementApi.getMeasurements().get().getMeasurements();
    }
}
