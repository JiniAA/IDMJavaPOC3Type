package com.idm.tarento.RestApiConnector.Services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataPostService {

//    {
//        HttpURLConnection readConn = null;
//        HttpURLConnection writeConn = null;
//        List<String> session = null;
//        String username = "devadmin";
//        String password = "IdM_2019";
//        String authString = username + ":" + password;
//        String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes());
//        String url = "http://vmi320299.contaboserver.net:50600/idmrestapi/v2/service/ET_MX_PERSON(ID=79608,TASK_GUID=guid'EF8ADD60-BE32-4D68-8785-570CDEE57999')";
//        URL apiUrl = null;
//        try {
//            apiUrl = new URL(url);
//        } catch (MalformedURLException ex) {
//            throw new RuntimeException(ex);
//        }
//
////         readConn = (HttpURLConnection) new URL(url).openConnection();
//        try {
//            readConn = (HttpURLConnection) apiUrl.openConnection();
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
//        //String token = "wXgZDEJY5Ak52_S8gOEfjoAW9S1UtombL1o";
//        try {
//            readConn.setRequestMethod("GET");
//        } catch (ProtocolException ex) {
//            throw new RuntimeException(ex);
//        }
//        readConn.setRequestProperty("Accept", "application/json");
//        //readConn.setRequestProperty("Username","devadmin");
//        readConn.setRequestProperty("Authorization", "Basic " + encodedAuthString);
//        //readConn.setRequestProperty("X-CSRF-Token", "Fetch");
//        // readConn.setRequestProperty("X-CSRF-Token", token);
//        readConn.setRequestProperty("X-CSRF-Token", "Fetch");
//        try {
//            readConn.connect();
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
//        int responseCode = 0;
//        try {
//            responseCode = readConn.getResponseCode();
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
//        System.out.println(responseCode);
//        if (responseCode == HttpURLConnection.HTTP_OK) {
//            BufferedReader reader = null;
//            try {
//                reader = new BufferedReader(new InputStreamReader(readConn.getInputStream()));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            String line;
//            StringBuilder response = new StringBuilder();
//            while (true) {
//                try {
//                    if (!((line = reader.readLine()) != null)) break;
//                } catch (IOException ex) {
//                    throw new RuntimeException(ex);
//                }
//                response.append(line);
//            }
////             reader.close();
//
//            String responseData = response.toString();
//            System.out.println("Response: " + responseData);
//        } else {
//            System.out.println("Error: " + responseCode);
//        }



//    } catch (Exception e) {
//        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//
//    }
     }

//    public ResponseEntity<?> updateEntry(String Id){
//        // String getUrl = urlGeneratorForGet(Id);
//
//        String postUrl= urlGeneratorForPost(Id);
//        System.out.println("posturl: "+postUrl);
//
//        try{
//            ResponseEntity<?> responseEntity =getEntry(Id);
//
//            //calling get and taking the headers
////            HttpHeaders getHeaders = new HttpHeaders();
////            getHeaders.setBasicAuth("devadmin", "IdM_2019");
////            getHeaders.set("Accept", "application/json");
////            getHeaders.set("X-CSRF-Token", "Fetch");
////            //ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
////            HttpEntity<?> requestEntity = new HttpEntity<>(getHeaders);
////            ResponseEntity<String> getResponse = restTemplate.exchange(getUrl, HttpMethod.GET, requestEntity, String.class);
////
////            //ResponseEntity<Void> getResponse = restTemplate.exchange(getUrl, HttpMethod.GET, new HttpEntity<>(getHeaders), Void.class);
////            String csrfToken = getResponse.getHeaders().getFirst("X-CSRF-Token");
////            System.out.println("token:  "+csrfToken);
//            Charset charset = StandardCharsets.UTF_8;
//            String contentType = "application/json";
//            String csrfToken= "Mp_KMX0W_VBp8mUypQTmCgSefl54H02EO28";
//            //calling post api and posting the data
//            HttpHeaders postHeaders = new HttpHeaders();
//            postHeaders.setBasicAuth("devadmin", "IdM_2019");
//            postHeaders.set("X-CSRF-Token", csrfToken);
//            // postHeaders.setContentType(MediaType.APPLICATION_JSON);
//            MediaType mediaType = new MediaType(MediaType.parseMediaType(contentType), charset);
//            postHeaders.setContentType(mediaType);
//            //postHeaders.setContentType(MediaType.parseMediaType(contentType));
//            postHeaders.set("Accept", "application/json");
//            //Passing value to the body
//            //Object postBody;
//            Map<String, Object> postBody = new HashMap<>();
//            postBody.put("SV_MX_MAIL_PRIMARY","deva.benz@example3.com");
//            HttpEntity<Map<String, Object>> postEntity = new HttpEntity<>(postBody, postHeaders);
//            ResponseEntity<String> postResponse = restTemplate.exchange(postUrl, HttpMethod.POST, postEntity, String.class);
//            HttpStatus postStatusCode = (HttpStatus) postResponse.getStatusCode();
//            String postResponseBody = postResponse.getBody();
//
//            System.out.println("POST Status Code: " + postStatusCode);
//            System.out.println("POST Response Body: " + postResponseBody);
//        }
//        catch (Exception e)
//        {
//            System.out.println(e);
//        }
//        return null;
//    }
//}


// get using read connection method

//        try {
//            String username = "devadmin";
//            String password = "IdM_2019";
//            String authString = username + ":" + password;
//            String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes());
//            URL apiUrl = new URL(url);
//            HttpURLConnection readConn = null;
//            HttpURLConnection writeConn = null;
//            List<String> session = null;
////         readConn = (HttpURLConnection) new URL(url).openConnection();
//            readConn = (HttpURLConnection) apiUrl.openConnection();
//            String token = "wXgZDEJY5Ak52_S8gOEfjoAW9S1UtombL1o";
//            readConn.setRequestMethod("GET");
//            readConn.setRequestProperty("Accept", "application/json");
//            //readConn.setRequestProperty("Username","devadmin");
//            readConn.setRequestProperty("Authorization", "Basic " + encodedAuthString);
//            //readConn.setRequestProperty("X-CSRF-Token", "Fetch");
//           // readConn.setRequestProperty("X-CSRF-Token", token);
//            readConn.connect();
//            int responseCode = readConn.getResponseCode();
//            System.out.println(responseCode);
//         if (responseCode == HttpURLConnection.HTTP_OK) {
//             BufferedReader reader = new BufferedReader(new InputStreamReader(readConn.getInputStream()));
//             String line;
//             StringBuilder response = new StringBuilder();
//             while ((line = reader.readLine()) != null) {
//                 response.append(line);
//             }
////             reader.close();
//
//             String responseData = response.toString();
//             System.out.println("Response: " + responseData);
//         } else {
//             System.out.println("Error: " + responseCode);
//         }
//
//        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//
//        }