package com.tarento.Idm.poc.config;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration

public class SpringSecurityConfig {

    public static String credFilepath="C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\BasicAuthCredentials.txt";
    public static String user_name;
    public static String password;
    public static String AuthType;
    @Bean
    public void setCredentilas () throws IOException, ParseException {
        String credentilas = new String(Files.readAllBytes(Paths.get(credFilepath)));
        JSONParser parse = new JSONParser(credentilas);
        Map<String, String> json_credentials = (Map<String, String>) parse.parse();
        System.out.println("json_credentials:"+json_credentials);
        user_name=json_credentials.get("username");
        password=json_credentials.get("password");
        AuthType=json_credentials.get("Authentication_type");
        System.out.println(user_name);
    }
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username(user_name)
                .password(password)
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable();
//        http
//                .authorizeHttpRequests((authz) -> authz
//                        .anyRequest().fullyAuthenticated().and()
//                )
//                .httpBasic(withDefaults());
//        return http.build();
//    }
}
