package se.phooey.raining.weather;

import java.time.Clock;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import se.phooey.raining.weather.exception.RainReportException;
import tk.plogitech.darksky.api.jackson.DarkSkyJacksonClient;
import tk.plogitech.darksky.forecast.APIKey;
import tk.plogitech.darksky.forecast.ForecastException;
import tk.plogitech.darksky.forecast.ForecastRequest;
import tk.plogitech.darksky.forecast.ForecastRequestBuilder;
import tk.plogitech.darksky.forecast.ForecastRequestBuilder.Block;
import tk.plogitech.darksky.forecast.ForecastRequestBuilder.Language;
import tk.plogitech.darksky.forecast.ForecastRequestBuilder.Units;
import tk.plogitech.darksky.forecast.GeoCoordinates;
import tk.plogitech.darksky.forecast.model.Currently;
import tk.plogitech.darksky.forecast.model.Daily;
import tk.plogitech.darksky.forecast.model.DailyDataPoint;
import tk.plogitech.darksky.forecast.model.Forecast;
import tk.plogitech.darksky.forecast.model.Latitude;
import tk.plogitech.darksky.forecast.model.Longitude;

/**
 * Implementation of {@link WeatherProvider} using the Dark Sky API to retrieve
 * a weather report for the requested location and parsing the information in it
 * to create a {@link RainReport}. <br>
 * <br>
 * Uses the {@link tk.plogitech.darksky.api.jackson.DarkSkyJacksonClient
 * DarkSkyJacksonClient} to retrieve data from the Dark Sky API.<br>
 * <br>
 * Free Dark Sky API calls are limited to 1000 per day, which is automatically
 * enforced by the DarkSkyWeatherProvider, and after 1000 API calls in one day
 * (according to the provided Clock), a RainReportException will be thrown until
 * the next day.
 * 
 * @see tk.plogitech.darksky.api.jackson.DarkSkyJacksonClient
 * @see <a href=
 *      "https://github.com/200Puls/darksky-forecast-api">https://github.com/200Puls/darksky-forecast-api</a>
 */
public class DarkSkyWeatherProvider implements WeatherProvider {

	private static final String REQUIRED_URL_APPENDAGE = "##key##/##latitude##,##longitude####time##";
	private static final int MAXIMUM_API_CALLS_PER_DAY = 1000;

	private final APIKey apiKey;
	private final DarkSkyJacksonClient client;
	private final String url;
	private final Logger logger;
	private final Clock clock;
	private final AtomicLong apiCallsMadeToday;
	private final AtomicLong timeOfLastApiCall;

	private boolean wasToday(long timeInMilliseconds) {
		Date then = new Date(timeInMilliseconds);
		Date now = new Date(clock.millis());
		return DateUtils.isSameDay(then, now);
	}

	private void countApiCall() throws ForecastException {
		long lastApiCall = this.timeOfLastApiCall.get();
		if (!wasToday(lastApiCall)) {
			this.logger.debug("Resetting Dark Sky API call counter.");
			apiCallsMadeToday.set(0);
		}
		long apiCallsToday = apiCallsMadeToday.incrementAndGet();
		this.logger.debug("Dark Sky API call number {} today.", apiCallsToday);
		this.timeOfLastApiCall.set(clock.millis());
		if (apiCallsToday >= MAXIMUM_API_CALLS_PER_DAY) {
			throw new ForecastException(
					String.format("Too many calls to the Dark Sky API in one day, call number %d (Maximum: %d)",
							apiCallsToday, MAXIMUM_API_CALLS_PER_DAY));
		}
	}

	private static Precipitation getPrecipitationForPrecipType(String precipType) {
		Precipitation result;
		switch (precipType) {
		case "rain":
			result = Precipitation.RAIN;
			break;
		case "sleet":
			result = Precipitation.SLEET;
			break;
		case "snow":
			result = Precipitation.SNOW;
			break;
		default:
			result = Precipitation.UNKNOWN;
			break;
		}
		return result;
	}

