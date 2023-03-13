package com.tarento.Idm.poc.util;

import com.tarento.Idm.poc.PocApplication;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(BasicAuthAndApiKeyFilterAndAuthenticator.class);
    public static String authenticationFilepath = "C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\FilePathsOfIDMPocTask1\\BasicAuthCredentials.txt";

    public Map<String, String> setCredentials()  {
      try {
          logger.info("setCredentials() method invoked");
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
      } catch (IOException e) {
          logger.error(String.valueOf(e));
          throw new RuntimeException(e);
      } catch (ParseException e) {
          logger.error(String.valueOf(e));
          throw new RuntimeException(e);
      }
    }

    public Boolean DoFilter(HttpServletRequest request) {
        try {
            logger.info("Entered into DoFilter() method");
            Map<String, String> authData = setCredentials();
            String apikey=authData.get("ApiKey");
            //String apikeyFromHeader=request.get
            System.out.println(apikey);
            String apiKeyHeader = request.getHeader(apikey);
            String basicAuthHeader = request.getHeader("Authorization");
            if ((basicAuthHeader == null) && (apiKeyHeader == null || !apiKeyHeader.equals(authData.get("KeyValue")))) {
                logger.info("Checking authentication: No authetication details are provided by user");
                return false;
            } else {
                if (basicAuthHeader != null) {//means user using basic auth
                   logger.info("user given Basic authentication Details");
                    return BasicAuthFilter(basicAuthHeader, authData);
                } else {
                    //user using api key
                    System.out.println(apiKeyHeader.equals(authData.get("KeyValue")));
                    logger.info("User has given api key authentication");
                    return apiKeyHeader.equals(authData.get("KeyValue"));
                }
            }
        } catch (IOException e) {
            logger.error(String.valueOf(e));
            throw new RuntimeException(e);
        } catch (ParseException e) {
            logger.error(String.valueOf(e));
            throw new RuntimeException(e);
        }
    }

    public Boolean BasicAuthFilter(String basicAuthHeader, Map<String, String> userData) throws IOException, ParseException {
        try {
            logger.info("Entered into BasicAuthFilter() Method");
            if (basicAuthHeader != null && basicAuthHeader.startsWith("Basic ")) {
                String base64Credentials = basicAuthHeader.substring("Basic ".length());
                String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
                String[] parts = credentials.split(":", 2);
                String authUsername = parts[0];
                String authPassword = parts[1];
                logger.info("username and password given by user"+authUsername+"\n"+authPassword);
                System.out.println("authUsername+authPassword:" + authUsername + authPassword);
                if (authUsername.equals(userData.get("username")) && authPassword.equals(userData.get("password"))) {
                    return true;
                } else {
                    return false;
                }
            }
            return null;
        } catch (Exception e) {
            logger.error(String.valueOf(e));
            throw new RuntimeException(e);
        }
    }


}

