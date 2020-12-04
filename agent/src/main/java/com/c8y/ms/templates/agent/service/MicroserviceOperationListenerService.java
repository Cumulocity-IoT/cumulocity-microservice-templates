package com.c8y.ms.templates.agent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import c8y.Configuration;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.notification.Subscriber;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;

/**
 * Operation listener, consumes "c8y_Configuration" operation
 *
 * @author alexander.pester@softwareag.com
 * @version 0.0.1
 */
@Service
public class MicroserviceOperationListenerService {

    private static final Logger logger = LoggerFactory.getLogger(MicroserviceOperationListenerService.class);

    private DeviceControlApi controlApi;

    private MicroserviceSubscriptionsService subscriptions;

    private MicroserviceConfigurationService configService;

    public MicroserviceOperationListenerService(DeviceControlApi controlApi,
                                                MicroserviceSubscriptionsService subscriptions, MicroserviceConfigurationService configService) {
        super();
        this.controlApi = controlApi;
        this.subscriptions = subscriptions;
        this.configService = configService;
    }

    public void registerForOperations(GId agentId) {
        Subscriber<GId, OperationRepresentation> subscriber = controlApi.getNotificationsSubscriber();
        OperationListener<GId, OperationRepresentation> operationListener = new OperationListener<>();
        subscriber.subscribe(agentId, operationListener);
        logger.info("Agent {} is subscribed for Operations", agentId);
    }

    public class OperationListener<GId, OperationRepresentation> implements SubscriptionListener<GId, OperationRepresentation> {

        @Override
        public void onError(Subscription<GId> arg0, Throwable arg1) {
            logger.error("", arg1);
        }

        @Override
        public void onNotification(Subscription<GId> sub, OperationRepresentation operation) {
            com.cumulocity.rest.representation.operation.OperationRepresentation op = (com.cumulocity.rest.representation.operation.OperationRepresentation) operation;
            com.cumulocity.model.idtype.GId gid = (com.cumulocity.model.idtype.GId) sub.getObject();
            logger.info("Operation received: {}", op);
            logger.info("Subscription: " + gid);

            Configuration config = (Configuration) op.get("c8y_Configuration");
            if (config != null) {
                subscriptions.runForEachTenant(() -> {
                    try {
                        //check if operation exists in this tenant
                        if (controlApi.getOperation(op.getId()) != null) {

                            op.setStatus(OperationStatus.EXECUTING.toString());
                            controlApi.update(op);

                            if (configService.updateProperties(config.getConfig()))
                                op.setStatus(OperationStatus.SUCCESSFUL.toString());
                            else
                                op.setStatus(OperationStatus.FAILED.toString());

                            controlApi.update(op);
                        }
                    } catch (Throwable e) {
                        logger.error("Error on Operation notification:", e);
                    }
                });
            }
        }
    }
}
