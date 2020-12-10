package com.c8y.ms.templates.realtime.subscriber;

import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.notification.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MeasurementRealtimeNotificationSubscriber implements Subscriber<String, MeasurementNotification> {
    private static final Logger LOG = LoggerFactory.getLogger(MeasurementRealtimeNotificationSubscriber.class);

    private static final String REALTIME_NOTIFICATIONS_URL = "cep/realtime";

    private final Subscriber<String, MeasurementNotification> subscriber;

    private static final String CHANNEL_PREFIX = "/measurements/";

    public MeasurementRealtimeNotificationSubscriber(PlatformParameters parameters) {
        subscriber = createSubscriber(parameters);
    }

    private Subscriber<String, MeasurementNotification> createSubscriber(PlatformParameters parameters) {
        return SubscriberBuilder.<String, MeasurementNotification>anSubscriber()
                .withParameters(parameters)
                .withEndpoint(REALTIME_NOTIFICATIONS_URL)
                .withSubscriptionNameResolver(new Identity())
                .withDataType(MeasurementNotification.class)
                .build();
    }

    public Subscription<String> subscribe(final String channelID,
                                          final SubscriptionListener<String, MeasurementNotification> handler) throws SDKException {
        return subscriber.subscribe(CHANNEL_PREFIX + channelID, handler);
    }

    public void disconnect() {
        subscriber.disconnect();
    }

    private static final class Identity implements SubscriptionNameResolver<String> {
        @Override
        public String apply(String id) {
            return id;
        }
    }

    @Override
    public Subscription<String> subscribe(String arg0, SubscribeOperationListener arg1,
                                          SubscriptionListener<String, MeasurementNotification> arg2, boolean arg3) throws SDKException {
        return null;
    }
}
