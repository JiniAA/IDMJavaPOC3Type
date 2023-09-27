package com.idm.tarento.RestApiConnector.Services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
@Service
public class TestService {
    // TODO Auto-generated method stub

    String getUrl = "http://vmi320299.contaboserver.net:50600/idmrestapi/v2/service/ET_MX_PERSON(ID=79608,TASK_GUID=guid'EF8ADD60-BE32-4D68-8785-570CDEE57999')";
    String username = "devadmin";
    String password = "IdM_2019";

    // Set basic authentication header
    String auth = username + ":" + password;
    String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
    String authHeader = "Basic " + encodedAuth;

    HttpURLConnection readconnection = null;
    HttpURLConnection writeconnection = null;
    List<String> session = null;
		public ResponseEntity<?> executeFunction() {
            try {

                URL url = new URL(getUrl);
                readconnection = (HttpURLConnection) url.openConnection();
                readconnection.setRequestMethod("GET");
                readconnection.setRequestProperty("Authorization", authHeader);
                readconnection.setRequestProperty("X-CSRF-Token", "FETCH");
                readconnection.connect();

                int responseCode = readconnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(readconnection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();

                    System.out.println("Initial Get Call Response Code: " + responseCode);
                    String jsonResponse = response.toString();
                    System.out.println("Initial Get Call Success Response:" + jsonResponse);

                    String xsrfToken = extractXsrfToken(readconnection);
                    session = getSessionCookies(readconnection);
                    readconnection.disconnect();

                    String jsonString = "{\"SV_MX_MAIL_PRIMARY\": \"deva23052023@test.com\"}";
                    byte[] payloadBytes = jsonString.getBytes("UTF-8");

                    String postUrl = "http://vmi320299.contaboserver.net:50600/idmrestapi/v2/service/ET_MX_PERSON(ID=79608,TASK_GUID=guid'62352952-10D0-4648-B7DC-F5CB32BBA367')";
                    URL url2 = new URL(postUrl);
                    writeconnection = (HttpURLConnection) url2.openConnection();
                    writeconnection.setRequestMethod("POST");
                    //writeconnection.setRequestProperty("Authorization", authHeader);
                    writeconnection.setRequestProperty("X-CSRF-Token", xsrfToken);
                    setSessionCookies(writeconnection, session);
                    writeconnection.setRequestProperty("X-HTTP-METHOD", "MERGE");
                    writeconnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    writeconnection.setRequestProperty("Accept", "application/json");
                    writeconnection.setRequestProperty("Content-Length", String.valueOf(payloadBytes.length));
                    //writeconnection.connect();
                    writeconnection.setDoInput(true);
                    writeconnection.setDoOutput(true);


                    try (OutputStream os = writeconnection.getOutputStream()) {
                        byte[] input = jsonString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int writeresponseCode = writeconnection.getResponseCode();
                    System.out.println("writeresponseCode:" + writeresponseCode);

                    if (writeresponseCode == HttpURLConnection.HTTP_OK) {

                        try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(writeconnection.getInputStream(), "utf-8"))) {
                            StringBuilder writeresponse = new StringBuilder();
                            String responseLine = null;
                            while ((responseLine = br.readLine()) != null) {
                                writeresponse.append(responseLine.trim());
                            }

                            System.out.println("write Success response: " + writeresponse);
                        }

                    } else {
                        System.out.println("Write Error: " + writeresponseCode);
                        try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(writeconnection.getInputStream(), "utf-8"))) {
                            StringBuilder writeresponse = new StringBuilder();
                            String responseLine = null;
                            while ((responseLine = br.readLine()) != null) {
                                writeresponse.append(responseLine.trim());
                            }

                            System.out.println("write Error response: " + writeresponse);
                        }

                    }

                    writeconnection.disconnect();


                } else {
                    System.out.println("Read Error: " + responseCode);
                }

            } catch (ProtocolException ex) {
                throw new RuntimeException(ex);
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return null;
        }


    private static String extractXsrfToken(HttpURLConnection readconnection) {

        System.out.println("MEthod Execution: extractXsrfToken");

        List<String> value = null;
        Map<String, List<String>> headers = readconnection.getHeaderFields();
        Iterator<String> keys = headers.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            if ("X-CSRF-Token".equalsIgnoreCase(key)) {
                value = headers.get(key);
            }
        }

        if (value == null || value.size() == 0) {
            return null;
        } else {
            System.out.println("extractXsrfToken get value: "+value.get(0));
            return value.get(0);
        }

    }

    private static final List<String> getSessionCookies(HttpURLConnection conn) {
        System.out.println("MEthod Execution: getSessionCookies");
        Map<String, List<String>> response_headers = conn.getHeaderFields();
        Iterator<String> keys = response_headers.keySet().iterator();
        String key;
        while (keys.hasNext()) {
            key = keys.next();
            if ("set-cookie".equalsIgnoreCase(key)) {
                List<String> session = response_headers.get(key);
                System.out.println("getSessionCookies session: " + session);
                return session;
            }
        }

        // no session
        return null;
    }

    private static final void setSessionCookies(HttpURLConnection writeconnection, List<String> session) {
        System.out.println("MEthod Execution: setSessionCookies");
        if (session != null) {
            String agregated_cookies = "";
            for (String cookie: session) {
                agregated_cookies += cookie + "; ";
            }
            writeconnection.setRequestProperty("cookie", agregated_cookies);
            System.out.println("setSessionCookies cookie: " + agregated_cookies);
        }
}
}
