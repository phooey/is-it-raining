package se.phooey.raining.weather;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import se.phooey.raining.weather.exception.RainReportException;

/**
 * A weather provider provides a rain report for a requested location represented by
 * geographic coordinates
 * 
 * @see se.phooey.raining.weather.RainReport
 */
@Component
public interface WeatherProvider {

	/**
	 * Returns a {@link RainReport} for the requested location
	 * 
	 * @param latitude the latitude for the requested location
	 * @param longitude the longitude for the requested location
	 * @return A {@link RainReport} for the provided location
	 * @throws RainReportException if a RainReport could not be generated
	 */
	public @NonNull RainReport isItRainingAtCoordinates(double latitude, double longitude) throws RainReportException;
}
