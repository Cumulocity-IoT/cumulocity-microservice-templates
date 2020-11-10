package com.sag.c8y.template.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;

@Service
public class InventoryApiCachedService {
	private static final Logger LOG = LoggerFactory.getLogger(InventoryApiCachedService.class);
	
	private InventoryApi inventoryApi;
	
	public InventoryApiCachedService(InventoryApi inventoryApi) {
		super();
		this.inventoryApi = inventoryApi;
	}
	
	@Cacheable("managedObjects")
	public ManagedObjectRepresentation get(String id) {
		LOG.info("load ManagedObject of: {}", id); 
		try {
			ManagedObjectRepresentation managedObjectRepresentation = inventoryApi.get(GId.asGId(id));
			if(managedObjectRepresentation == null) {
				LOG.info("ManagedObject not found!");
				return null;
			}
			LOG.info("ManagedObject found");
			return managedObjectRepresentation;
		}catch(Exception ex) {
			LOG.error("", ex);
			return null;
		}
	}
	
	@CacheEvict(value="managedObjects", allEntries=true)
	public void clearCache() {}
	
	@CacheEvict(value = "managedObjects", key = "#id")
	public void evictSingleCacheValue(String id) {}
}
