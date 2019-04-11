package se.phooey.raining.web;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import io.github.bonigarcia.wdm.WebDriverManager;
import se.phooey.raining.utils.TestUtils;

/**
 * Full-Stack End-to-End test using Selenium to make sure that the front-end of
 * the application is served at route "/", and is working correctly integrating
 * with the back-end.
 * 
 * The back-end is already fully tested, so we focus on testing the front-end
 * here. The front-end JavaScript code is also already tested with unit tests.
 * So due to the the long execution time of these tests we do only a positive 
 * test scenario here.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IsItRainingE2ESeleniumTest {

	private FirefoxDriver driver;
	private String url;

	@LocalServerPort
	private int port;

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089);

	@BeforeClass
	public static void setUpClass() throws Exception {
		WebDriverManager.firefoxdriver().setup();
	}

	@Before
	public void setUp() throws Exception {
		FirefoxOptions options = new FirefoxOptions();
		FirefoxProfile profile = new FirefoxProfile();
		// Disable native events and spoof geolocation
		profile.setPreference("webdriver_enable_native_events", false);
		profile.setPreference("geo.prompt.testing", true);
		profile.setPreference("geo.prompt.testing.allow", true);
		final String fileName = "spoofed_geolocation.json";
		URL fileUrl = getClass().getClassLoader().getResource(fileName);
		profile.setPreference("geo.wifi.uri",
				Objects.requireNonNull(fileUrl, String.format("Cannot find file %s", fileName)).toExternalForm());
		options.setProfile(profile);
		options.setHeadless(true);
		driver = new FirefoxDriver(options);
		url = String.format("http://localhost:%d/", port);
	}

	@After
	public void tearDown() {
		driver.quit();
	}

	@Test
	public void pageWithSpoofedGeolocationIncludesCoordinatesAndDummyRainReportWithDarkSkyAttributionText()
			throws InterruptedException, IOException {
		double expectedLatitude = 13.37;
		double expectedLongitude = 90.01;

		TestUtils.stubResponseForDarkSkyApiRequest(TestUtils.getDarkSkyUrl(expectedLatitude, expectedLongitude),
				wireMockRule, "classpath:dummy_darksky_response.json");

		driver.get(url);

		WebDriverWait wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("latitude"),
				String.valueOf(expectedLatitude)));

		// Check that some main style elements are correct
		assertThat(driver.getTitle(), equalTo("Is it raining?"));
		assertThat(driver.findElement(By.id("title")).getText(), equalTo("Is it raining?"));
		assertThat(driver.findElement(By.id("rainReport")).getCssValue("visibility"), equalTo("visible"));
		
		// Check that all values from the RainReport are set
		assertThat(driver.findElement(By.id("latitude")).getText(), equalTo(String.valueOf(expectedLatitude)));
		assertThat(driver.findElement(By.id("longitude")).getText(), equalTo(String.valueOf(expectedLongitude)));
		assertThat(driver.findElement(By.id("answer")).getText(), equalTo("Yes"));
		assertThat(driver.findElement(By.id("currentPrecipitation")).getText(), equalTo("rain"));
		assertThat(driver.findElement(By.id("currentProbability")).getText(), equalTo("1%"));
		assertThat(driver.findElement(By.id("currentIntensity")).getText(), equalTo("1.29 mm/hour"));
		assertThat(driver.findElement(By.id("chanceOfPrecipitationToday")).getText(), equalTo("100%"));
		assertThat(driver.findElement(By.id("typeOfPrecipitationToday")).getText(), equalTo("rain"));

		// Check that the attribution link for Dark Sky is visible according to the terms of service
		final String attributionText = "Powered by Dark Sky";
		final String attributionLink = "https://darksky.net/poweredby/";

		assertThat(driver.findElement(By.id("darkSkyAttribution")).getText(), equalTo(attributionText));
		assertThat(driver.findElement(By.id("darkSkyAttribution")).getAttribute("href"), equalTo(attributionLink));
	}
}
