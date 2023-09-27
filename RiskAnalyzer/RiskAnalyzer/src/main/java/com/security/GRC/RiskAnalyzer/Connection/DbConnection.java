package com.security.GRC.RiskAnalyzer.Connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

@Component
public class DbConnection {
    //    public Connection callDbconnect {
    public Connection callDbconnect()
    {
        try {
           // ObjectMapper objectMapper = new ObjectMapper();
            String user="root";
            String password="Jiniaa@1996";
            String driver="com.mysql.cj.jdbc.Driver";
            String url="jdbc:mysql://localhost:3306/grc_rules";
            Connection connection = connectDb(url, user, password,driver );
            return connection;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
