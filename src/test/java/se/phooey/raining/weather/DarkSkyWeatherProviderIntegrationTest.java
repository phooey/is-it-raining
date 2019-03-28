package se.phooey.raining.weather;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import se.phooey.raining.utils.TestUtils;

/**
 * Integration tests using WireMock to make sure that DarkSkyWeatherProvider can
 * correctly parse two real responses from the DarkSky API, provided as offline
 * JSON files, into a RainReport.
 * 
 * @see se.phooey.raining.weather.DarkSkyWeatherProvider
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DarkSkyWeatherProviderIntegrationTest {

	@Autowired
	private DarkSkyWeatherProvider subject;

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089);

	@Test
	public void givenForecastWithoutRain_shouldReturnNoAndZeroChance() throws Exception {
		double latitude = 48.36;
		double longitude = 10.89;
		TestUtils.stubResponseForDarkSkyApiRequest(
				TestUtils.getDarkSkyUrl(latitude, longitude),
				wireMockRule,
				"classpath:darksky_response_without_rain.json");

		RainReport result = subject.isItRainingAtCoordinates(latitude, longitude);

		assertThat(result.getLatitude()).isEqualTo(latitude);
		assertThat(result.getLongitude()).isEqualTo(longitude);
		assertThat(result.getRainingCurrently()).isEqualTo(IsItRaining.NO.toString());
		assertThat(result.getChanceOfRainToday()).isEqualTo(0);
	}

	@Test
	public void givenForecastWithRain_shouldReturnYesAndChanceOne() throws Exception {
		double latitude = 50.76;
		double longitude = 15.05;
		TestUtils.stubResponseForDarkSkyApiRequest(
				TestUtils.getDarkSkyUrl(latitude, longitude),
				wireMockRule,
				"classpath:darksky_response_with_rain.json");

		RainReport result = subject.isItRainingAtCoordinates(latitude, longitude);

		assertThat(result.getLatitude()).isEqualTo(latitude);
		assertThat(result.getLongitude()).isEqualTo(longitude);
		assertThat(result.getRainingCurrently()).isEqualTo(IsItRaining.YES.toString());
		assertThat(result.getChanceOfRainToday()).isEqualTo(1);
	}
}
