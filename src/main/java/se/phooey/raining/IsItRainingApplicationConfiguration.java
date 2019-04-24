package se.phooey.raining;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class IsItRainingApplicationConfiguration {
	
	@Bean
	public CaffeineCache rainReportCache() {
	    return new CaffeineCache("rainReportCache",
	            Caffeine.newBuilder()
	                    .expireAfterAccess(1, TimeUnit.MINUTES)
	                    .build());
	}

	@Bean
	public CommonsRequestLoggingFilter logFilter() {
		CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
		filter.setIncludeClientInfo(true);
		filter.setIncludeQueryString(true);
		filter.setIncludePayload(true);
		filter.setMaxPayloadLength(10000);
		filter.setIncludeHeaders(false);
		return filter;
	}
}