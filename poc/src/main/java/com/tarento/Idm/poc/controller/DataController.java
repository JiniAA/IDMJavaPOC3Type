package com.tarento.Idm.poc.controller;

import com.tarento.Idm.poc.DBconnection;
import com.tarento.Idm.poc.service.DataService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/getData")
public class DataController {


    @Autowired
    DBconnection dBconnection;


   @GetMapping("/{endPoint}")
    public Map<String, List<Map<String, Object>>> dynamicQueryExecuter(@PathVariable("endPoint") String endPoint) throws JSONException, IOException, SQLException {
        return dBconnection.readQueryEndPoint(endPoint);
    }
    @PostMapping("/postData/{tableName}")
    public void postDataToDB(@PathVariable("tableName") String tableName,
                             @RequestParam("columnNames")List<String> columnNames,

                             @RequestBody List<List<Object>> data) throws SQLException {
       dBconnection.postDataToDB(tableName,columnNames,data);
    }
    @PostMapping("/pushData")
    public void transferDataAtoB() throws SQLException, IOException {
       dBconnection.dataTransferAToB();
    }
}
