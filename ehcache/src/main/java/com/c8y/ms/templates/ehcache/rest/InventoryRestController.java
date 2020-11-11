package com.c8y.ms.templates.ehcache.rest;

import com.c8y.ms.templates.ehcache.service.IdentityApiCachedService;
import com.c8y.ms.templates.ehcache.service.InventoryApiCachedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

@RestController
@RequestMapping("/inventory")
public class InventoryRestController {

	private InventoryApiCachedService inventoryApi;
	
	private IdentityApiCachedService identityApi;

	@Autowired
	public InventoryRestController(final InventoryApiCachedService inventoryApi, final IdentityApiCachedService identityApi) {
		this.inventoryApi = inventoryApi;
		this.identityApi = identityApi;
	}

	/**
	 * find managed object by id
	 * 
	 * @param deviceId
	 * @return
	 */
	@GetMapping(path = "/managedObjects/{deviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ManagedObjectRepresentation> getManagedObjectRepresentation(@PathVariable String deviceId) {
		ManagedObjectRepresentation managedObjectRepresentation = inventoryApi.get(deviceId);
		if(managedObjectRepresentation == null) {
			new ResponseEntity<DeviceInfoResponse>(HttpStatus.NOT_FOUND); 
		}

		return new ResponseEntity<ManagedObjectRepresentation>(managedObjectRepresentation, HttpStatus.OK);
	}
	
	@GetMapping(path = "/managedObjects/identity/externalIds/{externalIdType}/{externalId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ManagedObjectRepresentation> getManagedObjectRepresentationByIdentity(@PathVariable String externalIdType, @PathVariable String externalId) {
		ExternalIDRepresentation identity = identityApi.getIdentity(externalIdType, externalId);
		if(identity == null) {
			return new ResponseEntity<ManagedObjectRepresentation>(HttpStatus.NOT_FOUND); 			
		}
		
		ManagedObjectRepresentation managedObjectRepresentation = inventoryApi.get(identity.getManagedObject().getId().getValue());
		if(managedObjectRepresentation == null) {
			return new ResponseEntity<ManagedObjectRepresentation>(HttpStatus.NOT_FOUND); 
		}

		return new ResponseEntity<ManagedObjectRepresentation>(managedObjectRepresentation, HttpStatus.OK);
	}
	

}
