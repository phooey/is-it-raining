package se.phooey.raining.web;

import static org.hamcrest.CoreMatchers.containsString;
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
 * Full-Stack End-to-End tests using Selenium to make sure that the front-end of
 * the application is served at route "/", and is working correctly integrating
 * with the back-end.
 * 
 * The back-end is already fully tested, so we focus on testing the front-end
 * here. The front-end is really simple, but has some error-handling which
 * should arguably be tested. But due to simplicity and the long execution time
 * of these tests we do only a positive test scenario here.
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
		profile.setPreference("geo.wifi.uri", Objects.requireNonNull(fileUrl, String.format("Cannot find file %s", fileName)).toExternalForm());
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
	public void pageWithSpoofedGeolocationIncludesCoordinatesAndDummyRainReport()
			throws InterruptedException, IOException {
		double expectedLatitude = 13.37;
		double expectedLongitude = 90.01;

		TestUtils.stubResponseForDarkSkyApiRequest(TestUtils.getDarkSkyUrl(expectedLatitude, expectedLongitude),
				wireMockRule, "classpath:dummy_darksky_response.json");

		driver.get(url);

		WebDriverWait wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("latitude"),
				String.valueOf(expectedLatitude)));

		assertThat(driver.findElement(By.id("title")).getText(), containsString("Is it raining?"));
		assertThat(driver.findElement(By.id("latitude")).getText(), containsString(String.valueOf(expectedLatitude)));
		assertThat(driver.findElement(By.id("longitude")).getText(), containsString(String.valueOf(expectedLongitude)));
		assertThat(driver.findElement(By.id("rainingCurrently")).getText(), containsString("Yes"));
		assertThat(driver.findElement(By.id("chanceOfRain")).getText(), containsString("100%"));
	}

	@Test
	public void pageIncludesAttributionLinkToDarkSky() {
		final String attributionText = "Powered by Dark Sky";
		final String attributionLink = "https://darksky.net/poweredby/";

		driver.get(url);

		assertThat(driver.findElement(By.tagName("body")).getText(), containsString(attributionText));
		assertThat(driver.findElement(By.id("darkSkyAttribution")).getText(), containsString(attributionText));
		assertThat(driver.findElement(By.id("darkSkyAttribution")).getAttribute("href"),
				containsString(attributionLink));
	}
}
