package com.c8y.ms.templates.realtime.service;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import org.springframework.stereotype.Service;

@Service
public class OperationService {

    private final DeviceControlApi deviceControlApi;

    public OperationService(DeviceControlApi deviceControlApi) {
        this.deviceControlApi = deviceControlApi;
    }

    public void updateOperationToExecuting(final OperationRepresentation operation) {
        updateOperationStatus(operation, OperationStatus.EXECUTING);
    }

    public void updateOperationToSuccessful(final OperationRepresentation operation) {
        updateOperationStatus(operation, OperationStatus.SUCCESSFUL);
    }

    public void updateOperationStatus(final OperationRepresentation operation, OperationStatus operationStatus) {
        operation.setStatus(operationStatus.name());

        deviceControlApi.update(operation);
    }
}
