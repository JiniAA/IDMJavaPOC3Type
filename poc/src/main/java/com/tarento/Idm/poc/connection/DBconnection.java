package com.tarento.Idm.poc.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.*;
import java.util.*;
//import static sun.tools.jstat.Alignment.keySet;
//import org.json.simple.JSONValue;

@Component
public class DBconnection {

    public Connection readConnectionDetails(String file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            connectEstablish connectEstablish = objectMapper.readValue(new File(file), DBconnection.connectEstablish.class);
            String url = urlGenerator(connectEstablish);
            Connection connection = connectDb(url, connectEstablish.userid, connectEstablish.password, connectEstablish.Driver);
            return connection;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String urlGenerator(connectEstablish connectEstablish) {
        try {
            String JdbcUrl = null;
            if (connectEstablish.database.equals("sqlserver")) {
                JdbcUrl = "jdbc:" + connectEstablish.database + "://" + connectEstablish.host + ":" + connectEstablish.port + ";databaseName=" + connectEstablish.schema + ";encrypt=true;trustServerCertificate=true";
            } else if (connectEstablish.database.equals("mysql")) {
                JdbcUrl = "jdbc:" + connectEstablish.database + "://" + connectEstablish.host + ":" + connectEstablish.port + "/" + connectEstablish.schema;
            } else if (connectEstablish.database.equals("oracle")) {
                JdbcUrl = "jdbc:" + connectEstablish.database + ":" + connectEstablish.Driver + ":" + "@" + connectEstablish.host + ":" + connectEstablish.port + ":" + connectEstablish.schema;
            }
            return JdbcUrl;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //creating a record to hold all the connection details
    public record connectEstablish(String host, String port, String database, String schema, String userid,
                                   String password, String Driver, String instancename) {
    }

    public Connection connectDb(String url, String user, String password, String driver) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        Connection connection = null;
        Class.forName(driver);
        Properties properties = new Properties();
        properties.put("user", user);
        properties.put("password", password);
        properties.put("connectTimeout", 20000);
        connection = DriverManager.getConnection(url, properties);

        return connection;
    }

}

