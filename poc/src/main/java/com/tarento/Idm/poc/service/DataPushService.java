package com.tarento.Idm.poc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarento.Idm.poc.connection.DBconnection;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

@Service
public class DataPushService {

    DBconnection dBconnection;

    DataPostService dataPostService;

    final static String filePath1
            = "C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\DbConnectionDetails.txt";
    final static String filePath2
            = "C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\DbConnectionDetails2.txt";

    final static String queryfile
            = "C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\EndPointsAndQueries.json";

    public void dataTransferAToB() {
        try {
            Connection connectionA = dBconnection.readConnectionDetails(filePath1);
            Connection connectionB = dBconnection.readConnectionDetails(filePath2);
            Statement statementA = connectionA.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            Statement statementB = connectionB.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            for (String tableName : dataBaseTableName()) {
                Boolean deleteExist = statementB.execute("DELETE FROM " + tableName);
                ResultSet resultSet = statementA.executeQuery("SELECT * FROM " + tableName);

                List<String> columnNames = buildTableModel(resultSet);
                List<List<Object>> data = buildTableData(resultSet);
                dataPostService.tableInsertQueryExecuter(columnNames, data, connectionB, tableName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public Set<String> dataBaseTableName() {
        try {
            InputStream inputStream = new FileInputStream(new File(queryfile));
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Map<String, String>> jsonMap = mapper.readValue(inputStream, Map.class);
            Set<String> tableNames = jsonMap.keySet();

            return tableNames;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<String> buildTableModel(ResultSet rs) throws SQLException {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            // names of columns
            Vector<String> columnNames = new Vector<String>();
            int columnCount = metaData.getColumnCount();
            List<String> columnName = new ArrayList<>();

            for (int column = 1; column <= columnCount; column++) {
                columnNames.add(metaData.getColumnName(column));
                columnName.add(metaData.getColumnName(column));
            }
            return columnName;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<List<Object>> buildTableData(ResultSet rs) throws SQLException {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<List<Object>> data = new ArrayList<>();
            while (rs.next()) {
                List<Object> vector = new Vector<Object>();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    vector.add(rs.getObject(columnIndex));
                }

                data.add(vector);
            }
            return data;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
