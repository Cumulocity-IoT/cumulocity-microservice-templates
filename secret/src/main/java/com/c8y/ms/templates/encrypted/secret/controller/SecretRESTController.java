package com.c8y.ms.templates.encrypted.secret.controller;

import com.c8y.ms.templates.encrypted.secret.service.SecretService;
import com.cumulocity.microservice.settings.service.MicroserviceSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cumulocity.model.option.OptionPK;
import com.cumulocity.rest.representation.tenant.OptionRepresentation;
import com.cumulocity.sdk.client.option.TenantOptionApi;

@RestController
@RequestMapping("/secret")
public class SecretRESTController {

	private SecretService secretService;

	public SecretRESTController(SecretService secretService) {
		this.secretService = secretService;
	}

	@PostMapping(path = "/{key}", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> postSecret(@PathVariable String key, @RequestBody String secret) throws Exception {
		OptionRepresentation option = secretService.storeSecret(key, secret);
		return new ResponseEntity<String>("Option " + option.getKey() + " successfully stored!", HttpStatus.OK);
	}

	@GetMapping(path = "/{key}", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getSecret(@PathVariable String key) throws Exception {
		String secret = secretService.getSecret(key);
		return new ResponseEntity<String>(secret, HttpStatus.OK);
	}

	/**
	 * When microservice runs hosted, the option is being sent to the microservice, the “credentials.” prefix is removed and the value is decrypted
	 */
	@GetMapping(path = "/header", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getServerKey(@RequestHeader(value = "secret") String secret) throws Exception {
		return new ResponseEntity<String>(secret, HttpStatus.OK);
	}
}
