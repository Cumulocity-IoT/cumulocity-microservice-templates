package com.c8y.ms.templates.agent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;

import c8y.Configuration;

/**
 * Service handles specific microservice configurations defined in MicroserviceProperties.
 *
 * @author alexander.pester@softwareag.com
 * @version 2.2.0
 * <p>
 * 15.08.2019
 */
@Service
public class MicroserviceConfigurationService {
    private static final Logger LOG = LoggerFactory.getLogger(MicroserviceConfigurationService.class);

    private InventoryApi inventoryApi;

    private MicroserviceConfigurationProperties properties;

    private GId microserviceAgentRepresentation;

    public MicroserviceConfigurationService(InventoryApi inventoryApi, Environment env) {
        this.inventoryApi = inventoryApi;
        this.properties = new MicroserviceConfigurationProperties();
        for (MicroserviceProperties microserviceProperty : MicroserviceProperties.values()) {
            this.properties.putProperty(microserviceProperty.getKey(), env.getProperty(microserviceProperty.getKey()));
        }
    }

    public Configuration createConfigurationByProperties() {
        Configuration configuration = new Configuration();
        configuration.setConfig(properties.toConfigString());
        return configuration;
    }

    private Configuration getCurrentConfiguration() {
        ManagedObjectRepresentation currentMicroserviceRepresentation = null;
        try {
            currentMicroserviceRepresentation = inventoryApi.get(microserviceAgentRepresentation);
        } catch (Exception ex) {
            LOG.error("Could not load current configuration, due to not existings microservice representation!", ex);
            return null;
        }

        if (currentMicroserviceRepresentation == null) {
            return null;
        }

        return (Configuration) currentMicroserviceRepresentation.get("c8y_Configuration");
    }


    public String[] getPropertyValues(MicroserviceProperties microserviceProperty) {
        loadConfiguration();
        String propertyValues = properties.getPropertyValue(microserviceProperty.getKey());
        if (propertyValues != null) {
            return propertyValues.split(",");
        }
        return null;
    }

    public String getPropertyValue(MicroserviceProperties microserviceProperty) {
        loadConfiguration();
        return properties.getPropertyValue(microserviceProperty.getKey());
    }

    public String getPropertyValue(String key) {
        loadConfiguration();
        return properties.getPropertyValue(key);
    }

    public void setMicroserviceAgentRepresentation(GId microserviceAgentRepresentation) {
        this.microserviceAgentRepresentation = microserviceAgentRepresentation;
    }

    public boolean updateProperties(String configString) {
        properties.clearProperties();
        properties.putProperties(configString);
        return updateProperties();
    }

    public void loadConfiguration() {
        //overwrite all env properties by C8y_Configuration
        Configuration currentConfiguration = getCurrentConfiguration();
        if (currentConfiguration != null) {
            properties.putProperties(currentConfiguration.getConfig());
        }
    }

    private boolean updateProperties() {
        ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();
        managedObjectRepresentation.setId(microserviceAgentRepresentation);
        Configuration configuration = new Configuration(properties.toConfigString());
        managedObjectRepresentation.set(configuration);
        try {
            inventoryApi.update(managedObjectRepresentation);
            return true;
        } catch (Exception ex) {
            LOG.error("", ex);
        }
        return false;
    }
}
