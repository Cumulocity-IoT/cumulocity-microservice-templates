package com.c8y.ms.templates.notification2.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.c8y.ms.templates.notification2.utils.Notification;
import com.c8y.ms.templates.notification2.utils.NotificationWebSocketHandler;
import com.cumulocity.rest.representation.reliable.notification.NotificationSubscriptionFilterRepresentation;
import com.cumulocity.rest.representation.reliable.notification.NotificationSubscriptionRepresentation;
import com.cumulocity.rest.representation.reliable.notification.NotificationTokenRequestRepresentation;
import com.cumulocity.sdk.client.messaging.notifications.NotificationSubscriptionApi;
import com.cumulocity.sdk.client.messaging.notifications.NotificationSubscriptionCollection;
import com.cumulocity.sdk.client.messaging.notifications.NotificationSubscriptionFilter;
import com.cumulocity.sdk.client.messaging.notifications.PagedNotificationSubscriptionCollectionRepresentation;
import com.cumulocity.sdk.client.messaging.notifications.Token;
import com.cumulocity.sdk.client.messaging.notifications.TokenApi;
import com.google.common.collect.Lists;

@Service
public class NotificationService {

	private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
	
	private static final String SUBSCRIPTION_CONTEXT = "tenant";
	private static final String SUBSCRIPTION_NAME = "SubscriptionServiceRequest";
	private static final String SUBSCRIPTION_CONSUMER = "ServiceRequestConsumer1";
	private static final List<String> SUBSCRIPTION_APIS = Lists.newArrayList("events");
	private static final String SUBSCRIPTION_TYPE_FILTER = "'cy8_ServiceRequest'";
	
	private NotificationSubscriptionApi notificationSubscriptionApi;
	private TokenApi tokenApi;

	@Autowired
	public NotificationService(NotificationSubscriptionApi notificationSubscriptionApi, TokenApi tokenApi) {
		super();
		this.notificationSubscriptionApi = notificationSubscriptionApi;
		this.tokenApi = tokenApi;
	}
	
	public void setupSubscriptions() {
		NotificationSubscriptionRepresentation subscription = getSubscription();
		if(subscription != null) {
			log.info("Subscription already created! {}", subscription.getId().getValue());
			return;
		}
		
		NotificationSubscriptionRepresentation notificationSubscription = new NotificationSubscriptionRepresentation();
		notificationSubscription.setContext(SUBSCRIPTION_CONTEXT);
		notificationSubscription.setSubscription(SUBSCRIPTION_NAME);
		
		NotificationSubscriptionFilterRepresentation subscriptionFilter = new NotificationSubscriptionFilterRepresentation();
		subscriptionFilter.setApis(SUBSCRIPTION_APIS);
		subscriptionFilter.setTypeFilter(SUBSCRIPTION_TYPE_FILTER);
		
		notificationSubscription.setSubscriptionFilter(subscriptionFilter);
		
		
		NotificationSubscriptionRepresentation newSubscription = this.notificationSubscriptionApi.subscribe(notificationSubscription);
		
		log.info("Subscription done! {}", newSubscription.getId().getValue());
	}
	
	public String createTokenForConsumer() {
		NotificationTokenRequestRepresentation tokenRequest = new NotificationTokenRequestRepresentation();
		tokenRequest.setSubscription(SUBSCRIPTION_NAME);
		tokenRequest.setSubscriber(SUBSCRIPTION_CONSUMER);
		Token newToken = tokenApi.create(tokenRequest);
		return newToken.getTokenString();
	}
	
	public void unsubscribeToken(String token) {
		tokenApi.unsubscribe(new Token(token));
	}
	
	public void deleteSubscriptions() {
		NotificationSubscriptionRepresentation subscription = getSubscription();
		this.notificationSubscriptionApi.delete(subscription);		
	}
	
	public NotificationSubscriptionRepresentation getSubscription() {
		NotificationSubscriptionFilter filter = new NotificationSubscriptionFilter();
		filter.bySubscription(SUBSCRIPTION_NAME);
		
		NotificationSubscriptionCollection subscriptionCollection = this.notificationSubscriptionApi.getSubscriptionsByFilter(filter);
		PagedNotificationSubscriptionCollectionRepresentation page = subscriptionCollection.get(1);
		List<NotificationSubscriptionRepresentation> subscriptions = page.getSubscriptions();
		
		for (NotificationSubscriptionRepresentation subscription : subscriptions) {
			return subscription;
		}
	
		return null;
	}
	
}
