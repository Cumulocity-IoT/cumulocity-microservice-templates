package com.c8y.ms.templates.notification2.utils;

public class JwtTokenPayload {
	private String jti;
	private String iss;
	private String aud;
	private String sub;
	private String tci;
	private Long iat;
	private Long nbf;
	private Long exp;
	private Boolean tfa;
	private String ten;
	private String xsrfToken;
	private String topic;

	public String getTopic() {
		return topic;
	}

	public String getJti() {
		return jti;
	}

	public String getIss() {
		return iss;
	}

	public String getAud() {
		return aud;
	}

	public String getSub() {
		return sub;
	}

	public String getTci() {
		return tci;
	}

	public Long getIat() {
		return iat;
	}

	public Long getNbf() {
		return nbf;
	}

	public Long getExp() {
		return exp;
	}

	public Boolean getTfa() {
		return tfa;
	}

	public String getTen() {
		return ten;
	}

	public String getXsrfToken() {
		return xsrfToken;
	}

}
