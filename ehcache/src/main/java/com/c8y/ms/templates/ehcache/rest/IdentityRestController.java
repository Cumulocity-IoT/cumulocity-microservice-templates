package com.c8y.ms.templates.ehcache.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cumulocity.sdk.client.identity.IdentityApi;

@RestController
@RequestMapping("/identity")
public class IdentityRestController {

	//externalIds/{{externalIdType}}/{{externalId}}
	
	private IdentityApi identityApi;
	
	@Autowired
	public IdentityRestController(IdentityApi identityApi) {
		this.identityApi = identityApi;
	}
}
