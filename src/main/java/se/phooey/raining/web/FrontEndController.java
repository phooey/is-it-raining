package se.phooey.raining.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Simple {@link Controller} serving an HTML page as the front-end of the
 * application on route "/", using Thymeleaf.
 */
@Controller
@PropertySource("classpath:frontend.properties")
public class FrontEndController {

	@Value("${application.name}")
	private String appName;

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("appName", appName);
		return "index";
	}
}
