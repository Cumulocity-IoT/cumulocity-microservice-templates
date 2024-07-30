package com.c8y.ms.templates.notification2.utils;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
public class StandardWebSocketClientConfig {

	@Bean
	public StandardWebSocketClient client() {
		WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
		webSocketContainer.setAsyncSendTimeout(-1);
		webSocketContainer.setDefaultMaxSessionIdleTimeout(-1);
		return new StandardWebSocketClient(webSocketContainer);
	}

}
