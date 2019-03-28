package se.phooey.raining.utils;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * Utility class with reusable methods to be used in the different tests
 */
@Component
public class TestUtils {

	private static String apiKey;
	
	@Value("${darksky.api.key}")
    public void setApiKey(String apiKey) {
        TestUtils.apiKey = apiKey;
    }
	
	private static final String readFile(String path) throws IOException {
		File file = ResourceUtils.getFile(path);
		return new String(Files.readAllBytes(file.toPath()));
	}

	/**
	 * Get the expected Dark Sky API request URL for a given latitude and longitude
	 * 
	 * @param latitude the latitude of the request
	 * @param longitude the longitude of the request
	 * @return the URL for the Dark Sky API request for the given coordinates (reduced to two decimal points)
	 */
	public static final String getDarkSkyUrl(double latitude, double longitude) {
		return String.format(Locale.US, "/%s/%.2f,%.2f", TestUtils.apiKey, latitude, longitude);
	}

	/**
	 * Use the provided wireMockRule to stub out a request to the given URL,
	 * read data from the provided response file to give in the canned HTTP response.
	 * 
	 * @param requestUrl the Dark Sky API URL to match when stubbing the response for the request
	 * @param wireMockRule the WireMock rule to use when stubbing the response for the request
	 * @param responseFile path to a file containing the JSON data to give in the stubbed HTTP response
	 * @throws IOException if the given responseFile can not be read
	 */
	public static void stubResponseForDarkSkyApiRequest(String requestUrl, WireMockRule wireMockRule,
			String responseFile) throws IOException {
		String darkSkyApiResponse = TestUtils.readFile(responseFile);
		wireMockRule.stubFor(
				get(urlPathMatching(requestUrl))
				.willReturn(aResponse().withBody(darkSkyApiResponse)
				.withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.withStatus(200)));
	}
}
