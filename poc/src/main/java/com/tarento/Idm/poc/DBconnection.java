package com.tarento.Idm.poc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.List;

//import static sun.tools.jstat.Alignment.keySet;
//import org.json.simple.JSONValue;

@Component
public class DBconnection {




    final static String filePath1
            = "C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\DbConnectionDetails.txt";
    final static String filePath2
            = "C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\DbConnectionDetails2.txt";
    final static String filePath3
            = "C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\DbConnectionDetails3.txt";
    final static String idmFilePath3
            = "C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\idmDBConnectionDetails.txt";
    final static String queryfile
            = "C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\EndPointsAndQueries.json";
    final static String IDMqueryfile
            = "C:\\JiniAA\\IDM\\POCs\\Notesofpoc\\IdmqueryAndEndpoints.json";
    public Connection readConnectionDetails(String file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            connectEstablish connectEstablish = objectMapper.readValue(new File(file), DBconnection.connectEstablish.class);
            String  url = "jdbc:" + connectEstablish.database + "://" + connectEstablish.host + ":" + connectEstablish.port + "/" + connectEstablish.schema;
          //String url="jdbc:" +connectEstablish.database +"://"+ connectEstablish.host+"\\"
           //       +connectEstablish.instancename+";databaseName="+connectEstablish.schema;
          //jdbc:sqlserver://164.68.114.67\\sqlexpress;databaseName=MXMC_DB

            Connection connection = connectDb(url, connectEstablish.userid, connectEstablish.password, connectEstablish.Driver);
            System.out.println(url);
            return connection;
    }
    catch (IOException | ClassNotFoundException | SQLException e) {
        throw new RuntimeException(e);
    } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void postDataToDB(String tableName, List<String> columnNames, List<List<Object>> data) throws SQLException {

        Connection connection =readConnectionDetails(filePath3);
        tableInsertQueryExecuter(columnNames,data,connection,tableName);

    }

    public record connectEstablish(String host,String port,String database,String schema,String userid,String password,String Driver){

    }

    public Map<String, List<Map<String, Object>>> queryStatement(String sql, String endPoint) throws IOException, SQLException {
        Connection connection =readConnectionDetails(filePath1);
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            /* CallableStatement callableStatement =connection.prepareCall(sql);
            ResultSet resultSet=callableStatement.getResultSet();*/

        ResultSet resultSet = statement.executeQuery(sql);

        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnCount = rsmd.getColumnCount();

        Map<String,List<Map<String, Object>>> table = new HashMap<>();

        int rowCount = 0;
        if (resultSet.last()) {//make cursor to point to the last row in the ResultSet object
            rowCount = resultSet.getRow();
            resultSet.beforeFirst(); //make cursor to point to the front of the ResultSet object, just before the first row.
        }

        while (resultSet.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                // Note that the index is 1-based
                String colName = rsmd.getColumnName(i);
                Object colVal = resultSet.getObject(i);
                row.put(colName, colVal);
            }
            rows.add(row);
        }
        table.put(endPoint,rows);

        return table;



    }
    public Connection connectDb(String url,String user,String password,String driver) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        Connection connection = null;
       Class.forName(driver);

        Properties properties = new Properties();
        properties.put("user", user);
        properties.put("password", password);
        properties.put("connectTimeout",10000);


        connection= DriverManager.getConnection(url,properties);

        return connection;

    }

    public Map<String, List<Map<String, Object>>> readQueryEndPoint(String endPoint) throws IOException, JSONException, SQLException {
        InputStream inputStream=new FileInputStream(new File(queryfile));
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<String,String>> jsonMap =  mapper.readValue(inputStream,Map.class);
        Set<String> keyss = jsonMap.keySet();


        for (String key : keyss) {

            if(key.equals(endPoint)) {
                Map<String,String> value = jsonMap.get(key);
                 String query=   value.get("Query");

              return   queryStatement(query,endPoint);
            }


        }

        return null;
    }
public void dataTransferAToB() throws SQLException, IOException {
    Connection connectionA =readConnectionDetails(filePath1);
    Connection connectionB = readConnectionDetails(filePath2);
    Statement statementA = connectionA.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

    Statement statementB = connectionB.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);


    for(String tableName : dataBaseTableName()) {

         Boolean deleteExist = statementB.execute("DELETE FROM " + tableName);
        ResultSet resultSet = statementA.executeQuery("SELECT * FROM " + tableName);

        List<String> columnNames = buildTableModel(resultSet);
        List<List<Object>> data = buildTableData(resultSet);
        tableInsertQueryExecuter(columnNames,data,connectionB,tableName);
    }

 }

public Set<String> dataBaseTableName() throws IOException {
    InputStream inputStream=new FileInputStream(new File(queryfile));
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Map<String,String>> jsonMap =  mapper.readValue(inputStream,Map.class);
    Set<String> tableNames = jsonMap.keySet();

  return tableNames;
}
    public static List<String> buildTableModel(ResultSet rs)
            throws SQLException {

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


    }
    public static List<List<Object>> buildTableData(ResultSet rs)
            throws SQLException {

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

    }

    public void tableInsertQueryExecuter(List<String> columnNames,List<List<Object>> data,Connection connection,String tableName) throws SQLException {

        String insertColumns = "";
        String insertValues = "";

        if(columnNames != null && columnNames.size() > 0){
            insertColumns += columnNames.get(0);
            insertValues += "?";
        }


        for(int i = 1; i < columnNames.size();i++){
            insertColumns += ", " + columnNames.get(i) ;
            insertValues +=", " + "?";
        }

        String insertSql = "INSERT INTO "+tableName+" (" + insertColumns + ") values(" + insertValues + ")";

        PreparedStatement ps = connection.prepareStatement(insertSql);


        for(List<Object> o : data) {
            int i = 1;
            for (Object p : o) {
                ps.setString(i, (String) p);
                i++;
            }
            int count = ps.executeUpdate ();
        }


    }


}

