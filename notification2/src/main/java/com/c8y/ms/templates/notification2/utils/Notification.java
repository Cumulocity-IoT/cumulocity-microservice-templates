package com.c8y.ms.templates.notification2.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Notification {
	private final String acknowledgeHeader;
	private final List<String> notificationHeaders;
	private final String message;

	private Notification(String acknowledgeHeader, List<String> notificationHeaders, String message) {
		this.acknowledgeHeader = acknowledgeHeader;
		this.notificationHeaders = notificationHeaders;
		this.message = message;
	}

	public static Notification parse(String message) {
		ArrayList<String> headers = new ArrayList<>(8);
		while (true) {
			int i = message.indexOf('\n');
			if (i == -1) {
				break;
			}
			String header = message.substring(0, i);
			message = message.substring(i + 1);
			if (header.length() == 0) {
				break;
			}
			headers.add(header);
		}
		if (headers.isEmpty()) {
			return new Notification(null, Collections.emptyList(), message);
		}
		return new Notification(headers.get(0), Collections.unmodifiableList(headers.subList(1, headers.size())),
				message);
	}

	public String getAcknowledgeHeader() {
		return acknowledgeHeader;
	}

	public List<String> getNotificationHeaders() {
		return notificationHeaders;
	}

	public String getMessage() {
		return message;
	}

}
