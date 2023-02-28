package com.tarento.Idm.poc.service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarento.Idm.poc.connection.DBconnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.*;
import java.util.*;

@Service
public class DataPushService {
    @Autowired
    DBconnection dBconnection;
    @Autowired
    DataPostService dataPostService;

    final static String DatabaseAconnectionDetails
            = "C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\DbConnectionDetailsForDatabaseA.txt";
    final static String DatabaseBonnectionDetails
            = "C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\DbConnectionDetailsforDatabaseB.txt";

    final static String TablesWithqueriesfile
            = "C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\TableNamesandQueries.json";

    public ResponseEntity<?> dataTransferAToB() {
        try {
            Connection connectionA = dBconnection.readConnectionDetails(DatabaseAconnectionDetails);
            Connection connectionB = dBconnection.readConnectionDetails(DatabaseBonnectionDetails);
            Statement statementA = connectionA.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            Statement statementB = connectionB.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            Map<String, Map<String, String>> endpointQuerymap = readTablenameQuery();
            Set<String> tables = endpointQuerymap.keySet();
            for (String tableName : tables) {
                Boolean deleteExist = statementB.execute("DELETE FROM " + tableName);
                ResultSet resultSet = statementA.executeQuery(endpointQuerymap.get(tableName).get("Query"));
                List<String> columnNames = buildTableModel(resultSet);
                List<List<Object>> data = buildTableData(resultSet);
                dataPostService.tableInsertQueryExecuter(columnNames, data, connectionB, tableName);
            }
            return new ResponseEntity<>("Success", HttpStatus.ACCEPTED);
        } catch (SQLException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Map<String, Map<String, String>> readTablenameQuery() throws IOException {
        InputStream inputStream = new FileInputStream(new File(TablesWithqueriesfile));
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<String, String>> jsonMap = mapper.readValue(inputStream, Map.class);
        Set<String> tableNames = jsonMap.keySet();
        return jsonMap;
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
