package com.tarento.Idm.poc;

import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.io.IOException;
import java.sql.SQLException;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableWebSecurity
public class PocApplication  {

	public static void main(String[] args) throws SQLException, IOException {
		SpringApplication.run(PocApplication.class, args);

	}




}
