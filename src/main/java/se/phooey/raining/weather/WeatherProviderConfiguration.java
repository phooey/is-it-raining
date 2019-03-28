package se.phooey.raining.weather;

import java.time.Clock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import tk.plogitech.darksky.api.jackson.DarkSkyJacksonClient;
import tk.plogitech.darksky.forecast.APIKey;

@Configuration
@PropertySource("classpath:darksky.apikey.properties")
public class WeatherProviderConfiguration {
	@Value("${darksky.api.key}")
	private String apiKey;
	@Value("${darksky.api.url}")
	private String apiUrl;
	
	@Bean
	public WeatherProvider darkSkyWeatherProvider() {
		return new DarkSkyWeatherProvider(new APIKey(apiKey), apiUrl, new DarkSkyJacksonClient(), Clock.systemUTC());
	}
}
