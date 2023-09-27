package com.idm.tarento.RestApiConnector.Controller;

import com.idm.tarento.RestApiConnector.Services.DataGetService;
import com.idm.tarento.RestApiConnector.Services.DataPostService;
import com.idm.tarento.RestApiConnector.Services.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLDataException;

@RestController
@RequestMapping("/api")
public class IdmController {
    @Autowired
    DataGetService dataGetService;
    @Autowired
    DataPostService dataPostService;
    @Autowired
    TestService testService;

        @GetMapping("/getSpecificEntity")
    public String readData(@RequestBody String id) throws SQLDataException {

        return dataGetService.getEntry(id);
    }
    @PostMapping("/updateEntity")
    public ResponseEntity<?> updateData(@RequestBody String id) throws SQLDataException {

        return dataGetService.updateEntry(id);
    }

    @GetMapping("/push")
    public ResponseEntity<?> dataPush() throws SQLDataException {

         return testService.executeFunction();
    }

}
