package se.phooey.raining.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;

/**
 * Unit tests for se.phooey.raining.web.IsItRainingController.FrontEndController
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = FrontEndController.class)
@TestPropertySource(locations = "/frontend.properties")
public class FrontEndControllerTest {
	
	@Value("${application.name}")
	private String expectedAppName;
	
	@Mock
	Model model;

	@Autowired
	FrontEndController subject;
	
	@Before
	public void setUp() {
		initMocks(this);
	}
	
	@Test
	public void index_shouldReturnIndexAndInjectAppName() {
		String result = subject.index(model);
		
		then(model).should().addAttribute("appName", expectedAppName);
		
		assertThat(result).isEqualTo("index");
	}
}
