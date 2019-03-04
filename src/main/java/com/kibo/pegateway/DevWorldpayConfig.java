package com.kibo.pegateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevWorldpayConfig {
	
	@Bean
	public String dataSource() {
		  return null;
	}	 

}
