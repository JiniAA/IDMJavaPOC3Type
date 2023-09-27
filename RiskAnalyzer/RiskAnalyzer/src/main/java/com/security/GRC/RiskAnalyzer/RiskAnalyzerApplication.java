package com.security.GRC.RiskAnalyzer;

import com.security.GRC.RiskAnalyzer.Services.DataGetService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class RiskAnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RiskAnalyzerApplication.class, args);
	}

}
