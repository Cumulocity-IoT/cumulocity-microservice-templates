package com.sag.c8y.template.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cumulocity.model.ID;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.sdk.client.identity.IdentityApi;

@Service
public class IdentityApiCachedService {

	private static final Logger LOG = LoggerFactory.getLogger(IdentityApiCachedService.class);
	
	private IdentityApi identityApi;
	
	@Autowired
	public IdentityApiCachedService(IdentityApi identityApi) {
		this.identityApi = identityApi;
	}
	
	@Cacheable(value="externalIds", key="{#type, #externalId}")
	public ExternalIDRepresentation getIdentity(String type, String externalId) {
		LOG.info("Load ExternalId of: {} {}", type, externalId); 
		try {
			ExternalIDRepresentation externalIDRepresentation = this.identityApi.getExternalId(new ID(type, externalId));			
			if(externalIDRepresentation == null) {
				LOG.warn("ExternalId not found");
				return null;
			}
			LOG.info("ExternalId found");
			return externalIDRepresentation;
		}catch(Exception ex) {
			LOG.error("", ex);
			return null;
		}
	}
}
