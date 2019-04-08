package se.phooey.raining.weather;

/**
 * Enumeration class representing possible types of precipitation
 * and their String representations in English.
 */
public enum Precipitation {
	NONE("none"), UNKNOWN("unknown"), RAIN("rain"), SLEET("sleet"), SNOW("snow");

	private final String message;

	Precipitation(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return this.message;
	}
}
