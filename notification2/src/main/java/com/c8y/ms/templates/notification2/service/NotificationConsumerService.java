package com.c8y.ms.templates.notification2.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Service
public class NotificationConsumerService extends TextWebSocketHandler {
	private static final Logger log = LoggerFactory.getLogger(NotificationConsumerService.class);

	private final static String WEBSOCKET_URL_PATTERN = "%s/notification2/consumer/?token=%s";

	private StandardWebSocketClient webSocketClient;

	@Autowired
	public NotificationConsumerService() {
		super();
		this.webSocketClient = new StandardWebSocketClient();
	}
	
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println(message.getPayload());
    }

	public void connect(String jwt) throws InterruptedException, ExecutionException, URISyntaxException {
		WebSocketSession clientSession = webSocketClient.doHandshake(this, new WebSocketHttpHeaders(), getWebSocketUrl(jwt)).get();
	}

	private URI getWebSocketUrl(String token) throws URISyntaxException {
		return new URI(String.format(WEBSOCKET_URL_PATTERN, System.getenv("C8Y_BASEURL_WEBSOCKET"), token));
	}
}
