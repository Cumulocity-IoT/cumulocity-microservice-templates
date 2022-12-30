package com.c8y.ms.templates.metrics.service;

import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.c8y.ms.templates.metrics.config.MicrometerConfig;
import com.c8y.ms.templates.metrics.utils.RevertQueryParameter;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.cumulocity.sdk.client.measurement.MeasurementFilter;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

import io.micrometer.core.annotation.Timed;

@Service
public class MeasurementService {
	private static final Logger LOG = LoggerFactory.getLogger(MeasurementService.class);

	private final MeasurementApi measurementApi;

	public MeasurementService(final MeasurementApi measurementApi) {
		this.measurementApi = measurementApi;
	}

	/**
	 * Recording time with @Timed annotation. Requires setup of TimedAspect-Bean in {@link MicrometerConfig}.
	 * <hr/>
	 * Query the latest measurement for a specific device. This requires to set the
	 * date and also to provide the query parameter "revert=true". For the revert
	 * query parameter a custom class RevertQueryParameter has been implemented
	 * which is used on the list. If you want to query for a specific type you need
	 * to add the type as a filter to the MeasurementFilter
	 *
	 * @param deviceId id of device for which the latest measurement should be queried
	 * @return the latest measurement as MeasurementRepresentation
	 */
	@Timed(value = "measurements.latest.request")
	public Optional<MeasurementRepresentation> getLatestMeasurement(final String deviceId) {
		if (Strings.isNullOrEmpty(deviceId)) {
			return Optional.absent();
		}

		final DateTime dateFrom = new DateTime(2020, 1, 1, 0, 0);
		final DateTime dateNow = new DateTime();
		final MeasurementFilter filter = new MeasurementFilter().bySource(new GId(deviceId)).byDate(dateFrom.toDate(),
				dateNow.toDate());

		try {
			final List<MeasurementRepresentation> measurements = measurementApi.getMeasurementsByFilter(filter)
					.get(2000, RevertQueryParameter.getInstance()).getMeasurements();

			if (measurements == null || measurements.isEmpty()) {
				return Optional.absent();
			}

			return Optional.of(measurements.get(0));
		} catch (final SDKException exception) {
			LOG.error("Error while loading measurements for device", exception);
		}

		return Optional.absent();
	}
}
