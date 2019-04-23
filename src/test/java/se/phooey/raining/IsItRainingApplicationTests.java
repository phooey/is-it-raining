package se.phooey.raining;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import se.phooey.raining.weather.DarkSkyWeatherProvider;
import se.phooey.raining.weather.WeatherProvider;

/**
 * Simple test to make sure the application context loads correctly and that the
 * expected beans are injected, if not it will be caught already here.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IsItRainingApplicationTests {

	@Autowired
	WeatherProvider wp;

	@Test
	public void contextLoadsAndWeatherProviderBeanIsInjected() {
		assertThat(wp).isInstanceOf(DarkSkyWeatherProvider.class);
	}

}
