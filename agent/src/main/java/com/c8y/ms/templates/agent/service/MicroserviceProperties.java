package com.c8y.ms.templates.agent.service;

/**
 * Contains all specific property keys which are configurable during runtime.
 *
 * @author alexander.pester@cumulocity.com
 * @version 2.2.0
 * <p>
 * 15.08.2019
 */
public enum MicroserviceProperties {

    MS_CONFIG_PROPERTY("ms.config.property"), MS_CONFIG_PROPERTY_LIST("ms.config.property.list");

    private String key;

    private MicroserviceProperties(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
