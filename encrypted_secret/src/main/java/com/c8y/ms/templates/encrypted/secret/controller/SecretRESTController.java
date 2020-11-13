package com.c8y.ms.templates.encrypted.secret.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cumulocity.model.option.OptionPK;
import com.cumulocity.rest.representation.tenant.OptionRepresentation;
import com.cumulocity.sdk.client.option.TenantOptionApi;

@RestController
@RequestMapping("/secret")
public class SecretRESTController {

	private static final String SECRET_KEY = "credentials.secret";

	private TenantOptionApi tenantOptionApi;

	@Autowired
	public SecretRESTController(TenantOptionApi tenantOptionApi) {
		this.tenantOptionApi = tenantOptionApi;
	}

	@PostMapping(path = "/store", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> postSecret(@RequestBody String secret) throws Exception {
		OptionRepresentation option = new OptionRepresentation();
		option.setCategory("template");
		option.setKey(SECRET_KEY);
		option.setValue(secret);
		tenantOptionApi.save(option);
		return new ResponseEntity<String>("Option " + option.getKey() + " successfully stored!", HttpStatus.OK);
	}

	@GetMapping(path = "/header", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getServerKey(@RequestHeader(value = "secret") String secret) throws Exception {
		return new ResponseEntity<String>(secret, HttpStatus.OK);
	}

	@GetMapping(path = "/api", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getServerKey() throws Exception {
		OptionPK optionPK = new OptionPK();
		optionPK.setCategory("template");
		optionPK.setKey(SECRET_KEY);
//		tenantOptionApi.getAllOptionsForCategory("template") is not going to decrypt the data, you have to use getOption
		OptionRepresentation option = tenantOptionApi.getOption(optionPK);
		return new ResponseEntity<String>(option.getValue(), HttpStatus.OK);
	}
}
