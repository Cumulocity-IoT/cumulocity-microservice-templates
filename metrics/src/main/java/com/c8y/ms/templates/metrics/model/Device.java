package com.c8y.ms.templates.metrics.model;

import java.util.Objects;

import jakarta.validation.constraints.NotEmpty;

public class Device {

	@NotEmpty
	private String name;

	@NotEmpty
	private String type;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final Device device = (Device) o;
		return Objects.equals(name, device.name) &&
				Objects.equals(type, device.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, type);
	}

	@Override
	public String toString() {
		return "Device{" +
				"name='" + name + '\'' +
				", type='" + type + '\'' +
				'}';
	}
}
