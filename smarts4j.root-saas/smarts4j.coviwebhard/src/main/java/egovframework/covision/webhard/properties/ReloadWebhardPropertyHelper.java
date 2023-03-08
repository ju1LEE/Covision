package egovframework.covision.webhard.properties;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import egovframework.covision.webhard.common.web.MobileCommonCon;

@Component
public class ReloadWebhardPropertyHelper{
	
	public static final Logger LOGGER = LogManager.getLogger(ReloadWebhardPropertyHelper.class);
	
	private static ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration> builder;
	
	//JAVA 9 + javax.annotation-api
	@PostConstruct
	void init() {
		builder = new ReloadingFileBasedConfigurationBuilder<>(PropertiesConfiguration.class).configure(new Parameters().fileBased().setFile(new File(System.getProperty("DEPLOY_PATH")+"/covi_property/webhard.properties")));
		
		builder.addEventListener(ConfigurationBuilderEvent.CONFIGURATION_REQUEST, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) {
				
			}
			
		});
		
		//1분마다 프로퍼티 감지
		PeriodicReloadingTrigger configReloadingTrigger = new PeriodicReloadingTrigger(builder.getReloadingController(), null, 1, TimeUnit.MINUTES);
		
		configReloadingTrigger.start();
	}
	
	
	public static Configuration getCompositeConfiguration() {
		try {
			
			return builder.getConfiguration();
			
		} catch (ConfigurationException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
			
		return null;
	}
	
}
