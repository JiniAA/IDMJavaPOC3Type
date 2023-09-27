package com.idm.tarento.RestApiConnector.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.management.ManagementPermission;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DataGetService {
    @Autowired
    private RestTemplate restTemplate;
    public String getEntry(String Id) {
        String url = urlGeneratorForGet(Id);
        HttpHeaders headers = new HttpHeaders();
        try {
            headers.setBasicAuth("devadmin", "IdM_2019");
            headers.set("Accept", "application/json");
            headers.set("X-CSRF-Token", "Fetch");
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            String csrfToken = response.getHeaders().getFirst("X-CSRF-Token");
            System.out.println(response.getBody());
            //return new ResponseEntity<>(csrfToken,HttpStatus.ACCEPTED);
            return csrfToken;

        }catch (Exception e){
            //return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            return e.getMessage()+HttpStatus.BAD_REQUEST;
        }

    }

    public ResponseEntity<?> updateEntry(String Id){

        String postUrl= urlGeneratorForPost(Id);
        System.out.println("posturl: "+postUrl);

        try{
           // ResponseEntity<?> responseEntity =getEntry(Id);
            String csrfToken =getEntry(Id);
            System.out.println(csrfToken);
            Charset charset = StandardCharsets.UTF_8;
            String contentType = "application/json";
           // String csrfToken= "Mp_KMX0W_VBp8mUypQTmCgSefl54H02EO28";
            //calling post api and posting the data
            HttpHeaders postHeaders = new HttpHeaders();
            postHeaders.setBasicAuth("devadmin", "IdM_2019");
            postHeaders.set("X-CSRF-Token", csrfToken);
           // postHeaders.setContentType(MediaType.APPLICATION_JSON);
            MediaType mediaType = new MediaType(MediaType.parseMediaType(contentType), charset);
            postHeaders.setContentType(mediaType);
            //postHeaders.setContentType(MediaType.parseMediaType(contentType));
            postHeaders.set("Accept", "application/json");
            //Passing value to the body
            //Object postBody;
            Map<String, Object> postBody = new HashMap<>();
            postBody.put("SV_MX_MAIL_PRIMARY","deva.benz@example3.com");
            HttpEntity <Map<String, Object>> postEntity = new HttpEntity<>(postBody, postHeaders);
            ResponseEntity<String> postResponse = restTemplate.exchange(postUrl, HttpMethod.POST, postEntity, String.class);
            HttpStatus postStatusCode = (HttpStatus) postResponse.getStatusCode();
            String postResponseBody = postResponse.getBody();

            System.out.println("POST Status Code: " + postStatusCode);
            System.out.println("POST Response Body: " + postResponseBody);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
return null;
    }

    public String urlGeneratorForGet(String Id) {
        String getApi = "http://vmi320299.contaboserver.net:50600/idmrestapi/v2/service/ET_MX_PERSON(ID=" + Id + ",TASK_GUID=guid'EF8ADD60-BE32-4D68-8785-570CDEE57999')";
        System.out.println(getApi);


        return getApi;
    }
    public String urlGeneratorForPost(String Id){
        String postApi="http://vmi320299.contaboserver.net:50600/idmrestapi/v2/service/ET_MX_PERSON(ID="+Id+",TASK_GUID=guid'62352952-10D0-4648-B7DC-F5CB32BBA367')";

        return postApi;
    }
}
