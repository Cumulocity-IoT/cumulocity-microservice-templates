package com.c8y.ms.templates.basic.services;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c8y.ms.templates.basic.model.Device;
import com.c8y.ms.templates.basic.service.DeviceService;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.ManagedObjectCollection;
import com.cumulocity.sdk.client.inventory.PagedManagedObjectCollectionRepresentation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

import c8y.Agent;
import c8y.IsDevice;


public class DeviceServiceTest {

	private DeviceService deviceService;

	@Mock
	private InventoryApi inventoryApi;

	@Mock
	private MicroserviceSubscriptionsService subscriptions;

	@Mock
	private ManagedObjectCollection mockedCollection;

	@Mock
	private PagedManagedObjectCollectionRepresentation pagedMockedCollection;

	@Mock
	private Iterable<ManagedObjectRepresentation> managedObjects;

	@Mock
	private ManagedObjectRepresentation mormap;

	Iterator<ManagedObjectRepresentation> mockIterator = mock(Iterator.class);

	ArrayList<ManagedObjectRepresentation> spyList = Mockito.spy(ArrayList.class);

	Device device;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		deviceService = new DeviceService(inventoryApi, subscriptions);
	}

	/**
	 * Test when IdentityApi and ExternalApi works and creates Device and External
	 * ID successfully
	 */
	@DisplayName("Test createDevice when InventoryApi and IdentityApi works successfully")
	@Test
	void createDeviceSuccess() {

		device = new Device();
		device.setName("TestDeviceName001");
		device.setType("TestDeviceType");

		ManagedObjectRepresentation responseMo = new ManagedObjectRepresentation();
		responseMo.setId(new GId("123"));
		responseMo.setProperty("name", device.getName());
		responseMo.setProperty("type", device.getType());
		responseMo.set(new IsDevice());
		responseMo.set(new Agent());

		when(inventoryApi.create(any())).thenReturn(responseMo);

		Optional<ManagedObjectRepresentation> moOptional = deviceService.createDevice(device);
		assertNotNull(moOptional.get());
		ManagedObjectRepresentation mo = moOptional.get();

		assertEquals(mo.getProperty("name"), device.getName());
		assertEquals(mo.getProperty("type"), device.getType());
		assertTrue(mo.hasProperty("c8y_IsDevice"));
	}

	/**
	 * Test when InventoryApi throws an Exception
	 */
	@DisplayName("Test createDevice when InventoryApi Fails")
	@Test
	void createDeviceFail() {

		device = new Device();
		device.setName("TestDeviceName001");
		device.setType("TestDeviceType");

		when(inventoryApi.create(any())).thenThrow(new SDKException("exception"));
		Optional<ManagedObjectRepresentation> moOptional = deviceService.createDevice(device);
		assertFalse(moOptional.isPresent());
		verify(inventoryApi, times(1)).create(any());
	}

	// Test getAllDeviceNames method
	@Test
	void testGetAllDeviceNames() throws SDKException {

		ManagedObjectRepresentation device1 = new ManagedObjectRepresentation();
		device1.setName("Device123");
		device1.setType("TestDeviceType");

		ManagedObjectRepresentation device2 = new ManagedObjectRepresentation();
		device2.setName("Device456");
		device2.setType("TestDeviceType");

		Mockito.doReturn(2).when(spyList).size();

		when(mockIterator.hasNext()).thenReturn(true, true, false);
		when(mockIterator.next()).thenReturn(device1, device2);
		when(managedObjects.iterator()).thenReturn(mockIterator);

		when(inventoryApi.getManagedObjectsByFilter(any())).thenReturn(mockedCollection);
		when(mockedCollection.get(2000)).thenReturn(pagedMockedCollection);
		when(pagedMockedCollection.allPages()).thenReturn(managedObjects);

		// Call the method
		List<String> deviceNames = deviceService.getAllDeviceNames();

		// Assertions
		assertNotNull(deviceNames);
		assertEquals(2, deviceNames.size());
		assertTrue(deviceNames.contains("Device123"));
		assertTrue(deviceNames.contains("Device456"));
	}
	
	 @Test
	  public void testUpdateDevice_Success() throws JsonProcessingException{
		
        ManagedObjectRepresentation existingDevice = new ManagedObjectRepresentation();
        existingDevice.setId(new GId("123"));
        existingDevice.setName("Device123");
        existingDevice.setType("TestDeviceType");

        ManagedObjectRepresentation updatedDevice = new ManagedObjectRepresentation();
        updatedDevice.setId(new GId("123"));
        updatedDevice.setName("UpdatedName");

        // Mock the device retrieval
        when(inventoryApi.get(any(GId.class))).thenReturn(existingDevice);
        
        // Mock the update operation
        when(inventoryApi.update(any(ManagedObjectRepresentation.class))).thenReturn(updatedDevice);

        String requestJson = "{\"name\": \"UpdatedDevice\"}";  // Simulating a JSON request
        Optional<ManagedObjectRepresentation> result = deviceService.updateDevice("123", requestJson);

		// Assertions
        assertTrue(result.isPresent());
        assertEquals("UpdatedName", result.get().getName());
        verify(inventoryApi, times(1)).update(any(ManagedObjectRepresentation.class));
	 }
}
