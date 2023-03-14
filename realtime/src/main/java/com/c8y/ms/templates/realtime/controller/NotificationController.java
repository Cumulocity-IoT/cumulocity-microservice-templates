package com.c8y.ms.templates.realtime.controller;

import com.c8y.ms.templates.realtime.service.OperationService;
import com.c8y.ms.templates.realtime.subscriber.MeasurementNotification;
import com.c8y.ms.templates.realtime.subscriber.MeasurementRealtimeNotificationSubscriber;
import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.model.JSONBase;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.sdk.client.RestConnector;
import com.cumulocity.sdk.client.cep.notification.InventoryRealtimeDeleteAwareNotificationsSubscriber;
import com.cumulocity.sdk.client.cep.notification.ManagedObjectDeleteAwareNotification;
import com.cumulocity.sdk.client.devicecontrol.notification.OperationNotificationSubscriber;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.svenson.JSON;
import org.svenson.JSONParser;

@Controller
public class NotificationController {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationController.class);
    /**
     * device id is stored in the application.properties as a property
     */
    @Value("${device.id}")
    private String deviceId;

    @Autowired
    private ContextService<MicroserviceCredentials> contextService;

    private MicroserviceCredentials microserviceCredentials;

    @Autowired
    private MicroserviceSubscriptionsService subscriptions;

    @Autowired
    private OperationService operationService;

    @Autowired
       private RestConnector restConnector;

    private Subscription<String> subscription;

    private Subscription<String> measurementSubscription;

    private Subscription<GId> operationSubscription;

    private final JSONParser jsonParser = JSONBase.getJSONParser();

    private final JSON json = JSON.defaultJSON();

    /**
     * This method will be triggered during the initial start of the Microservice
     * when all subscriptions are queried and during runtime when a tenant
     * subscribes for this particular Microservice.
     *
     * @param event contains information about the tenant which subscribed for this Microservice
     */
    @EventListener
    private void onMicroserviceSubscriptionAddedEvent(final MicroserviceSubscriptionAddedEvent event) {
        microserviceCredentials = contextService.getContext();

        // execute business logic in the context of a particular tenant
        subscriptions.runForTenant(event.getCredentials().getTenant(), () -> {
            PlatformParameters platform = restConnector.getPlatformParameters();
            registerForDeviceUpdates(platform);
            registerForMeasurementUpdates(platform);
            registerForOperations(platform);
        });
    }

    /**
     * Subscribe for device updates. In this case the subscription is done for a
     * specific device but using the wildcard character * you can subscribe for
     * updates of all managed objects. For subscription the built-in class
     * InventoryRealtimeDeleteAwareNotificationsSubscriber is being used.
     */
    private void registerForDeviceUpdates(PlatformParameters platform) {
        InventoryRealtimeDeleteAwareNotificationsSubscriber subscriber = new InventoryRealtimeDeleteAwareNotificationsSubscriber(platform);
        this.subscription = subscriber.subscribe(deviceId, new SubscriptionListener<>() {
            @Override
            public void onNotification(Subscription<String> arg0, ManagedObjectDeleteAwareNotification notification) {
                // the payload differs depending if it's a delete or an update notification
                if ("DELETE".equals(notification.getRealtimeAction())) {
                    String managedObjectId = (String) notification.getData();
                    LOG.info("ManagedObject with ID {} removed.", managedObjectId);
                } else {
                    final ManagedObjectRepresentation mo = jsonParser.parse(ManagedObjectRepresentation.class, json.forValue(notification.getData()));
                    LOG.info("on device update received: {}", mo.toJSON());
                }
            }

            @Override
            public void onError(Subscription<String> arg0, Throwable arg1) {
                LOG.error("An error occurred for the managedObject subscription", arg1);
            }
        });
    }

    /**
     * Subscribe for device updates. In this case the subscription is done for a
     * specific device but using the wildcard character * you can subscribe for
     * measurements of all devices. Be careful with this as this will result in a
     * lot of measurements. For subscription a custom class
     * MeasurementRealtimeNotificationSubscriber has been implemented which allows
     * to subscribe on the measurement channel to receive updates when a new
     * measurement has been created
     */
    private void registerForMeasurementUpdates(PlatformParameters platform) {
        final MeasurementRealtimeNotificationSubscriber subscriber = new MeasurementRealtimeNotificationSubscriber(platform);
        measurementSubscription = subscriber.subscribe(deviceId, new SubscriptionListener<>() {
            @Override
            public void onNotification(Subscription<String> arg0, MeasurementNotification notification) {
                final MeasurementRepresentation measurement = jsonParser.parse(MeasurementRepresentation.class, json.forValue(notification.getData()));
                LOG.info("new measurement received: {}", measurement.toJSON());
            }

            @Override
            public void onError(Subscription<String> arg0, Throwable arg1) {
                LOG.error("An error occurred for the measurement subscription", arg1);
            }
        });
    }

    /**
     * Listen to operations which are being sent for the device. Only operations for specified device id
     * will be received.
     */
    private void registerForOperations(PlatformParameters platform) {
        final OperationNotificationSubscriber subscriber = new OperationNotificationSubscriber(platform);
        operationSubscription = subscriber.subscribe(new GId(deviceId), new SubscriptionListener<>() {
            @Override
            public void onNotification(Subscription<GId> subscription, OperationRepresentation operationRepresentation) {
                LOG.info("New operation received: {}", operationRepresentation.toJSON());

                /*
                    Realtime notification are received in a separate thread. Therefore, if for example an operation
                    should be updated you need to provide the context for which tenant the update should be executed.
                    In this case it's done by using the runWithinContext method of the ContextService<MicroserviceCredentials>
                    class
                 */
                contextService.runWithinContext(microserviceCredentials, () -> {
                    try {
                        operationService.updateOperationToExecuting(operationRepresentation);

                        // small delay to simulate an update of the status of the received operation from Executing to Successful
                        Thread.sleep(2500);

                        operationService.updateOperationToSuccessful(operationRepresentation);
                    } catch (InterruptedException e) {
                        LOG.error("An error occurred while processing the operation", e);
                    }
                });
            }

            @Override
            public void onError(Subscription<GId> subscription, Throwable throwable) {
                LOG.error("An error occurred while listening for operations: ", throwable);
            }
        });
    }
}
