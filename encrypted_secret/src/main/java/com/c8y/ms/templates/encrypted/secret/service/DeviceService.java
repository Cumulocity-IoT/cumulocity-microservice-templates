package com.c8y.ms.templates.encrypted.secret.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;

@Service
public class DeviceService {
	
	private InventoryApi inventoryApi;

	@Autowired
	public DeviceService(InventoryApi inventoryApi) {
		this.inventoryApi = inventoryApi;
	}
	
	public String getDeviceName(String id) {
		GId gid = new GId(id);
		ManagedObjectRepresentation managedObjectRepresentation = inventoryApi.get(gid);
		return managedObjectRepresentation.getName();
	}
	
}
