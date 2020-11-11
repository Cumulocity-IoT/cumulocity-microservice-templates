package com.c8y.ms.templates.ehcache.controller;

import com.c8y.ms.templates.ehcache.service.InventoryApiCachedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.svenson.JSON;
import org.svenson.JSONParser;

import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.model.JSONBase;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.cep.notification.InventoryRealtimeDeleteAwareNotificationsSubscriber;
import com.cumulocity.sdk.client.cep.notification.ManagedObjectDeleteAwareNotification;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;

@Component
public class NotificationController {
	private static final Logger LOG = LoggerFactory.getLogger(NotificationController.class);

	@Autowired
	private ContextService<MicroserviceCredentials> contextService;

	private MicroserviceCredentials microserviceCredentials;

	@Autowired
	private MicroserviceSubscriptionsService subscriptions;

	@Autowired
	private PlatformImpl platform;
	
	private InventoryApiCachedService inventoryApi;

	private Subscription<String> subscription;

	private final JSONParser jsonParser = JSONBase.getJSONParser();

	private final JSON json = JSON.defaultJSON();

	/**
	 * This method will be triggered during the initial start of the Microservice
	 * when all subscriptions are queried and during runtime when a tenant
	 * subscribes for this particular Microservice.
	 * 
	 * @param event
	 */
	@EventListener
	private void onMicroserviceSubscriptionAddedEvent(final MicroserviceSubscriptionAddedEvent event) {
		microserviceCredentials = contextService.getContext();

		// execute business logic in the context of a particular tenant
		subscriptions.runForTenant(event.getCredentials().getTenant(), () -> {
			registerForDeviceUpdates();
		});
	}

	/**
	 * Subscribe for device updates. In this case the subscription is done for a
	 * specific device but using the wildcard character * you can subscribe for
	 * updates of all managed objects. For subscription the built-in class
	 * InventoryRealtimeDeleteAwareNotificationsSubscriber is being used.
	 */
	private void registerForDeviceUpdates() {
		InventoryRealtimeDeleteAwareNotificationsSubscriber subscriber = new InventoryRealtimeDeleteAwareNotificationsSubscriber(
				platform);
		this.subscription = subscriber.subscribe("*",
				new SubscriptionListener<String, ManagedObjectDeleteAwareNotification>() {

					@Override
					public void onNotification(Subscription<String> arg0, ManagedObjectDeleteAwareNotification arg1) {
						final ManagedObjectRepresentation mo = (ManagedObjectRepresentation) jsonParser
								.parse(ManagedObjectRepresentation.class, json.forValue(arg1.getData()));
						//remove from cache after update in order to reload new version of managed object next time if needed
						inventoryApi.evictSingleCacheValue(mo.getId().getValue());
						LOG.info("on update received: {}", mo.toJSON());
					}

					@Override
					public void onError(Subscription<String> arg0, Throwable arg1) {
						// TODO Auto-generated method stub
					}
				});
	}
}
