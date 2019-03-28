package se.phooey.raining.weather;

/**
 * Enumeration class representing possible answers to the question
 * "Is it raining currently?", and their String representations in English.
 */
public enum IsItRaining {
	YES("Yes"), NO("No"), UNKNOWN("Unknown");

	private final String message;

	IsItRaining(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return this.message;
	}
}
