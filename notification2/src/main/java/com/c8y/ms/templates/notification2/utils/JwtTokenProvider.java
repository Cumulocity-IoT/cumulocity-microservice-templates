package com.c8y.ms.templates.notification2.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.c8y.ms.templates.notification2.service.NotificationService;

public class JwtTokenProvider {
	private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

	private final JwtTokenProviderListener jwtTokenChangeListener;

	private final NotificationService notificationService;

	public JwtTokenProvider(JwtTokenProviderListener jwtTokenChangeListener, NotificationService notificationService) {
		this.jwtTokenChangeListener = jwtTokenChangeListener;
		this.notificationService = notificationService;
	}

	public void start() {
		String jwtTokenString = notificationService.createTokenForConsumer();
		JwtToken jwtToken = new JwtToken(jwtTokenString);
		jwtTokenChangeListener.jwtTokenChange(jwtToken);
		setupNewTimerTask(jwtToken);
	}

	private void setupNewTimerTask(JwtToken jwtToken) {
		Timer timer = new Timer("Timer");
		log.debug("IssueAt: {}, Expiration: {}, CurrentDate: {}", jwtToken.getIssuedAtDateTime(),
				jwtToken.getExpirationTimeDateTime(), ZonedDateTime.now(ZoneId.of("GMT")));

		int buffer = 10000; // 10s
		long delay = ((jwtToken.getExpirationTime() * 1000) - System.currentTimeMillis()) / 2;
		long delayWithBuffer = delay + buffer;
		log.info("Next jwtToken request in {} seconds", delayWithBuffer / 1000);

		TimerTask task = new TimerTask() {
			public void run() {
				try {
					start();
				} catch (Exception e) {
					// log.error("Failed to publish message {}", jwtMqttTopics.getUat(), e);
				}
			}
		};
		timer.schedule(task, delayWithBuffer);
	}

}
