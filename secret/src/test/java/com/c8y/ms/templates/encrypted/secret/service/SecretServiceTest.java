package com.c8y.ms.templates.encrypted.secret.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cumulocity.rest.representation.tenant.OptionRepresentation;
import com.cumulocity.sdk.client.option.TenantOptionApi;

class SecretServiceTest {

	@Mock
	private TenantOptionApi tenantOptionApi;

	private SecretService secretService;

	private static final String SECRET_KEY = "testKey";
	private static final String SECRET_VALUE = "testSecret";

	private OptionRepresentation optionRepresentation;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		secretService = new SecretService(tenantOptionApi);

		// Setup the OptionRepresentation that we will use in tests
		optionRepresentation = new OptionRepresentation();
		optionRepresentation.setCategory("templates");
		optionRepresentation.setKey("credentials." + SECRET_KEY);
		optionRepresentation.setValue(SECRET_VALUE);
	}

	@Test
	public void testStoreSecret() {
		// Arrange
		when(tenantOptionApi.save(any())).thenReturn(optionRepresentation);

		// Act
		OptionRepresentation result = secretService.storeSecret(SECRET_KEY, SECRET_VALUE);

		// Assert
		assertNotNull(result);
		assertEquals(SECRET_VALUE, result.getValue());
		assertEquals("credentials." + SECRET_KEY, result.getKey());
	}

	@Test
	public void testGetSecret() {
		// Arrange
		when(tenantOptionApi.getOption(any())).thenReturn(optionRepresentation);

		// Act
		OptionRepresentation result = secretService.getSecret(SECRET_KEY);

		// Assert
		assertNotNull(result);
		assertEquals(SECRET_VALUE, result.getValue());
		assertEquals("credentials." + SECRET_KEY, result.getKey());
	}

	@Test
	public void testGetSecret_notFound() {
		// Arrange
		when(tenantOptionApi.getOption(any())).thenReturn(null);

		// Act
		OptionRepresentation result = secretService.getSecret(SECRET_KEY);

		// Assert
		assertNull(result);
	}
}