	private void populateFromCurrently(Optional<Currently> currentForecast, RainReport report) {
		Currently currently;
		if (!currentForecast.isPresent()) {
			return;
		}
		currently = currentForecast.get();
		double precipProbability = Optional.ofNullable(currently.getPrecipProbability()).orElse(-1.0);
		report.setCurrentProbability(precipProbability);
		if (precipProbability == 0.0) {
			report.setCurrentIntensity(0.0);
			report.setCurrentPrecipitation(Precipitation.NONE.toString());
			return;
		}
		double precipIntensity = Optional.ofNullable(currently.getPrecipIntensity()).orElse(-1.0);
		report.setCurrentIntensity(precipIntensity);
		String precipType = Optional.ofNullable(currently.getPrecipType()).orElse("");
		report.setCurrentPrecipitation(getPrecipitationForPrecipType(precipType).toString());
	}

	private void populateFromDaily(Optional<Daily> dailyForecast, RainReport report) {
		if (!dailyForecast.isPresent()) {
			return;
		}
		Daily daily = dailyForecast.get();
		List<DailyDataPoint> dailyData = daily.getData();
		if (CollectionUtils.isEmpty(dailyData)) {
			return;
		}
		DailyDataPoint today = dailyData.get(0);
		double precipProbability = Optional.ofNullable(today.getPrecipProbability()).orElse(-1.0);
		report.setChanceOfPrecipitationToday(precipProbability);
		if (precipProbability == 0.0) {
			report.setTypeOfPrecipitationToday(Precipitation.NONE.toString());
			return;
		}
		String precipType = Optional.ofNullable(today.getPrecipType()).orElse("");
		report.setTypeOfPrecipitationToday(getPrecipitationForPrecipType(precipType).toString());
	}

	/**
	 * Creates a new DarkSkyWeatherProvider
	 * 
	 * @param apiKey the API key to use when making requests to the Dark Sky API
	 * @param apiUrl the URL to use when making Dark Sky API requests
	 * @param client The DarkSkyJacksonClient to use to make Dark Sky API requests
	 * @param clock  A Clock to use to determine the time when counting API calls
	 *               made in a day
	 */
	@Autowired
	public DarkSkyWeatherProvider(APIKey apiKey, String apiUrl, DarkSkyJacksonClient client, Clock clock) {
		this.apiKey = apiKey;
		this.client = client;
		this.url = apiUrl + REQUIRED_URL_APPENDAGE;
		this.clock = clock;
		this.logger = LoggerFactory.getLogger(DarkSkyWeatherProvider.class);
		this.apiCallsMadeToday = new AtomicLong(0);
		this.timeOfLastApiCall = new AtomicLong(0);
	}

	public RainReport isItRainingAtCoordinates(double latitude, double longitude) throws RainReportException {
		try {
			logger.info("Retrieving weather report from the Dark Sky API for coordinates {}, {}", latitude, longitude);
			countApiCall();
			ForecastRequest request = new ForecastRequestBuilder().key(this.apiKey).url(this.url)
					.location(new GeoCoordinates(new Longitude(longitude), new Latitude(latitude)))
					.exclude(Block.hourly).exclude(Block.minutely).language(Language.en).units(Units.si).build();
			Forecast forecast = Optional.ofNullable(client.forecast(request))
					.orElseThrow(() -> new ForecastException("Forecast is null"));
			RainReport result = new RainReport();
			result.setLatitude(latitude);
			result.setLongitude(longitude);
			populateFromCurrently(Optional.ofNullable(forecast.getCurrently()), result);
			populateFromDaily(Optional.ofNullable(forecast.getDaily()), result);
			return result;
		} catch (IllegalArgumentException | ForecastException e) {
			logger.error(e.getMessage());
			throw new RainReportException(String.format(Locale.US,
					"Could not generate a RainReport for coordinates %f, %f", latitude, longitude));
		}
	}

}
