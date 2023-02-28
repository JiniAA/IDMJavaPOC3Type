package com.tarento.Idm.poc.service;

import com.tarento.Idm.poc.connection.DBconnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Service
public class DataPostService {


    DBconnection dBconnection;

    final static String filePath3
            = "C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\DbConnectionDetails3.txt";

    public void postDataToDB(String tableName, List<String> columnNames, List<List<Object>> data) throws SQLException {

        Connection connection = dBconnection.readConnectionDetails(filePath3);
        tableInsertQueryExecuter(columnNames, data, connection, tableName);

    }

    public void tableInsertQueryExecuter(List<String> columnNames, List<List<Object>> data, Connection connection, String tableName) throws SQLException {
        try {
            String insertColumns = "";
            String insertValues = "";

            if (columnNames != null && columnNames.size() > 0) {
                insertColumns += columnNames.get(0);
                insertValues += "?";
            }

            for (int i = 1; i < columnNames.size(); i++) {
                insertColumns += ", " + columnNames.get(i);
                insertValues += ", " + "?";
            }

            String insertSql = "INSERT INTO " + tableName + " (" + insertColumns + ") values(" + insertValues + ")";

            PreparedStatement ps = connection.prepareStatement(insertSql);

            for (List<Object> o : data) {
                int i = 1;
                for (Object p : o) {
                    if (p != null)
                        ps.setString(i, p.toString());
                    else
                        ps.setString(i, "");
                    i++;
                }
                int count = ps.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
