package se.phooey.raining.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import se.phooey.raining.weather.IsItRaining;
import se.phooey.raining.weather.RainReport;
import se.phooey.raining.weather.WeatherProvider;
import se.phooey.raining.weather.exception.RainReportException;
import se.phooey.raining.web.exception.InvalidCoordinatesException;

/**
 * Unit tests for @see
 * se.phooey.raining.web.IsItRainingController.IsItRainingController
 */
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

		final String raining = IsItRaining.YES.toString();
		final double rainingCurrently = 1;

		RainReport dummyReport = new RainReport(truncatedLatitude, truncatedLongitude, raining, rainingCurrently);
		given(mockWeatherProvider.isItRainingAtCoordinates(truncatedLatitude, truncatedLongitude))
				.willReturn(dummyReport);

		RainReport result = subject.isItRaining(requestedLatitude, requestedLongitude);

		assertThat(result).isEqualTo(dummyReport);
	}

}
