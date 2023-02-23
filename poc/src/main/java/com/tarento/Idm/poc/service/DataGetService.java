package com.tarento.Idm.poc.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.tarento.Idm.poc.connection.DBconnection;
import com.tarento.Idm.poc.util.TemplateParser;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

//import static javax.management.MBeanServerFactory.builder;

@Service
public class DataGetService {

    @Autowired
    DBconnection dBconnection;
    @Autowired
    TemplateParser templateParser;

    final static String idmFilePath3
            = "C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\idmDBConnectionDetails.txt";
    final static String IDMquery_TemplateFile
            = "C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\IDMquery_Template.json";


    public Map<String, List<Map<String, Object>>> queryStatement(String sql, String endPoint) {
        try {
            Connection connection = dBconnection.readConnectionDetails(idmFilePath3);
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            ResultSet resultSet = statement.executeQuery(sql);

            List<Map<String, Object>> rows = new ArrayList<>();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();

            Map<String, List<Map<String, Object>>> table = new HashMap<>();
            int rowCount = 0;
            if (resultSet.last()) {//make cursor to point to the last row in the ResultSet object
                rowCount = resultSet.getRow();
                resultSet.beforeFirst(); //make cursor to point to the front of the ResultSet object, just before the first row.
            }
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    // Note that the index is 1-based
                    String colName = rsmd.getColumnName(i);
                    Object colVal = resultSet.getObject(i);
                    row.put(colName, colVal);
                }
                rows.add(row);
            }
            table.put(endPoint, rows);
            return table;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String,Object> readQueryEndPoint(String endPoint) throws IOException, ParseException, JSONException {

        InputStream inputStream = new FileInputStream(new File(IDMquery_TemplateFile));
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<String, String>> jsonMap = mapper.readValue(inputStream, Map.class);
        System.out.println(jsonMap);
        Set<String> keyss = jsonMap.keySet();
        List<Object> response = new ArrayList<>();
        Map<String,Object>Response=new HashMap<>();
        int flag = 0;
        for (String key : keyss) {
            if (key.equals(endPoint)) {
                Map<String, String> value = jsonMap.get(key);
                String query = value.get("Query");
                String Template_flag = value.get("templateFile");
                String Template_path = value.get("jsonResponseTemplate_filePath");
                String ParentNode;
                if (Template_flag.equals("Y")) {
                    ObjectMapper TemplateMapper = new ObjectMapper();
                    GsonBuilder builder = new GsonBuilder();
                    builder.serializeNulls();
                    Gson gson = builder.setPrettyPrinting().create();
                    String jsonMap_template = new String(Files.readAllBytes(Paths.get(Template_path)));
                    JSONParser parser1 = new JSONParser(jsonMap_template);
                    Object template = parser1.parse();
                    String jsonInString = gson.toJson(template);
                    JSONObject templateObject = new JSONObject(jsonInString);
                    System.out.println("templateObject" + templateObject);
                    JSONObject template_body = (JSONObject) templateObject.get("body");
                    ParentNode= (String) templateObject.get("parentNode");
                    Map<String, List<Map<String, Object>>> object = queryStatement(query, endPoint);
                    Collection<List<Map<String, Object>>> objectList = object.values();
                    for (List<Map<String, Object>> datas : objectList) {
                        System.out.println("datas" + datas);
                        for (Map<String, Object> data : datas) {
                            flag = flag + 1;
                            String res = templateParser.parse("template_dispatcher_001", template_body.toString(), data);
                            JSONParser parser = new JSONParser(res);
                            Object json = parser.parse();
                            response.add(json);

                        }
                    }
                    Response.put("Total_entries",flag);
                    Response.put(ParentNode,response);
                    return Response;

//                    Map<String, String> map = new HashMap<>();
//                    map.put("ParentNode", endPoint);
//                    map.put("Total_entries", String.valueOf(flag));
//                    Object obj = new Object();
//                    obj = map;
//                    response1.add(obj);
//                    response1.add(response);
//                    return response1;

          }
                else {
                    Map<String, List<Map<String, Object>>> object = queryStatement(query, endPoint);
                    Collection<List<Map<String, Object>>> objectList = object.values();
                    //return Collections.singletonList(objectList);
                    for (List<Map<String, Object>> datas : objectList) {
                        System.out.println("datas" + datas);
                        for (Map<String, Object> data : datas) {
                            flag = flag + 1;
                            response.add(data);
                        }
                    }
                    Response.put("Total_entries",flag);
                    Response.put(endPoint,response);
                    return Response;                }
                // return response;
            }
        }
        return null;
    }
}
