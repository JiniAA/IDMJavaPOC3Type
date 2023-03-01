package com.tarento.Idm.poc.util;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component

public class BasicAuthAndApiKeyFilterAndAuthenticator {
    public static String authenticationFilepath = "C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\FilePathsOfIDMPocTask1\\BasicAuthCredentials.txt";

    public Map<String, String> setCredentials() throws IOException, ParseException {
        Map<String, String> credential = new HashMap<>();
        String credentilas = new String(Files.readAllBytes(Paths.get(authenticationFilepath)));
        JSONParser parse = new JSONParser(credentilas);
        Map<String, String> json_credentials = (Map<String, String>) parse.parse();
        credential.put("ApiKey", json_credentials.get("ApiKey"));
        credential.put("KeyValue", json_credentials.get("KeyValue"));
        credential.put("AuthType", json_credentials.get("AuthType"));
        credential.put("username", json_credentials.get("username"));
        credential.put("password", json_credentials.get("password"));

        return credential;
    }

    public Boolean DoFilter(HttpServletRequest request) {
        try {
            Map<String, String> authData = setCredentials();
            String apikey=authData.get("ApiKey");
            //String apikeyFromHeader=request.get
            System.out.println(apikey);
            String apiKeyHeader = request.getHeader(apikey);
            String basicAuthHeader = request.getHeader("Authorization");
            if ((basicAuthHeader == null) && (apiKeyHeader == null || !apiKeyHeader.equals(authData.get("KeyValue")))) {
                return false;
            } else {
                if (basicAuthHeader != null) {//means user using basic auth
                    return BasicAuthFilter(basicAuthHeader, authData);
                } else {
                    //user using api key
                    System.out.println(apiKeyHeader.equals(authData.get("KeyValue")));
                    return apiKeyHeader.equals(authData.get("KeyValue"));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean BasicAuthFilter(String basicAuthHeader, Map<String, String> userData) throws IOException, ParseException {
        try {
            if (basicAuthHeader != null && basicAuthHeader.startsWith("Basic ")) {
                String base64Credentials = basicAuthHeader.substring("Basic ".length());
                String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
                String[] parts = credentials.split(":", 2);
                String authUsername = parts[0];
                String authPassword = parts[1];
                System.out.println("authUsername+authPassword:" + authUsername + authPassword);
                if (authUsername.equals(userData.get("username")) && authPassword.equals(userData.get("password"))) {
                    return true;
                } else {
                    return false;
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

