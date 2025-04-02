package com.c8y.ms.templates.basic.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.c8y.ms.templates.basic.service.MeasurementService;
import com.c8y.ms.templates.basic.utils.RevertQueryParameter;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.cumulocity.sdk.client.measurement.MeasurementCollection;
import com.cumulocity.sdk.client.measurement.MeasurementFilter;
import com.cumulocity.sdk.client.measurement.PagedMeasurementCollectionRepresentation;
import com.google.common.base.Optional;

public class MeasurementServiceTest {

	private MeasurementService measurementService;

	@Mock
	private MeasurementApi measurementApi;

	@Mock
	private MeasurementCollection measureColl;

	@Mock
	private Iterable<MeasurementRepresentation> measurementObjects;

	@Mock
	private PagedMeasurementCollectionRepresentation pagedMeasureColl;

	@Mock
	private MeasurementRepresentation measurementRep;

	Iterator<MeasurementRepresentation> mockIterator = mock(Iterator.class);

	ArrayList<MeasurementRepresentation> spyList = Mockito.spy(ArrayList.class);

	@BeforeEach
	void setUp() {

		MockitoAnnotations.openMocks(this);
		measurementService = new MeasurementService(measurementApi);
	}

	@Test
	void testGetLatestMeasurement() throws Exception {

		// Mock data
		String deviceId = "123";
		MeasurementRepresentation mockMeasurement1 = new MeasurementRepresentation();
		mockMeasurement1.setId(new GId("12345"));
		mockMeasurement1.setType("TestMeasurement");
		mockMeasurement1.setDateTime(new DateTime());
		spyList.add(mockMeasurement1);

		MeasurementRepresentation mockMeasurement2 = new MeasurementRepresentation();
		mockMeasurement2.setId(new GId("123456"));
		mockMeasurement2.setType("TestMeasurement");
		mockMeasurement2.setDateTime(new DateTime());
		spyList.add(mockMeasurement2);

		Mockito.doReturn(2).when(spyList).size();
		when(mockIterator.hasNext()).thenReturn(true, true, false);
		when(mockIterator.next()).thenReturn(mockMeasurement1, mockMeasurement2);
		when(measurementObjects.iterator()).thenReturn(mockIterator);

		// Mock the MeasurementApi response
		when(measurementApi.getMeasurementsByFilter(any())).thenReturn(measureColl);
		when(measureColl.get(2000, RevertQueryParameter.getInstance())).thenReturn(pagedMeasureColl);
		when(pagedMeasureColl.getMeasurements()).thenReturn(spyList);

		// Execute the method
		Optional<MeasurementRepresentation> result = measurementService.getLatestMeasurement(deviceId);

		// Verify that the result is present and contains the expected measurement
		assertTrue(result.isPresent());
		assertEquals(mockMeasurement1, result.get());
	}

}
