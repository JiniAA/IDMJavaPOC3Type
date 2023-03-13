package com.tarento.Idm.poc.controller;

import com.tarento.Idm.poc.connection.DBconnection;

import com.tarento.Idm.poc.service.DataGetService;
import com.tarento.Idm.poc.service.DataPostService;
import com.tarento.Idm.poc.service.DataPushService;
import com.tarento.Idm.poc.util.BasicAuthAndApiKeyFilterAndAuthenticator;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DataController {
    private static final Logger logger = LoggerFactory.getLogger(DataController.class);



    @Autowired
    DBconnection dBconnection;

    @Autowired
    DataPostService dataPostService;

    @Autowired
    DataPushService dataPushService;

    @Autowired
    DataGetService dataGetService;

    @Autowired
    BasicAuthAndApiKeyFilterAndAuthenticator Authfilter;


   @GetMapping("/{endPoint}")
    public ResponseEntity dynamicQueryExecuter(@PathVariable("endPoint") String endPoint, HttpServletRequest request) throws JSONException, IOException, SQLException, ParseException {
             if(Authfilter.DoFilter(request)) {
                 logger.info("Authentication successful");
                 return dataGetService.readQueryEndPoint(endPoint);
             }
             else {
                 logger.info("Unauthorized user login");
                 logger.error("Unauthorized Error");
                 Map<String,String> errorMap=new HashMap<>();
                 errorMap.put("ErrorCode",HttpStatus.UNAUTHORIZED.toString());
                 errorMap.put("message","UNAUTHORIZED");
                 return new ResponseEntity<>(errorMap,HttpStatus.UNAUTHORIZED);

             }
    }

    @PostMapping("/postData/{tableName}")
    public void postDataToDB(@PathVariable("tableName") String tableName,
                             @RequestParam("columnNames")List<String> columnNames,

                             @RequestBody List<List<Object>> data) throws SQLException {
       dataPostService.postDataToDB(tableName,columnNames,data);
    }
    @PostMapping("/pushData")
    public ResponseEntity<?> transferDataAtoB() throws SQLException, IOException {
      return dataPushService.dataTransferAToB();
    }
}
