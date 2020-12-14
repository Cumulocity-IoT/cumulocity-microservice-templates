package com.c8y.ms.templates.encrypted.secret.service;

import com.cumulocity.microservice.settings.service.MicroserviceSettingsService;
import com.cumulocity.model.option.OptionPK;
import com.cumulocity.rest.representation.tenant.OptionRepresentation;
import com.cumulocity.sdk.client.option.TenantOptionApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SecretService {
    /**
     * Info: Adding “credentials.” prefix to the key will make the value of the option to be encrypted.
     * Additionally, when the option is being sent to the microservice, the “credentials.” prefix is removed and the value is decrypted.
     */
    private static final String SECRET_KEY = "credentials.";

    @Value("${application.name}")
    protected String applicationName;

    private TenantOptionApi tenantOptionApi;

    /**
     * Checkout for more details:
     * https://cumulocity.com/guides/microservice-sdk/java
     *
     * chapter: Microservice settings
     */
    private MicroserviceSettingsService microserviceSettingsService;

    public SecretService(TenantOptionApi tenantOptionApi, MicroserviceSettingsService microserviceSettingsService) {
        this.tenantOptionApi = tenantOptionApi;
        this.microserviceSettingsService = microserviceSettingsService;
    }

    public OptionRepresentation storeSecret(String key, String secret) {
        OptionRepresentation option = new OptionRepresentation();
        option.setCategory(applicationName);
        option.setKey(SECRET_KEY + key);
        option.setValue(secret);
        return tenantOptionApi.save(option);
    }

    /**
     * For microservice settings you should use MicroserviceSettingsService:
     * The microservice settings module provides two features:
     * <p>
     * Configure a microservice by defining tenant options
     * Override existing properties - Tenant options can override default values from properties files
     */
    public String getSecret(String key) {
        String secret = microserviceSettingsService.getCredential(key);
        return secret;
    }

    /**
     * If you want to use another category you can also use directly option api
     */
    public OptionRepresentation getSecretOption(String key) {
        OptionPK optionPK = new OptionPK();
        optionPK.setCategory(applicationName);
        optionPK.setKey(SECRET_KEY + key);
        //tenantOptionApi.getAllOptionsForCategory("templates-secret") is not going to decrypt the data, you have to use getOption
        return tenantOptionApi.getOption(optionPK);
    }
}
