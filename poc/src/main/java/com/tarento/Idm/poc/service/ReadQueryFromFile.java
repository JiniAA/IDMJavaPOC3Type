/*
package com.tarento.Idm.poc.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tarento.Idm.poc.DBconnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service

public class ReadQueryFromFile {

    @Autowired
    TemplateParser templateParser;
    final static String IDMquery_TemplateFile
            = "C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\IDMquery_Template.json";
    public Map<String, List<Map<String, Object>>> readQueryEndPoint(String endPoint) throws IOException, SQLException {
        //InputStream inputStream=new FileInputStream(new File(queryfile));
        InputStream inputStream = new FileInputStream(new File(IDMquery_TemplateFile));
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<String, String>> jsonMap = mapper.readValue(inputStream, Map.class);
        System.out.println(jsonMap);
        Set<String> keyss = jsonMap.keySet();
        for (String key : keyss) {
            if (key.equals(endPoint)) {
                Map<String, String> value = jsonMap.get(key);
                String query = value.get("Query");
                String Template_flag = value.get("templateFile");
                String Template_path = value.get("jsonResponseTemplate_filePath");
                if (Template_flag.equals("Y")) {
                    InputStream templateStream = new FileInputStream(new File(Template_path));
                    ObjectMapper TemplateMapper = new ObjectMapper();
                    mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
                    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                    // String jsonMap_template =  mapper.readValue(templateStream,String.class);
                    String jsonMap_template = mapper.writeValueAsString(templateStream);
                    System.out.println(jsonMap_template);
                    Object object = queryStatement(query, endPoint);
                    String res = templateParser.parse("template", jsonMap_template, object);
                    System.out.println(res);
                }
                return queryStatement(query, endPoint);
            }
        }
        return null;
    }

}
*/
