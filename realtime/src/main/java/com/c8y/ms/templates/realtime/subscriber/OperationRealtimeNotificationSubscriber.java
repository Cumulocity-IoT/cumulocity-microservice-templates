package com.c8y.ms.templates.realtime.subscriber;

import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.notification.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperationRealtimeNotificationSubscriber implements Subscriber<String, OperationNotification> {
    private static final Logger LOG = LoggerFactory.getLogger(OperationRealtimeNotificationSubscriber.class);

    private static final String REALTIME_NOTIFICATIONS_URL = "cep/realtime";

    private final Subscriber<String, OperationNotification> subscriber;

    private static final String channelPrefix = "/operations/";

    public OperationRealtimeNotificationSubscriber(PlatformParameters parameters) {
        subscriber = createSubscriber(parameters);
    }

    private Subscriber<String, OperationNotification> createSubscriber(PlatformParameters parameters) {
        return SubscriberBuilder.<String, OperationNotification>anSubscriber()
                .withParameters(parameters)
                .withEndpoint(REALTIME_NOTIFICATIONS_URL)
                .withSubscriptionNameResolver(new Identity())
                .withDataType(OperationNotification.class)
                .build();
    }

    public Subscription<String> subscribe(final String channelID, final SubscriptionListener<String, OperationNotification> handler)
            throws SDKException {
        return subscriber.subscribe(channelPrefix + channelID, handler);
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
    public Subscription<String> subscribe(String arg0, SubscribeOperationListener arg1, SubscriptionListener<String, OperationNotification> arg2,
                                          boolean arg3) throws SDKException {
        return null;
    }
}