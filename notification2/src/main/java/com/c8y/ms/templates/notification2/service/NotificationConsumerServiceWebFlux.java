package com.c8y.ms.templates.notification2.service;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.StandardWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.c8y.ms.templates.notification2.utils.NotificationJwt;
import com.c8y.ms.templates.notification2.utils.NotificationJwtProviderListener;
import com.cumulocity.microservice.api.CumulocityClientProperties;

import reactor.core.publisher.Mono;

@Service
public class NotificationConsumerServiceWebFlux implements NotificationJwtProviderListener, WebSocketHandler {
	private static final Logger log = LoggerFactory.getLogger(NotificationConsumerServiceWebFlux.class);

	private final static String WEBSOCKET_URL_PATTERN = "%s/notification2/consumer/?token=%s";
	
	
	private WebSocketClient webSocketClient;
	private String jwt;
	
	private final CumulocityClientProperties clientProperties;

	public NotificationConsumerServiceWebFlux(CumulocityClientProperties clientProperties) {
		super();
		this.clientProperties = clientProperties;
		this.webSocketClient = new StandardWebSocketClient();
	}
	
	
	public void connect(String jwt) {
		this.jwt = jwt;
		try {
			Mono<Void> mono = webSocketClient.execute(getWebSocketUrl(jwt), this);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		
	}

	public String getJwt() {
		return jwt;
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


	@Override
	public Mono<Void> handle(WebSocketSession session) {
		session.receive().log();
		return session.close();
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
}
