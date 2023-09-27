package com.c8y.ms.templates.notification2.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NotificationJwt {
  private static final Logger log = LoggerFactory.getLogger(NotificationJwt.class);

  private static final ObjectMapper mapper = new ObjectMapper();

  private String jwt;
  private NotificationJwtPayload jwtTokenPayload;

  public NotificationJwt(String jwt) {
    super();
    this.jwt = jwt;
    String payload = new String(Base64.decodeBase64(jwt.split("\\.")[1]));
    try {
      jwtTokenPayload = mapper.readValue(payload, NotificationJwtPayload.class);
    } catch (Exception e) {
      log.error("Reading JWT payload failed!", e);
    }
  }

  public String getConsumerName() {
    return jwtTokenPayload.getSub();
  }

  public String getTopic() {
    return jwtTokenPayload.getTopic();
  }

  public Long getExpirationTime() {
    return jwtTokenPayload.getExp();
  }
  
  public ZonedDateTime getExpirationTimeDateTime() {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(jwtTokenPayload.getExp() * 1000), ZoneId.of("GMT"));
  }

  public Long getIssuedAt() {
    return jwtTokenPayload.getIat();
  }
  
  public ZonedDateTime getIssuedAtDateTime() {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(jwtTokenPayload.getIat() * 1000), ZoneId.of("GMT"));
  }
  
  public String toString() {
    return jwt;
  }

  public NotificationJwtPayload getJwtTokenPayload() {
    return jwtTokenPayload;
  }

}
