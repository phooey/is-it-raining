package se.phooey.raining.web;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import se.phooey.raining.weather.RainReport;
import se.phooey.raining.weather.WeatherProvider;
import se.phooey.raining.weather.exception.RainReportException;
import se.phooey.raining.web.exception.InvalidCoordinatesException;

/**
 * {@link RestController} providing a simple REST API to retrieve a rain report for a geographic location
 */
@RestController
public class IsItRainingController {

	private WeatherProvider weatherProvider;

	private void validateCoordinates(double latitude, double longitude) throws InvalidCoordinatesException {
		if ((latitude > 90) || (latitude < -90) || (longitude > 180) || (longitude < -180)) {
			throw new InvalidCoordinatesException(
					"Coordinates need to be in range: -90 <= latitude <= 90, -180 <= longitude <= 180.");
		}
	}

	private double truncateDoubleToThreeDecimalPoints(double value) {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
		DecimalFormat df = new DecimalFormat("#.###", symbols);
		return Double.parseDouble(df.format(value));
	}

	/**
	 * Creates a new IsItRainingController
	 * 
	 * @param weatherProvider The {@link WeatherProvider} to use to generate the {@link RainReport}s
	 */
	@Autowired
	public IsItRainingController(WeatherProvider weatherProvider) {
		this.weatherProvider = weatherProvider;
	}

	/**
	 * Generates and returns a rain report for a requested geographic location
	 * 
	 * @param latitude the geographic latitude of the requested location
	 * @param longitude the geographic longitude of the requested location
	 * @return {@link RainReport} for the specified location
	 * @throws InvalidCoordinatesException If the specified coordinates are invalid
	 * @throws RainReportException If a RainReport could not be generated
	 */
	@GetMapping("/isitraining")
	public RainReport isItRaining(@RequestParam(value = "latitude") double latitude,
			@RequestParam(value = "longitude") double longitude) throws InvalidCoordinatesException, RainReportException {
		validateCoordinates(latitude, longitude);
		latitude = truncateDoubleToThreeDecimalPoints(latitude);
		longitude = truncateDoubleToThreeDecimalPoints(longitude);
		return weatherProvider.isItRainingAtCoordinates(latitude, longitude);
	}

}
