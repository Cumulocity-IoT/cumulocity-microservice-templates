package com.c8y.ms.templates.notification2.service;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.c8y.ms.templates.notification2.utils.NotificationJwt;
import com.c8y.ms.templates.notification2.utils.NotificationJwtProvider;
import com.c8y.ms.templates.notification2.utils.NotificationJwtProviderListener;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionRemovedEvent;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

/**
 * Service for add microservice as representation in cumulocity als
 * managedobject with agent fragment in order to store events etc. to it.
 *
 * @author alexander.pester@softwareag.com
 * @version 0.0.1
 *          <p>
 *          03.09.2019
 */
@Service
public class MicroserviceRepresentationService {
	private static final Logger LOG = LoggerFactory.getLogger(MicroserviceRepresentationService.class);

	private final MicroserviceSubscriptionsService subscriptions;

	private final NotificationService notificationService;

	private final NotificationConsumerService notificationConsumerService;

	private final NotificationJwtProvider jwtProvider;

	public MicroserviceRepresentationService(MicroserviceSubscriptionsService subscriptions,
			NotificationService notificationService, NotificationConsumerService notificationConsumerService, NotificationJwtProvider jwtProvider) {
		this.subscriptions = subscriptions;
		this.notificationService = notificationService;
		this.notificationConsumerService = notificationConsumerService;
		this.jwtProvider = jwtProvider;
	}

	@EventListener
	private void onSubscriptionEvent(final MicroserviceSubscriptionAddedEvent event) {
		String subscriptTenant = event.getCredentials().getTenant();

		subscriptions.runForTenant(subscriptTenant, new Runnable() {

			@Override
			public void run() {
				LOG.info("Microservice Subscription added!");

				LOG.info("Seting up notification 2.0 ...");
				notificationService.setupSubscriptions();
				jwtProvider.start();
			}
		});
	}

	@EventListener
	private void onSubscriptionEvent(final MicroserviceSubscriptionRemovedEvent event) {
		LOG.info("Microservice Subscription removed!");
		notificationService.unsubscribeToken(notificationConsumerService.getJwt());
		notificationConsumerService.disconnect();
	}
}
