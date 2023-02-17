package com.tarento.Idm.poc.controller;

import com.tarento.Idm.poc.connection.DBconnection;

import com.tarento.Idm.poc.service.DataGetService;
import com.tarento.Idm.poc.service.DataPostService;
import com.tarento.Idm.poc.service.DataPushService;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/getData")
public class DataController {


    @Autowired
    DBconnection dBconnection;

    @Autowired
    DataPostService dataPostService;

    @Autowired
    DataPushService dataPushService;

    @Autowired
    DataGetService dataGetService;


   @GetMapping("/{endPoint}")
    public List<Object> dynamicQueryExecuter(@PathVariable("endPoint") String endPoint) throws JSONException, IOException, SQLException, ParseException {
        return dataGetService.readQueryEndPoint(endPoint);
    }
   // Map<String, List<Map<String, Object>>>
    @PostMapping("/postData/{tableName}")
    public void postDataToDB(@PathVariable("tableName") String tableName,
                             @RequestParam("columnNames")List<String> columnNames,

                             @RequestBody List<List<Object>> data) throws SQLException {
       dataPostService.postDataToDB(tableName,columnNames,data);
    }
    @PostMapping("/pushData")
    public void transferDataAtoB() throws SQLException, IOException {
       dataPushService.dataTransferAToB();
    }
}
