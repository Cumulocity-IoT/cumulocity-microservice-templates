package com.c8y.ms.templates.notification2.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import com.c8y.ms.templates.notification2.utils.Notification;
import com.c8y.ms.templates.notification2.utils.NotificationJwt;
import com.c8y.ms.templates.notification2.utils.NotificationJwtProviderListener;
import com.c8y.ms.templates.notification2.utils.NotificationWebSocketHandler;
import com.cumulocity.microservice.api.CumulocityClientProperties;

@Service
public class NotificationConsumerService extends NotificationWebSocketHandler implements NotificationJwtProviderListener {
	private static final Logger log = LoggerFactory.getLogger(NotificationConsumerService.class);

	private final static String WEBSOCKET_URL_PATTERN = "%s/notification2/consumer/?token=%s";

	private StandardWebSocketClient webSocketClient;
	
	private final CumulocityClientProperties clientProperties;

	//Microservice has state
	private WebSocketConnectionManager webSocketConnectionManager;
	private String jwt;
	
	@Autowired
	public NotificationConsumerService(StandardWebSocketClient webSocketClient, CumulocityClientProperties clientProperties) {
		super();
		this.webSocketClient = webSocketClient;
		this.clientProperties = clientProperties;
	}
	
	@Override
	protected void handleNotification(WebSocketSession session, Notification notification) {
		log.info(notification.getMessage());
	}

	public void connect(String jwt) throws InterruptedException, ExecutionException, URISyntaxException {
		this.jwt = jwt;
		if(webSocketConnectionManager != null) {
			webSocketConnectionManager.stop();
		}
		webSocketConnectionManager = createWebSocketConnectionManager(jwt);
		webSocketConnectionManager.start();
	}
	
	public void disconnect() {
		this.webSocketConnectionManager.stop();
	}

	public String getJwt() {
		return jwt;
	}

	@Override
	public void jwtTokenChange(NotificationJwt jwtToken) {
		log.debug("JWT retrieved: {}", jwtToken.toString());
		try {
			connect(jwtToken.toString());
		} catch (Exception e) {
			log.error("Consumer connection failed!", e);
		}
	}
	
	private URI getWebSocketUrl(String token) throws URISyntaxException {
		String httpBaseUrl = clientProperties.getBaseURL();
		
		String wssBaseUrl;
		if(httpBaseUrl.startsWith("https:")) {
			 wssBaseUrl = "wss" + httpBaseUrl.substring(httpBaseUrl.indexOf(":"));
		}else {
			wssBaseUrl = "ws" + httpBaseUrl.substring(httpBaseUrl.indexOf(":"));
		}
		return new URI(String.format(WEBSOCKET_URL_PATTERN, wssBaseUrl, token));
	}
	
	private WebSocketConnectionManager createWebSocketConnectionManager(String token) {
		URI serverUri;
		try {
			serverUri = getWebSocketUrl(token);
	        WebSocketConnectionManager manager = new WebSocketConnectionManager(this.webSocketClient, this, serverUri.toString());
	        return manager;
		} catch (URISyntaxException e) {
			log.error("", e);
		}
		return null;
	}




}
