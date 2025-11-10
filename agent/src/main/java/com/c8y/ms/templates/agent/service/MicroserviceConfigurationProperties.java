package com.c8y.ms.templates.agent.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instance of this class contains all properties during runtime. Properties can be set by configString from C8y_Configuration element or directly by key & value.
 *
 * @author alexander.pester@cumulocity.com
 * @version 2.2.0
 * <p>
 * 15.08.2019
 */
public class MicroserviceConfigurationProperties {
    private static final Logger LOG = LoggerFactory.getLogger(MicroserviceConfigurationProperties.class);

    private Properties properties;

    public MicroserviceConfigurationProperties() {
        properties = new Properties();
    }

    public String toConfigString() {
        StringWriter writer = new StringWriter();
        try {
            properties.store(new PrintWriter(writer), null);
        } catch (IOException e) {
            LOG.error("", e);
        }
        return writer.getBuffer().toString();
    }

    public void putProperties(String configString) {
        try {
            properties.load(new StringReader(configString));
        } catch (IOException e) {
            LOG.error("", e);
        }
    }

    public void clearProperties() {
        properties.clear();
    }

    public void putProperty(String key, String value) {
        properties.put(key, value);
    }

    public String getPropertyValue(String key) {
        return (String) properties.get(key);
    }

    @Override
    public String toString() {
        return properties.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MicroserviceConfigurationProperties other = (MicroserviceConfigurationProperties) obj;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        return true;
    }
}