package se.phooey.raining.weather;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import se.phooey.raining.weather.exception.RainReportException;
import tk.plogitech.darksky.api.jackson.DarkSkyJacksonClient;
import tk.plogitech.darksky.forecast.APIKey;
import tk.plogitech.darksky.forecast.ForecastException;
import tk.plogitech.darksky.forecast.model.Currently;
import tk.plogitech.darksky.forecast.model.Daily;
import tk.plogitech.darksky.forecast.model.DailyDataPoint;
import tk.plogitech.darksky.forecast.model.Forecast;

/**
 * Unit tests for @see se.phooey.raining.weather.DarkSkyWeatherProvider
 */
public class DarkSkyWeatherProviderTest {

	private static final double DUMMY_LATITUDE = 48.366512;
	private static final double DUMMY_LONGITUDE = 10.894446;
	private static final String DUMMY_API_KEY = "dummykey";
	private static final String DUMMY_URL = "http://dummy.url/";

	@Mock
	private DarkSkyJacksonClient mockClient;
	@Mock
	private Forecast mockForecast;
	@Mock
	private Currently mockCurrently;
	@Mock
	private Daily mockDaily;
	@Mock
	private DailyDataPoint mockDailyDataPoint;
	@Mock
	private Clock mockClock;

	private DarkSkyWeatherProvider subject;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		subject = new DarkSkyWeatherProvider(new APIKey(DUMMY_API_KEY), DUMMY_URL, mockClient, mockClock);
		when(mockClock.millis()).thenReturn(Clock.systemUTC().millis());
	}

	// Helper method that mocks a Forecast result with the provided parameters
	private void mockForecast(boolean rainCurrently, double chanceOfRainToday) throws ForecastException {
		when(mockClient.forecast(any())).thenReturn(mockForecast);
		when(mockForecast.getCurrently()).thenReturn(mockCurrently);
		if (rainCurrently) {
			when(mockCurrently.getPrecipType()).thenReturn("rain");
			when(mockForecast.getDaily()).thenReturn(mockDaily);
		}
		List<DailyDataPoint> dailyData = new ArrayList<>();
		dailyData.add(mockDailyDataPoint);
		when(mockDaily.getData()).thenReturn(dailyData);
		when(mockDailyDataPoint.getPrecipType()).thenReturn("rain");
		when(mockDailyDataPoint.getPrecipProbability()).thenReturn(chanceOfRainToday);
	}

	@Test(expected = RainReportException.class)
	public void whenForecastThrowsForecastException_thenItShouldThrowARainReportException() throws Exception {
		when(mockClient.forecast(any())).thenThrow(ForecastException.class);

		subject.isItRainingAtCoordinates(DUMMY_LATITUDE, DUMMY_LONGITUDE);

	}

	@Test
	public void whenReturnRainReport_thenItShouldIncludeCoordinates() throws Exception {
		when(mockForecast.getCurrently()).thenReturn(null);
		when(mockClient.forecast(any())).thenReturn(mockForecast);

		RainReport result = subject.isItRainingAtCoordinates(DUMMY_LATITUDE, DUMMY_LONGITUDE);
		assertThat(result.getLatitude()).isEqualTo(DUMMY_LATITUDE);
		assertThat(result.getLongitude()).isEqualTo(DUMMY_LONGITUDE);
	}

	@Test
	public void whenNoCurrentlyInForecast_thenShouldReturnUnknown() throws Exception {
		when(mockForecast.getCurrently()).thenReturn(null);
		when(mockClient.forecast(any())).thenReturn(mockForecast);

		RainReport result = subject.isItRainingAtCoordinates(DUMMY_LATITUDE, DUMMY_LONGITUDE);
		assertThat(result.getCurrentPrecipitation()).isEqualTo(Precipitation.UNKNOWN.toString());
	}

	@Test
	public void whenNoRainInCurrently_thenShouldReturnNo() throws Exception {
		when(mockForecast.getCurrently()).thenReturn(mockCurrently);
		when(mockCurrently.getPrecipType()).thenReturn("");
		when(mockClient.forecast(any())).thenReturn(mockForecast);

		RainReport result = subject.isItRainingAtCoordinates(DUMMY_LATITUDE, DUMMY_LONGITUDE);
		assertThat(result.getCurrentPrecipitation()).isEqualTo(Precipitation.NONE.toString());
	}

	@Test
	public void whenRainInCurrently_thenShouldReturnYes() throws Exception {
		when(mockForecast.getCurrently()).thenReturn(mockCurrently);
		when(mockCurrently.getPrecipType()).thenReturn("rain");
		when(mockClient.forecast(any())).thenReturn(mockForecast);

		RainReport result = subject.isItRainingAtCoordinates(DUMMY_LATITUDE, DUMMY_LONGITUDE);
		assertThat(result.getCurrentPrecipitation()).isEqualTo(Precipitation.RAIN.toString());
	}

	@Test
	public void whenNoDailyInForecast_thenShouldReturnMinusOne() throws Exception {
		when(mockClient.forecast(any())).thenReturn(mockForecast);
		when(mockForecast.getDaily()).thenReturn(null);

		RainReport result = subject.isItRainingAtCoordinates(DUMMY_LATITUDE, DUMMY_LONGITUDE);
		assertThat(result.getChanceOfPrecipitationToday()).isEqualTo(-1);
	}

	@Test
	public void whenNoDailyDataPointInDaily_thenShouldReturnMinusOne() throws Exception {
		when(mockClient.forecast(any())).thenReturn(mockForecast);
		when(mockForecast.getDaily()).thenReturn(mockDaily);
		List<DailyDataPoint> emptyDailyData = new ArrayList<>();
		when(mockDaily.getData()).thenReturn(emptyDailyData);

		RainReport result = subject.isItRainingAtCoordinates(DUMMY_LATITUDE, DUMMY_LONGITUDE);
		assertThat(result.getChanceOfPrecipitationToday()).isEqualTo(-1);
	}

	@Test
	public void whenNoRainInDailyDataPointInDaily_thenShouldReturnZero() throws Exception {
		when(mockClient.forecast(any())).thenReturn(mockForecast);
		when(mockForecast.getDaily()).thenReturn(mockDaily);
		List<DailyDataPoint> dailyData = new ArrayList<>();
		dailyData.add(mockDailyDataPoint);
		when(mockDaily.getData()).thenReturn(dailyData);

		RainReport result = subject.isItRainingAtCoordinates(DUMMY_LATITUDE, DUMMY_LONGITUDE);
		assertThat(result.getChanceOfPrecipitationToday()).isEqualTo(0);
	}

	@Test
	public void whenRainInDailyDataPointInDaily_thenShouldReturnPrecipProbability() throws Exception {
		when(mockClient.forecast(any())).thenReturn(mockForecast);
		when(mockForecast.getDaily()).thenReturn(mockDaily);
		List<DailyDataPoint> dailyData = new ArrayList<>();
		dailyData.add(mockDailyDataPoint);
		when(mockDaily.getData()).thenReturn(dailyData);
		when(mockDailyDataPoint.getPrecipType()).thenReturn("rain");
		when(mockDailyDataPoint.getPrecipProbability()).thenReturn(0.5);

		RainReport result = subject.isItRainingAtCoordinates(DUMMY_LATITUDE, DUMMY_LONGITUDE);
		assertThat(result.getLatitude()).isEqualTo(DUMMY_LATITUDE);
		assertThat(result.getChanceOfPrecipitationToday()).isEqualTo(0.5);
	}

	@Test
	public void whenThereAreMoreThan1000ApiCallsInADay_thenItShouldNotMakeADarkSkyApiCallAndThrowARainReportException()
			throws Exception {
		mockForecast(true, 0.5);
		RainReport dummyRainReport = new RainReport();
		dummyRainReport.setLatitude(DUMMY_LATITUDE);
		dummyRainReport.setLongitude(DUMMY_LONGITUDE);
		dummyRainReport.setCurrentPrecipitation(Precipitation.RAIN.toString());
		dummyRainReport.setChanceOfPrecipitationToday(0.5);

		for (int i = 1; i <= 999; i++) {
			RainReport result = subject.isItRainingAtCoordinates(DUMMY_LATITUDE, DUMMY_LONGITUDE);
			assertThat(result).isEqualTo(dummyRainReport);
		}
		
		verify(mockClient, times(999)).forecast(any());
		verifyNoMoreInteractions(mockClient);
		try {
			subject.isItRainingAtCoordinates(DUMMY_LATITUDE, DUMMY_LONGITUDE);
			fail("Expected a RainReportException to be thrown");
		} catch (RainReportException e) {
			verifyNoMoreInteractions(mockClient);
		}
	}

	@Test
	public void whenThereAreMoreThan1000ApiCallsInADay_thenItShouldStartMakingNewCallsTheNextDay() throws Exception {
		when(mockClient.forecast(any())).thenReturn(mockForecast);
		

		// Make 1100 requests
		IntStream.range(0, 1100).forEachOrdered(n -> {
			try {
				subject.isItRainingAtCoordinates(DUMMY_LATITUDE, DUMMY_LONGITUDE);
			} catch (RainReportException e) {
				// Ignore, expected after 1000 requests
			}
		});

		verify(mockClient, times(999)).forecast(any());
		reset(mockClient);

		// Make sure we don't make any new Dark Sky API requests
		try {
			subject.isItRainingAtCoordinates(DUMMY_LATITUDE, DUMMY_LONGITUDE);
			fail("Expected a RainReportException to be thrown");
		} catch (RainReportException e) {
			verifyNoMoreInteractions(mockClient);
		}
		
		// Return tomorrows date to reset the API call counter and make sure we then
		// make new requests again
		mockForecast(true, 0.5);
		RainReport dummyRainReport = new RainReport();
		dummyRainReport.setLatitude(DUMMY_LATITUDE);
		dummyRainReport.setLongitude(DUMMY_LONGITUDE);
		dummyRainReport.setCurrentPrecipitation(Precipitation.RAIN.toString());
		dummyRainReport.setChanceOfPrecipitationToday(0.5);
		
		when(mockClock.millis()).thenReturn(Clock.systemUTC().millis() + DateUtils.MILLIS_PER_DAY);

		RainReport result = subject.isItRainingAtCoordinates(DUMMY_LATITUDE, DUMMY_LONGITUDE);
		verify(mockClient, times(1)).forecast(any());
		assertThat(result).isEqualTo(dummyRainReport);
	}
}
