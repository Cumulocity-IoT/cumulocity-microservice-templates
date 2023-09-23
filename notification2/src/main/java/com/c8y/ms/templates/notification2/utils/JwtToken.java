package com.c8y.ms.templates.notification2.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtToken {
  private static final Logger log = LoggerFactory.getLogger(JwtToken.class);

  private static final ObjectMapper mapper = new ObjectMapper();

  private String jwtToken;
  private JwtTokenPayload jwtTokenPayload;

  public JwtToken(String jwtToken) {
    super();
    this.jwtToken = jwtToken;
    String payload = new String(Base64.decodeBase64(jwtToken.split("\\.")[1]));
    try {
      jwtTokenPayload = mapper.readValue(payload, JwtTokenPayload.class);
    } catch (Exception e) {
      log.error("Reading JWT payload failed!", e);
    }
  }

  public String getUsername() {
    return jwtTokenPayload.getSub();
  }

  public String getTenant() {
    return jwtTokenPayload.getTen();
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
  
  public Long getNotBefore() {
    return jwtTokenPayload.getNbf();
  }

  public ZonedDateTime getNotBeforeDateTime() {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(jwtTokenPayload.getNbf() * 1000), ZoneId.of("GMT"));
  }
  
  public String getJwtToken() {
    return jwtToken;
  }

  public JwtTokenPayload getJwtTokenPayload() {
    return jwtTokenPayload;
  }

}
