package se.phooey.raining.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import se.phooey.raining.weather.Precipitation;
import se.phooey.raining.weather.RainReport;
import se.phooey.raining.weather.WeatherProvider;
import se.phooey.raining.weather.exception.RainReportException;

/**
 * API tests using Spring's MockMvc to make sure the REST API for route
 * "/isitraining" is behaving as expected.
 * 
 * The logic in the IsItRainingController is already tested by the unit tests,
 * so here we focus on HTTP status codes and responses.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class IsItRainingControllerAPITest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private WeatherProvider weatherProviderMock;

	private static final double DUMMY_LATITUDE = 13.37;
	private static final double DUMMY_LONGITUDE = 90.01;

	@Test
	public void whenGetIsItRainingWithoutParams_shouldRespondBadRequest() throws Exception {
		this.mockMvc.perform(get("/isitraining")).andExpect(status().is4xxClientError());
	}

	@Test
	public void whenGetIsItRainingWithInvalidCoordinates_shouldRespondBadRequest() throws Exception {
		this.mockMvc.perform(
				get("/isitraining")
				.param("latitude", String.valueOf(95))
				.param("longitude", String.valueOf(-190)))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void whenGetIsItRainingCausesTheWeatherProviderToThrowARainReportException_shouldRespondBadRequest()
			throws Exception {
		given(weatherProviderMock.isItRainingAtCoordinates(DUMMY_LATITUDE, DUMMY_LONGITUDE))
				.willThrow(RainReportException.class);
		this.mockMvc.perform(
				get("/isitraining")
				.param("latitude", String.valueOf(DUMMY_LATITUDE))
				.param("longitude", String.valueOf(DUMMY_LONGITUDE)))
				.andExpect(status().is5xxServerError());
	}

	@Test
	public void whenGetIsItRainingWithValidParams_shouldReturnARainReportForTheLocation() throws Exception {
		RainReport rainReport = new RainReport();
		rainReport.setLatitude(DUMMY_LATITUDE);
		rainReport.setLongitude(DUMMY_LONGITUDE);
		rainReport.setCurrentProbability(0.1);
		rainReport.setCurrentPrecipitation(Precipitation.RAIN.toString());
		rainReport.setCurrentIntensity(0.05);
		rainReport.setChanceOfPrecipitationToday(0.5);
		rainReport.setTypeOfPrecipitationToday(Precipitation.RAIN.toString());
		
		given(weatherProviderMock.isItRainingAtCoordinates(DUMMY_LATITUDE, DUMMY_LONGITUDE)).willReturn(rainReport);

		this.mockMvc.perform(
				get("/isitraining")
				.param("latitude", String.valueOf(DUMMY_LATITUDE))
				.param("longitude", String.valueOf(DUMMY_LONGITUDE)))
				.andDo(print()).andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.latitude").value(DUMMY_LATITUDE))
				.andExpect(jsonPath("$.longitude").value(DUMMY_LONGITUDE))
				.andExpect(jsonPath("$.currentPrecipitation").value(Precipitation.RAIN.toString()))
				.andExpect(jsonPath("$.currentProbability").value(0.1))
				.andExpect(jsonPath("$.currentIntensity").value(0.05))
				.andExpect(jsonPath("$.chanceOfPrecipitationToday").value(0.5))
				.andExpect(jsonPath("$.typeOfPrecipitationToday").value(Precipitation.RAIN.toString()));
	}

}
