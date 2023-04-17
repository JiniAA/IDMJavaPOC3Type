package com.tarento.Idm.poc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.io.IOException;
import java.sql.SQLException;



@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })

public class MainClass {
	private static final Logger logger = LoggerFactory.getLogger(MainClass.class);
	public static void main(String[] args) throws SQLException, IOException {
		SpringApplication.run(MainClass.class, args);
		logger.info("Application started");


	}




}
