package egovframework.covision.coviflow.cfg;

import java.sql.Driver;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class CoviFlowConfiguration {

	private final Logger log = LoggerFactory.getLogger(CoviFlowConfiguration.class);

	@Autowired
	protected Environment environment;

	/*
	@Bean
	public DataSource coviFlowdataSource() {
		SimpleDriverDataSource ds = new SimpleDriverDataSource();

		try {
			@SuppressWarnings("unchecked")
			Class<? extends Driver> driverClass = (Class<? extends Driver>) Class
					.forName(environment.getProperty("jdbc.driver",
							"org.h2.Driver"));
			ds.setDriverClass(driverClass);

		} catch (Exception e) {
			log.error("Error loading driver class", e);
		}

		// Connection settings
		ds.setUrl(environment.getProperty("jdbc.url",
				"jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000"));
		ds.setUsername(environment.getProperty("jdbc.username", "sa"));
		ds.setPassword(environment.getProperty("jdbc.password", ""));

		return ds;
	}

	@Bean(name = "coviFlowTransactionManager")
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(coviFlowdataSource());
		return transactionManager;
	}
	*/
	
	/*
	 * coviFlow database init 하는 bean을 아래에 구현 할 것 
	 * org.activiti.rest.conf.ActivitiEngineConfiguration을 참고 할 것
	 * */

}
