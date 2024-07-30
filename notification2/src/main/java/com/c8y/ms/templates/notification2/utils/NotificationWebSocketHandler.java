package com.c8y.ms.templates.notification2.utils;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public abstract class NotificationWebSocketHandler extends TextWebSocketHandler {
	private static final Logger log = LoggerFactory.getLogger(NotificationWebSocketHandler.class);
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("Connection established: {}", session.getId());
	}
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) {
		if(message == null || message.getPayloadLength() < 1) {
			handleNotification(session, null);
		}
		Notification notification = Notification.parse(message.getPayload());
		handleNotification(session, notification);
		TextMessage acknowledgeHeader = new TextMessage(notification.getAcknowledgeHeader());
		try {
			session.sendMessage(acknowledgeHeader);
		} catch (IOException e) {
			log.error("Acknowledge message failed!", e);
		}
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		log.error("Transport error: " +session.getId(), exception);
	}
	
	protected abstract void handleNotification(WebSocketSession session, Notification notification);
}
