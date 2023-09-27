package com.security.GRC.RiskAnalyzer.Controller;

import com.security.GRC.RiskAnalyzer.Connection.DbConnection;
import com.security.GRC.RiskAnalyzer.Services.DataGetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/read")
public class DataController {
    @Autowired
    DbConnection dbConnection;
    @Autowired
    DataGetService dataGetService;

    @GetMapping("/get/{role}")
    public ResponseEntity<?> readData(@PathVariable("role") String Role) throws SQLException {
   // public ResponseEntity<?> readData(@RequestBody List Role) throws SQLException {
    return (ResponseEntity<?>) dataGetService.execute(Role);
       // dataGetService.manageMultipleRolesAsInput(Role);
        //return null;
    }
    @GetMapping("/getList")
    public ResponseEntity<?> readListOfData(@RequestBody List<String> Role) throws SQLException {
        return dataGetService.manageMultipleRolesAsInput(Role);
    }
    @GetMapping("/buildRuleSet")
    public ResponseEntity<?> BuildRuleSet() throws SQLException {
        return dataGetService.buildTable();
    }
    @GetMapping("/getAll")
    public ResponseEntity<?> readAllData() throws SQLException {
        // public ResponseEntity<?> readData(@RequestBody List Role) throws SQLException {
        return dataGetService.getAllRoles();
        // dataGetService.manageMultipleRolesAsInput(Role);
        //return null;
    }
    @GetMapping("/getUserLevelRisk")
    public ResponseEntity<?> readUserLevelRisks(@RequestBody String user) throws SQLException {
        //return dataGetService.getUserRoles(user);
        return dataGetService.fetchTcodeCrossjoinForUsers(user);

    }
}
