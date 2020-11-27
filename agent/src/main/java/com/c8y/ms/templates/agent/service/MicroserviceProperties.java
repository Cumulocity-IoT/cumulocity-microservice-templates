package com.c8y.ms.templates.agent.service;

/**
 * Contains all specific property keys which are configurable during runtime.
 * 
 * @author alexander.pester@softwareag.com
 * @version 2.2.0
 *
 * 15.08.2019
 */
public enum MicroserviceProperties {

	MS_TEMPLATE_NAME("ms.template.name"), MS_TEMPLATE_ID("ms.template.list");
	
	private String key;
	
	private MicroserviceProperties(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
}
