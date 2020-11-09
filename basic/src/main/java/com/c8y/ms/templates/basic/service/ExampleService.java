package com.c8y.ms.templates.basic.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import com.cumulocity.sdk.client.inventory.ManagedObjectCollection;
import com.google.common.collect.Lists;

/**
 * This is an example service. This should be removed for your real project!
 * 
 * @author APES
 *
 */
@Service
public class ExampleService {
	
	private InventoryApi inventoryApi;

	@Autowired
	public ExampleService(InventoryApi inventoryApi) {
		this.inventoryApi = inventoryApi;
	}
	
	public List<String> getAllDeviceNames() {
		List<String> allDeviceNames = new ArrayList<>();
		
		InventoryFilter inventoryFilter = new InventoryFilter();
		inventoryFilter.byFragmentType("c8y_IsDevice");
		ManagedObjectCollection managedObjectsByFilter = inventoryApi.getManagedObjectsByFilter(inventoryFilter);
		List<ManagedObjectRepresentation> allObjects = Lists.newArrayList( managedObjectsByFilter.get().allPages());
		for (ManagedObjectRepresentation managedObjectRepresentation : allObjects) {
			allDeviceNames.add(managedObjectRepresentation.getName());
		}
		return allDeviceNames;
	}
	
}
