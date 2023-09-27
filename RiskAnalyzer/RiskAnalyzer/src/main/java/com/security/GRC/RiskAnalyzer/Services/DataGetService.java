package com.security.GRC.RiskAnalyzer.Services;

import com.security.GRC.RiskAnalyzer.Connection.DbConnection;
import com.security.GRC.RiskAnalyzer.common.Common;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Date;
import java.util.*;


@Service
public class DataGetService {
    private static final Logger logger = LoggerFactory.getLogger(DataGetService.class);
    @Autowired
    DbConnection dbConnection;
    public Integer flag = 0;

    public ResponseEntity<?> manageMultipleRolesAsInput(List<String> inputList) {
        try {
            int inputSize = inputList.size();
            List<List<List<String>>> result = new ArrayList<>();
            for (int i = 0; i < inputSize; i++) {
                String input = inputList.get(i);
                result.add(execute(input));
            }
            excelGen(result);
            return new ResponseEntity<>("success", HttpStatus.OK);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public ResponseEntity<String> getUserRoles(String user) {
        try {
            String fetchAllRoles = "SELECT DISTINCT ROLES_ASSIGNED FROM USERSROLES_MAPPING WHERE USERID='" + user + "'";
            Connection connection = dbConnection.callDbconnect();
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery(fetchAllRoles);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            List<List<List<String>>> result = new ArrayList<>();
            List<String> tcodeList = new ArrayList<>();
            //user exists pr not
            if (!resultSet.isBeforeFirst()) {
                return new ResponseEntity<>("No Data for this user", HttpStatus.BAD_REQUEST);
            } else {
                while (resultSet.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        String colVal = (String) resultSet.getObject(i);
                        //Roles.add(colVal);
                        //result.add(execute(colVal));
                        //add the list into the list as a single list
                        tcodeList.addAll(fetchTcodesForUsers(colVal));

                    }
                }
                List<String> crossTcodes = performCrossJoin(tcodeList);
                //excelGen(result);
            }
        } catch (Exception e) {
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    public ResponseEntity<String> fetchTcodeCrossjoinForUsers(String user) {
        List<List<List<String>>> finalResult = new ArrayList<>();
        try {
            String query = "SELECT\n" +
                    "    c1.NAME_ROLE AS role1,\n" +
                    "    c1.tcodes AS tcode1,\n" +
                    "    c2.NAME_ROLE AS role2,\n" +
                    "    c2.tcodes AS tcode2,\n" +
                    "    CONCAT(c1.tcodes, \"|\", c2.tcodes) AS search_pattern1,\n" +
                    "    CONCAT(c2.tcodes, \"|\", c1.tcodes) AS search_pattern2\n" +
                    "FROM\n" +
                    "    GRC_ROLES c1  \n" +
                    "CROSS JOIN\n" +
                    "    GRC_ROLES c2\n" +
                    "    where c1.NAME_ROLE IN (select distinct ROLES_ASSIGNED from usersroles_mapping where USERID=" + "'" + user + "')" +
                    "AND c2.NAME_ROLE IN (select distinct ROLES_ASSIGNED from usersroles_mapping where USERID=" + "'" + user + "')";

            Connection connection = dbConnection.callDbconnect();
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery(query);

            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (resultSet.next()) {
                List<String> temprow = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    String colVal = (String) resultSet.getObject(i);
                    temprow.add(colVal);
                }

                String temp = temprow.get(columnCount - 2); // Getting the "search_pattern1" value
                String role1 = temprow.get(columnCount - 6);
                String role2 = temprow.get(columnCount - 4);
                //System.out.println("roles"+role1+":"+role2+":");
                String compareRuleset = "SELECT BUSINESSPROCESS,RISK_LEVEL,RISK_TYPE,RISK_ID,FUNCTION_NAME1,DESCRIPTION1,TCODE1,TCODE1_description,FUNCTION_NAME2,DESCRIPTION2,TCODE2,TCODE2_description FROM RULESET WHERE TcodePair1=" + "'" + temp + "' OR TcodePair2=" + "'" + temp + "'";
                Statement secondStatement = connection.createStatement(); // Create a separate statement for the second query
                ResultSet resultSet2 = secondStatement.executeQuery(compareRuleset);
                if (!resultSet2.isBeforeFirst()) {
                    //System.out.println("no");
                } else {
                    List<List<String>> result = new ArrayList<>();
                    ResultSetMetaData rsmd2 = resultSet2.getMetaData();
                    int columnCount2 = rsmd2.getColumnCount();
                    while (resultSet2.next()) {
//                        List<String> rulesetList = new ArrayList<>(temprow);
                        List<String> rulesetList = new ArrayList<>();
                        rulesetList.add(user);
                        for (int j = 1; j <= columnCount2; j++) {
                            String colVal = resultSet2.getString(j);
                            rulesetList.add(colVal);
                        }
                        rulesetList.add(Common.headers.indexOf("Role1"),role1);
                        rulesetList.add(Common.headers.indexOf("Role2"),role2);
                        result.add(rulesetList);
                    }
                    finalResult.add(result);
                }

                resultSet2.close(); // Close the second ResultSet
                secondStatement.close(); // Close the second Statement
            }

            resultSet.close();
            statement.close();
            connection.close();

            //excelGen(finalResult);
            excelGenForUsers(finalResult);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }


    public List<String> fetchTcodesForUsers(String role) {
        List<String> tcodes = new ArrayList<>();
        try {
            String query = "SELECT TCODES from GRC_ROLES where NAME_ROLE =" + "'" + role + "'";
            Connection connection = dbConnection.callDbconnect();
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery(query);

            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String colVal = (String) resultSet.getObject(i);
                    tcodes.add(colVal);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tcodes;
    }

    public ResponseEntity<String> getAllRoles() throws SQLException {
        try {
            String fetchAllRoles = "SELECT DISTINCT NAME_ROLE FROM GRC_ROLES";
            Connection connection = dbConnection.callDbconnect();
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery(fetchAllRoles);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            List<List<List<String>>> result = new ArrayList<>();
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String colVal = (String) resultSet.getObject(i);
                    result.add(execute(colVal));
                }
            }
            excelGen(result);
        } catch (Exception ex) {
            logger.error(String.valueOf(ex));
            throw new RuntimeException(ex);
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    public List<List<String>> execute(String Role) throws SQLException {

        String res = "success";
        return getTcodesAndanalyseRisk(Role);
    }

    public List<Map<String, Object>> getFunctions(List<String> tc, Statement stmnt) {
        try {
            int tcodesLen = tc.size();
            List<Map<String, Object>> rows = new ArrayList<>();
            for (int i = 0; i <= tcodesLen - 1; i++) {
                String temp = (String) tc.get(i);
                String Query2 = "SELECT FUNCTION_NAME,DESCRIPTION,tcode_description FROM FUNCTIONS WHERE TCODE=" + "'" + temp + "'";
                ResultSet resultSet = stmnt.executeQuery(Query2);
                ResultSetMetaData rsmd = resultSet.getMetaData();
                // List<Map<String, Object>> rows = new ArrayList<>();
                //int colCount = rsmd.getColumnCount();
                int columnCount = rsmd.getColumnCount();
                int rowCount = 0;
                if (resultSet.last()) {//make cursor to point to the last row in the ResultSet object
                    rowCount = resultSet.getRow();
                    resultSet.beforeFirst(); //make cursor to point to the front of the ResultSet object, just before the first row.
                }
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int k = 1; k <= columnCount; k++) {
                        // Note that the index is 1-based
                        String colName = rsmd.getColumnName(k);
                        Object colVal = resultSet.getObject(k);
                        row.put("Tcode", temp);
                        row.put(colName, colVal);
                    }
                    rows.add(row);
                }
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public List<List<String>> getTcodesAndanalyseRisk(String role) {
        try {
            int rowCountflag = 0;
            List<Object> response = new ArrayList<>();
            String query = "SELECT TCODES from GRC_ROLES where NAME_ROLE =" + "'" + role + "'";
            // String query = "SELECT Tcode from Roles_Tcodes where Role_name ='Z:ROLE:RISKANALYSIS'";
            Connection connection = dbConnection.callDbconnect();
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery(query);
            List<String> tcodes = new ArrayList<>();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            // List<Map<String, Object>> rows = new ArrayList<>();
            int columnCount = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String colVal = (String) resultSet.getObject(i);
                    tcodes.add(colVal);
                }
            }
            List<Map<String, Object>> funDetails = getFunctions(tcodes, statement);
            List<Object> Functions = new ArrayList();
            for (int i = 0; i < funDetails.size(); i++) {
                Functions.add(funDetails.get(i).get("FUNCTION_NAME"));
            }
            List uniqueFunctions = findUniqueFunctions(Functions);
            List<String> crossJoinedActions = new ArrayList<>();
            crossJoinedActions = performCrossJoin(tcodes);
            return riskAnalysis(role, crossJoinedActions, statement);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public List findUniqueFunctions(List list) {
        Set<String> uniqueElements = new HashSet<>(list);
        List Funs = new ArrayList();
        // Print the unique elements
        for (String element : uniqueElements) {
            Funs.add(element);

        }
        return Funs;
    }

    public List performCrossJoin(List actions) {
        try {
            Set<String> uniqueActions = new HashSet<>(actions);
            List<String> list1 = new ArrayList<>(uniqueActions);
            List<String> list2 = new ArrayList<>(uniqueActions);
            List<String> crossJoin = new ArrayList<>();
            for (Object str1 : list1) {
                for (Object str2 : list2) {
                    if (!str1.equals(str2)) {
                        String joinedString = str1 + "|" + str2;
                        String reverseCrossJoinResult = str2 + "|" + str1;
                        if (!crossJoin.contains(reverseCrossJoinResult))
                            crossJoin.add(joinedString);
                    }

                }
            }
            return crossJoin;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public List<List<String>> riskAnalysis(String role, List<String> crossJoinedActions, Statement stmnt) throws SQLException {
        try {
            int actionPairLength = crossJoinedActions.size();
            List<Object> riskDetails;
            // List<Map<String, Object>> rows = new ArrayList<>();
            List<List<String>> rows = new ArrayList<>();
            String temp = null;
            for (int i = 0; i <= actionPairLength - 1; i++) {
                Map<String, Object> function = null;
                temp = crossJoinedActions.get(i);
                String QueryToFindRisk = "SELECT RISK_ID,BUSINESSPROCESS,RISK_LEVEL,RISK_TYPE,FUNCTION_NAME1,DESCRIPTION1,TCODE1,TCODE1_description,FUNCTION_NAME2,DESCRIPTION2,TCODE2,TCODE2_description,TcodePair1 FROM RULESET WHERE TcodePair1=" + "'" + temp + "' OR TcodePair2=" + "'" + temp + "'";
                ResultSet resultSet = stmnt.executeQuery(QueryToFindRisk);
                ResultSetMetaData rsmd = resultSet.getMetaData();
                // List<Map<String, Object>> rows = new ArrayList<>();
                //int colCount = rsmd.getColumnCount();
                int columnCount = rsmd.getColumnCount();
                // Map<String, List<Map<String, Object>>> table = new HashMap<>();
                int rowCount = 0;
                if (resultSet.last()) {//make cursor to point to the last row in the ResultSet object
                    rowCount = resultSet.getRow();
                    resultSet.beforeFirst(); //make cursor to point to the front of the ResultSet object, just before the first row.
                }
                while (resultSet.next()) {
                    //  Map<String, Object> row = new HashMap<>();
                    List row = new ArrayList();
                    row.add(role);
                    row.add("Yes");
                    for (int k = 1; k <= columnCount; k++) {
                        // Note that the index is 1-based
                        String colName = rsmd.getColumnName(k);
                        Object colVal = resultSet.getObject(k);
                        //row.put("Tcode", temp);
                        row.add(colVal);
                    }
                    rows.add(row);

                }
            }
            int rowsSize = (rows.size());
            if (rowsSize <= 0) {
                List row = new ArrayList();
                row.add(role);
                row.add("No");
                rows.add(row);
            }

         return rows;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void excelGen(List<List<List<String>>> rows) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("Risk Analysis Report");

        // Define the headers
        List<String> headers = Arrays.asList(
                "RoleID", "Risk_Status", "RiskID", "BUSINESS PROCESS", "RISK_LEVEL",
                "RISK_TYPE", "Function1", "Function1 Description", "Tcode1",
                "Tcode1 Description", "Function2", "Function2 Description",
                "Tcode2", "Tcode2 Description", "Combination"
        );

        // Create the header row
        Row headerRow = spreadsheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers.get(i));
        }

        // Start inserting data from row 1
        int rowNum = 1;
        for (List<List<String>> rowList : rows) {
            for (List<String> rowData : rowList) {
                Row dataRow = spreadsheet.createRow(rowNum++);
                int cellNum = 0;
                for (String value : rowData) {
                    Cell cell = dataRow.createCell(cellNum++);
                    cell.setCellValue(value);
                }
            }
        }

        // Save the workbook to a file in a specific directory
        String directoryPath = "C:\\Users\\Jini A A\\Documents\\ExcelFiles";
        String fileName = "GRC_Risk_Analysis_Report_" + new Date().getTime() + ".xlsx";
        String filePath = Paths.get(directoryPath, fileName).toString();

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
           // System.out.println("Excel file saved successfully to: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void excelGenForUsers(List<List<List<String>>> rows) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("Risk Analysis Report");
        // Create the header row
        Row headerRow = spreadsheet.createRow(0);
        for (int i = 0; i < Common.headers.size(); i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(Common.headers.get(i));
        }

        // Start inserting data from row 1
        int rowNum = 1;
        for (List<List<String>> rowList : rows) {
            for (List<String> rowData : rowList) {
                Row dataRow = spreadsheet.createRow(rowNum++);
                int cellNum = 0;
                for (String value : rowData) {
                    Cell cell = dataRow.createCell(cellNum++);
                    cell.setCellValue(value);
                }
            }
        }

        // Save the workbook to a file in a specific directory
        String directoryPath = "C:\\Users\\Jini A A\\Documents\\ExcelFiles";
        String fileName = "GRC_Risk_Analysis_Report_" + new Date().getTime() + ".xlsx";
        String filePath = Paths.get(directoryPath, fileName).toString();

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
           // System.out.println("Excel file saved successfully to: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResponseEntity<?> buildTable() throws SQLException {
        try {
            List<Object> response = new ArrayList<>();
            String emptyTableQuery = "DELETE FROM RULESET";
            String buildTableQuery = "INSERT INTO RULESET (RISK_ID,BUSINESSPROCESS,RISK_LEVEL,RISK_TYPE,FUNCTION_NAME1, DESCRIPTION1, TCODE1,TCODE1_description, FUNCTION_NAME2, DESCRIPTION2, TCODE2,TCODE2_description,TcodePair1,TcodePair2 )\n" +
                    "SELECT r.RISKID,r.BUSINESS_PROCESS,r.RISK_LEVEL,r.RISK_TYPE,\n" +
                    "       t1.FUNCTIONS, t1.FUNCTION_DESCRIPTION, t1.TCODE,t1.TCODE_DESCRIPTION,\n" +
                    "       t2.FUNCTIONS, t2.FUNCTION_DESCRIPTION, t2.TCODE,t2.TCODE_DESCRIPTION,\n" +
                    "        CONCAT(t1.TCODE, \"|\", t2.TCODE), CONCAT(t2.TCODE, \"|\", t1.TCODE)\t\n" +
                    "FROM FUNCTIONS_DETAILS t1\n" +
                    "CROSS JOIN FUNCTIONS_DETAILS t2\n" +
                    "JOIN RISK_DETAILS r ON t1.FUNCTIONS = r.FUNCTION1 AND t2.FUNCTIONs = r.FUNCTION2;";
            Connection connection = dbConnection.callDbconnect();
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            boolean resultSet = statement.execute(emptyTableQuery);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            int resultSet1 = statement.executeUpdate(buildTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>("Ruleset Generated successfully", HttpStatus.OK);
    }

//    public void buildTable() throws SQLException {
//        try {
//            List<Object> response = new ArrayList<>();
//            String emptyTableQuery = "DELETE FROM crossjointable";
//            String buildTableQuery = "INSERT INTO crossjointable (RISK_ID,FUNCTION_NAME1, DESCRIPTION1, TCODE1,TCODE1_description, FUNCTION_NAME2, DESCRIPTION2, TCODE2,TCODE2_description,TcodePair1,TcodePair2 )\n" +
//                    "SELECT r.RISK_ID,\n" +
//                    "       t1.FUNCTION_NAME, t1.DESCRIPTION, t1.TCODE,t1.tcode_description,\n" +
//                    "       t2.FUNCTION_NAME, t2.DESCRIPTION, t2.TCODE,t2.tcode_description,\n" +
//                    "        CONCAT(t1.TCODE, \"|\", t2.TCODE), CONCAT(t2.TCODE, \"|\", t1.TCODE)\n" +
//                    "FROM FUNCTIONS t1\n" +
//                    "CROSS JOIN FUNCTIONS t2\n" +
//                    "JOIN RISK r ON t1.FUNCTION_NAME = r.FUN1 AND t2.FUNCTION_NAME = r.FUN2;";
//
//            //String query = "SELECT TCODES from GRC_ROLES where NAME_ROLE =" + "'" + role + "'";
//            // String query = "SELECT Tcode from Roles_Tcodes where Role_name ='Z:ROLE:RISKANALYSIS'";
//            Connection connection = dbConnection.callDbconnect();
//            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
//            boolean resultSet = statement.execute(emptyTableQuery);
//            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
//            int resultSet1 = statement.executeUpdate(buildTableQuery);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }


    ////////////////////////////////////////////////////////////////////////////////////////
    //Extra code for Reference
    public void verifyCrossJoin(List crossJoinedActions, Statement stmnt) throws SQLException {
        String query3 = "SELECT * from RISK";
        ResultSet resultSet = stmnt.executeQuery(query3);
        ResultSetMetaData rsmd = resultSet.getMetaData();
        List<Map<String, Object>> Records = new ArrayList<>();
        int columnCount = rsmd.getColumnCount();
        // Map<String, List<Map<String, Object>>> table = new HashMap<>();
        int rowCount = 0;
        if (resultSet.last()) {//make cursor to point to the last row in the ResultSet object
            rowCount = resultSet.getRow();
            resultSet.beforeFirst(); //make cursor to point to the front of the ResultSet object, just before the first row.
        }
        while (resultSet.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int k = 1; k <= columnCount; k++) {
                // Note that the index is 1-based
                String colName = rsmd.getColumnName(k);
                Object colVal = resultSet.getObject(k);
                // row.put("Tcode", temp);
                row.put(colName, colVal);
            }
            Records.add(row);
        }
        int count = Records.size();
        List functionsOfRisk = new ArrayList();
        List setOfRisks = new ArrayList();
        // List Actions = null;
        //Integer flag = 0;
        for (int l = 0; l < count; l++) {
            //setOfRisks.add(Records.get(l).get("RISK_ID"));
            String risk = (String) Records.get(l).get("RISK_ID");
            functionsOfRisk.add(Records.get(l).get("FUN1"));
            functionsOfRisk.add(Records.get(l).get("FUN2"));
            // String query4 = "SELECT TCODE FROM FUNCTIONS WHERE FUNCTION_NAME=" + "'" + functionsOfRisk.get(0) + "'" + " OR" + "'" + "FUNCTION_NAME=" + functionsOfRisk.get(1) + "'";
            String query4 = "CALL GetTCodeByFunctionName('" + functionsOfRisk.get(0) + "','" + functionsOfRisk.get(1) + "')";
            resultSet = stmnt.executeQuery(query4);
            rsmd = resultSet.getMetaData();
            List Actions = new ArrayList();
            columnCount = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int m = 1; m <= columnCount; m++) {
                    // Note that the index is 1-based
                    Object colVal = resultSet.getObject(m);
                    // row.put("Tcode", temp);
                    Actions.add(colVal);
                }
            }
            List riskviseCrossJoinOfActions = performCrossJoin(Actions);
            compareCrossJoins(crossJoinedActions, riskviseCrossJoinOfActions, risk);
        }
        if (flag != 1) {
           // System.out.println("This Role does not contain any risk");
        }

    }

    public void compareCrossJoins(List crossJoinedActions, List riskviseCrossJoinOfActions, String risk) {
        Set<String> set = new HashSet<>(crossJoinedActions);
        List<String> commonElements = new ArrayList<>();
        List<String> commonActionCombinations = new ArrayList<>();
        for (Object element : riskviseCrossJoinOfActions) {
            if (set.contains(element)) {
                commonElements.add((String) element);
            }
        }
        commonActionCombinations = filterCommonElements(commonElements);
        if (commonActionCombinations.size() != 0)
            flag = 1;
        //System.out.println("The below combination of Actions causes Risk\n" + commonActionCombinations + "\n And it belongs to the RiskID " + risk);
    }

    public List<String> filterCommonElements(List commonElements) {
        List<String> commonActionCombinations = new ArrayList<>();
        List<String> inputList = new ArrayList<>(commonElements);
        //List<String> inputList = Arrays.asList(commonElements.toString());
        for (String ele : inputList) {
            String[] parts = ele.split("\\|");
            for (int i = 0; i < parts.length - 1; i++) {
                String element1 = parts[i];
                String element2 = parts[i + 1];

                if (!element1.equals(element2)) {
                    commonActionCombinations.add(ele);
                } else {
                   // System.out.println(element1 + " is equal to " + element2);
                }
            }
        }
        return commonActionCombinations;
    }
}

