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
import java.util.Map;

@Component
//public class ApiKeyFilter extends OncePerRequestFilter {
public class BasicAuthAndApiKeyFilterAndAuthenticator {

   // private final String apiKey = "123456";
    public static String credFilepath="C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\FilePathsOfIDMPoc\\BasicAuthCredentials.txt";
    public static String ApiKey;
    public static String KeyValue;
    public static String AuthType;
    public static String username;
    public static String password;
    @Bean
    public void setCredentilass () throws IOException, ParseException {
        String credentilas = new String(Files.readAllBytes(Paths.get(credFilepath)));
        JSONParser parse = new JSONParser(credentilas);
        Map<String, String> json_credentials = (Map<String, String>) parse.parse();
        System.out.println("json_credentials:"+json_credentials);
        ApiKey=json_credentials.get("ApiKey");
        KeyValue=json_credentials.get("KeyValue");
        AuthType=json_credentials.get("Authentication_type");
        username=json_credentials.get("username");
        password=json_credentials.get("password");
        System.out.println(ApiKey+KeyValue);
        System.out.println(json_credentials);
    }

    //@Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String apiKeyHeader = request.getHeader("Key");
//        if (apiKeyHeader == null || !apiKeyHeader.equals(KeyValue)) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API key");
//            return;
//        }
//        filterChain.doFilter(request, response);
//    }

    public Boolean DoFilter(HttpServletRequest request) {
        String apiKeyHeader = request.getHeader(ApiKey);
        System.out.println("apiKeyHeader"+apiKeyHeader);
        String basicAuthHeader=request.getHeader("Authorization");
        System.out.println("basicAuthHeader"+basicAuthHeader);
        if ((basicAuthHeader == null ) && (apiKeyHeader == null || !apiKeyHeader.equals(KeyValue))) {
            return false;
        }
        else {
            if (basicAuthHeader!= null)
            {//means user using basic auth
              return BasicAuthFilter(basicAuthHeader);
            }
            else {
                //user using api key
                return apiKeyHeader.equals(KeyValue);
            }
        }
    }
    public Boolean BasicAuthFilter(String basicAuthHeader){
        if (basicAuthHeader != null && basicAuthHeader.startsWith("Basic ")) {
            String base64Credentials = basicAuthHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
            String[] parts = credentials.split(":", 2);
            String authUsername = parts[0];
            String authPassword = parts[1];
            System.out.println("authUsername+authPassword:"+authUsername+authPassword);
            if (authUsername.equals(username) && authPassword.equals(password)) {
                return true;
            } else {
                return false;
            }
        }
        return null;
    }


}

