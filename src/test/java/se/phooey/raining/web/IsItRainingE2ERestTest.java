package se.phooey.raining.web;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import io.restassured.http.ContentType;
import se.phooey.raining.utils.TestUtils;
import se.phooey.raining.weather.Precipitation;

/**
 * End-To-End test making sure a real HTTP GET request to the route
 * "/isitraining" in the served REST API is working as expected.
 * 
 * As we have already tested everything else on lower levels, we just spin up
 * the server and do one simple test with a HTTP request here, for a positive scenario.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IsItRainingE2ERestTest {

	@LocalServerPort
	private int port;

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089);

	@Test
	public void getIsItRainingShouldReturnDummyRainReport() throws Exception {
		final double latitude = 13.37;
		final double longitude = 90.01;
		final String url = String.format("http://localhost:%s/isitraining", port);

		TestUtils.stubResponseForDarkSkyApiRequest(TestUtils.getDarkSkyUrl(latitude, longitude), wireMockRule,
				"classpath:dummy_darksky_response.json");

		given()
		.queryParam("latitude", String.valueOf(latitude))
		.queryParam("longitude", String.valueOf(longitude))
		.when()
		.get(url)
		.then()
		.statusCode(equalTo(200)).and()
		.contentType(ContentType.JSON).and()
		.body("latitude", equalTo((float) latitude)).and()
		.body("longitude", equalTo((float) longitude)).and()
		.body("currentPrecipitation", equalTo(Precipitation.RAIN.toString())).and()
		.body("chanceOfPrecipitationToday", equalTo((float) 1));
	}
}
