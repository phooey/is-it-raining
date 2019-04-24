package se.phooey.raining.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import se.phooey.raining.weather.Precipitation;
import se.phooey.raining.weather.RainReport;
import se.phooey.raining.weather.WeatherProvider;
import se.phooey.raining.weather.exception.RainReportException;
import se.phooey.raining.web.exception.InvalidCoordinatesException;

/**
 * Unit tests for @see
 * se.phooey.raining.web.IsItRainingController.IsItRainingController
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class IsItRainingControllerTest {

	private IsItRainingController subject;
	
	@Mock
	WeatherProvider mockWeatherProvider;

	@Before
	public void setUp() {
		initMocks(this);
		subject = new IsItRainingController(mockWeatherProvider);
	}

	@Test(expected = InvalidCoordinatesException.class)
	public void whenGivingInvalidCoordinates_itShouldThrowAnInvalidCoordinatesException()
			throws InvalidCoordinatesException, RainReportException {
		subject.isItRaining(95, 195);
	}

	@Test(expected = RainReportException.class)
	public void whenWeatherProviderThrowsARainReportException_itShouldBeRethrown()
			throws InvalidCoordinatesException, RainReportException {
		final double dummyLatitude = 13.37;
		final double dummyLongitude = 90.01;
		given(mockWeatherProvider.isItRainingAtCoordinates(dummyLatitude, dummyLongitude))
				.willThrow(RainReportException.class);
		subject.isItRaining(dummyLatitude, dummyLongitude);
	}

	@Test
	public void whenRequestingARainReport_itShouldTruncateTheCoordinatesToThreeDecimalPoints()
			throws InvalidCoordinatesException, RainReportException {
		final double requestedLatitude = 50.12345;
		final double truncatedLatitude = 50.123;

		final double requestedLongitude = 10.12345;
		final double truncatedLongitude = 10.123;

		subject.isItRaining(requestedLatitude, requestedLongitude);

		then(mockWeatherProvider).should().isItRainingAtCoordinates(truncatedLatitude, truncatedLongitude);
	}

	@Test
	public void whenWeatherProviderReturnsARainReport_itShouldBeReturnedByTheController()
			throws InvalidCoordinatesException, RainReportException {
		final double requestedLatitude = 50.12345;
		final double truncatedLatitude = 50.123;

		final double requestedLongitude = 10.12345;
		final double truncatedLongitude = 10.123;

		RainReport dummyRainReport = new RainReport();
		dummyRainReport.setLatitude(truncatedLatitude);
		dummyRainReport.setLongitude(truncatedLongitude);
		dummyRainReport.setCurrentProbability(0.1);
		dummyRainReport.setCurrentPrecipitation(Precipitation.RAIN.toString());
		dummyRainReport.setCurrentIntensity(0.05);
		dummyRainReport.setChanceOfPrecipitationToday(0.5);
		dummyRainReport.setTypeOfPrecipitationToday(Precipitation.RAIN.toString());
		given(mockWeatherProvider.isItRainingAtCoordinates(truncatedLatitude, truncatedLongitude))
				.willReturn(dummyRainReport);

		RainReport result = subject.isItRaining(requestedLatitude, requestedLongitude);

		assertThat(result).isEqualTo(dummyRainReport);
	}
	
	@Test
	public void whenRequestingARainReportForTheSameCoordinatesTwice_aCachedCopyShouldBeReturnedByTheController()
			throws InvalidCoordinatesException, RainReportException {
		final double latitude = 13.37;
		final double longitude = 90.01;

		RainReport dummyRainReport = new RainReport();
		dummyRainReport.setLatitude(latitude);
		dummyRainReport.setLongitude(longitude);
		dummyRainReport.setCurrentProbability(0.1);
		dummyRainReport.setCurrentPrecipitation(Precipitation.RAIN.toString());
		dummyRainReport.setCurrentIntensity(0.05);
		dummyRainReport.setChanceOfPrecipitationToday(0.5);
		dummyRainReport.setTypeOfPrecipitationToday(Precipitation.RAIN.toString());
		given(mockWeatherProvider.isItRainingAtCoordinates(latitude, longitude))
				.willReturn(dummyRainReport);

		RainReport first = subject.isItRaining(latitude, longitude);
		RainReport second = subject.isItRaining(latitude, longitude);

		assertThat(first).isEqualTo(second);
		verify(mockWeatherProvider, times(1)).isItRainingAtCoordinates(latitude, longitude);
		
	}

}
