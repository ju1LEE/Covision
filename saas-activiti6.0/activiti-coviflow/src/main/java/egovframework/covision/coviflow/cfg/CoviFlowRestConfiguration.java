package egovframework.covision.coviflow.cfg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import egovframework.covision.coviflow.rest.service.api.CoviFlowRestResponseFactory;

@Configuration
public class CoviFlowRestConfiguration {

	private final Logger log = LoggerFactory.getLogger(CoviFlowRestConfiguration.class);

	@Bean()
	public CoviFlowRestResponseFactory coviFlowRestResponseFactory() {
		log.info("coviFlowRestResponseFactory");
		CoviFlowRestResponseFactory restResponseFactory = new CoviFlowRestResponseFactory();
		return restResponseFactory;
	}
	
}
