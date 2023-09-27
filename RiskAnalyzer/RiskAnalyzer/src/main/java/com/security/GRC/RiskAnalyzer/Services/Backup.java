package com.security.GRC.RiskAnalyzer.Services;

import com.security.GRC.RiskAnalyzer.Connection.DbConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

public class Backup {

//    package com.security.GRC.RiskAnalyzer.Services;
//
//import com.security.GRC.RiskAnalyzer.Connection.DbConnection;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import javax.swing.*;
//import java.sql.*;
//import java.util.*;
//
//    @Service
//    public class DataGetService {
//        @Autowired
//        DbConnection dbConnection;
//        public Integer flag = 0;
//
////    public void manageMultipleRolesAsInput(List inputList) throws SQLException {
////        List<String> Roles = null;
////        if (inputList.equals("ALL") || inputList.equals("all")) {
////            List<Object> response = new ArrayList<>();
////            String fetchAllRoles = "SELECT NAME_ROLE FROM CRC_ROLES";
////            Connection connection = dbConnection.callDbconnect();
////            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
////            ResultSet resultSet = statement.executeQuery(fetchAllRoles);
////            ResultSetMetaData rsmd = resultSet.getMetaData();
////            // List<Map<String, Object>> rows = new ArrayList<>();
////            int columnCount = rsmd.getColumnCount();
////            while (resultSet.next()) {
////                for (int i = 1; i <= columnCount; i++) {
////                    String colVal = (String) resultSet.getObject(i);
////                    //Roles.add(colVal);
////                    execute(colVal);
////                }
////            }
////
////        } else {
////            int inputSize = inputList.size();
////            for (int i = 0; i <= inputSize; i++) {
////                String input = (String) inputList.get(i);
////                execute(input);
////            }
////        }
////    }
//
//        public ResponseEntity<?> execute(String Role) throws SQLException {
//            System.out.println(Role);
//            buildTable();
//            getTcodesAndanalyseRisk(Role);
//            String res = "success";
//            return new ResponseEntity<>(res, HttpStatus.OK);
//        }
//
//
//        public List<Map<String, Object>> getFunctions(List tc, Statement stmnt) throws SQLException {
//            int tcodesLen = tc.size();
//            List<Object> functions;
//            List<Map<String, Object>> rows = new ArrayList<>();
//            for (int i = 0; i <= tcodesLen - 1; i++) {
//                Map<String, Object> function = null;
//                String temp = (String) tc.get(i);
//                String Query2 = "SELECT FUNCTION_NAME,DESCRIPTION FROM FUNCTIONS WHERE TCODE=" + "'" + temp + "'";
//                ResultSet resultSet = stmnt.executeQuery(Query2);
//                ResultSetMetaData rsmd = resultSet.getMetaData();
//                // List<Map<String, Object>> rows = new ArrayList<>();
//                //int colCount = rsmd.getColumnCount();
//                int columnCount = rsmd.getColumnCount();
//                // Map<String, List<Map<String, Object>>> table = new HashMap<>();
//                int rowCount = 0;
//                if (resultSet.last()) {//make cursor to point to the last row in the ResultSet object
//                    rowCount = resultSet.getRow();
//                    resultSet.beforeFirst(); //make cursor to point to the front of the ResultSet object, just before the first row.
//                }
//                while (resultSet.next()) {
//                    Map<String, Object> row = new HashMap<>();
//                    for (int k = 1; k <= columnCount; k++) {
//                        // Note that the index is 1-based
//                        String colName = rsmd.getColumnName(k);
//                        Object colVal = resultSet.getObject(k);
//                        row.put("Tcode", temp);
//                        row.put(colName, colVal);
//                    }
//                    rows.add(row);
//                }
//            }
//            return rows;
//        }
//
//        public void getTcodesAndanalyseRisk(String role) {
//            try {
//                int rowCountflag = 0;
//                List<Object> response = new ArrayList<>();
//                String query = "SELECT TCODES from GRC_ROLES where NAME_ROLE =" + "'" + role + "'";
//                // String query = "SELECT Tcode from Roles_Tcodes where Role_name ='Z:ROLE:RISKANALYSIS'";
//                Connection connection = dbConnection.callDbconnect();
//                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
//                ResultSet resultSet = statement.executeQuery(query);
//                List<String> tcodes = new ArrayList<>();
//                ResultSetMetaData rsmd = resultSet.getMetaData();
//                // List<Map<String, Object>> rows = new ArrayList<>();
//                int columnCount = rsmd.getColumnCount();
//                while (resultSet.next()) {
//                    for (int i = 1; i <= columnCount; i++) {
//                        String colVal = (String) resultSet.getObject(i);
//                        tcodes.add(colVal);
//                    }
//                }
//                System.out.println(tcodes);
//                List<Map<String, Object>> funDetails = getFunctions(tcodes, statement);
//                System.out.println("Fundetails: " + funDetails);
//                List Functions = new ArrayList();
//                for (int i = 0; i < funDetails.size(); i++) {
//                    Functions.add(funDetails.get(i).get("FUNCTION_NAME"));
//                }
//                System.out.println(Functions);
//                //Set<String> uniqueFunctions = (Set<String>) findUniqueFunctions(Functions);
//                List uniqueFunctions = findUniqueFunctions(Functions);
//                System.out.println(uniqueFunctions);
//                List<String> crossJoinedActions = new ArrayList<>();
//                crossJoinedActions = performCrossJoin(tcodes);
//                System.out.println("cross join tcodes in the role::" + crossJoinedActions);
//                riskAnalysis(crossJoinedActions, statement);
//                //verifyCrossJoin(crossJoinedActions, statement);
//            } catch (Exception e) {
//            }
//
//        }
//
//        public List findUniqueFunctions(List list) {
//            Set<String> uniqueElements = new HashSet<>(list);
//            List Funs = new ArrayList();
//            // Print the unique elements
//            //System.out.println("Unique elements:");
//            for (String element : uniqueElements) {
//                Funs.add(element);
//
//            }
//            return Funs;
//        }
//
//        public List performCrossJoin(List actions) {
//            List list1 = actions;
//            List list2 = actions;
//            List<String> crossJoin = new ArrayList<>();
//            for (Object str1 : list1) {
//                for (Object str2 : list2) {
//                    if (!str1.equals(str2)) {
//                        String joinedString = str1 + "|" + str2;
//                        String reverseCrossJoinResult = str2 + "|" + str1;
//                        if (!crossJoin.contains(reverseCrossJoinResult))
//                            crossJoin.add(joinedString);
//                    }
//
//                }
//            }
//            return crossJoin;
//
//        }
//
//        public void riskAnalysis(List<String> crossJoinedActions, Statement stmnt) throws SQLException {
//            int actionPairLength = crossJoinedActions.size();
//            List<Object> riskDetails;
//            List<Map<String, Object>> rows = new ArrayList<>();
//            String temp = null;
//            for (int i = 0; i <= actionPairLength - 1; i++) {
//                Map<String, Object> function = null;
//                temp = crossJoinedActions.get(i);
//                //String Query2 = "SELECT FUNCTION_NAME,DESCRIPTION FROM FUNCTIONS WHERE TCODE=" + "'" + temp + "'";
//                String QueryToFindRisk = "SELECT * FROM crossjointable WHERE TcodePair1=" + "'" + temp + "' OR TcodePair2=" + "'" + temp + "'";
//                ResultSet resultSet = stmnt.executeQuery(QueryToFindRisk);
//                ResultSetMetaData rsmd = resultSet.getMetaData();
//                // List<Map<String, Object>> rows = new ArrayList<>();
//                //int colCount = rsmd.getColumnCount();
//                int columnCount = rsmd.getColumnCount();
//                // Map<String, List<Map<String, Object>>> table = new HashMap<>();
//                int rowCount = 0;
//                if (resultSet.last()) {//make cursor to point to the last row in the ResultSet object
//                    rowCount = resultSet.getRow();
//                    resultSet.beforeFirst(); //make cursor to point to the front of the ResultSet object, just before the first row.
//                }
//                while (resultSet.next()) {
//                    Map<String, Object> row = new HashMap<>();
//                    for (int k = 1; k <= columnCount; k++) {
//                        // Note that the index is 1-based
//                        String colName = rsmd.getColumnName(k);
//                        Object colVal = resultSet.getObject(k);
//                        //row.put("Tcode", temp);
//                        row.put(colName, colVal);
//                    }
//                    rows.add(row);
//
//                }
//            }
//            int rowsSize = (rows.size());
//            //System.out.println("rowsize" + rowsSize);
//            if (rowsSize > 0) {
//                System.out.println("The Role contains Risk");
//                for (int j = 0; j <= rowsSize; j++) {
//                    System.out.println(" Risk ID : " + rows.get(j).get("RISK_ID") + " Action pair : " + rows.get(j).get("TcodePair1") + " Member functions of the risk : " + rows.get(j).get("FUNCTION_NAME1") + " and " + rows.get(j).get("FUNCTION_NAME2"));
//
//                }
//            } else {
//                System.out.println("The Role has no Risk");
//            }
//        }
//
//        public void buildTable() throws SQLException {
//            List<Object> response = new ArrayList<>();
//            String emptyTableQuery = "DELETE FROM crossjointable";
//            String buildTableQuery = "INSERT INTO crossjointable (RISK_ID,FUNCTION_NAME1, DESCRIPTION1, TCODE1, FUNCTION_NAME2, DESCRIPTION2, TCODE2,TcodePair1,TcodePair2 )\n" +
//                    "SELECT r.RISK_ID,\n" +
//                    "       t1.FUNCTION_NAME, t1.DESCRIPTION, t1.TCODE,\n" +
//                    "       t2.FUNCTION_NAME, t2.DESCRIPTION, t2.TCODE,\n" +
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
//
//        }
//
//        ////////////////////////////////////////////////////////////////////////////////////////
//        //Extra code for Reference
//        public void verifyCrossJoin(List crossJoinedActions, Statement stmnt) throws SQLException {
//            String query3 = "SELECT * from RISK";
//            ResultSet resultSet = stmnt.executeQuery(query3);
//            ResultSetMetaData rsmd = resultSet.getMetaData();
//            List<Map<String, Object>> Records = new ArrayList<>();
//            int columnCount = rsmd.getColumnCount();
//            // Map<String, List<Map<String, Object>>> table = new HashMap<>();
//            int rowCount = 0;
//            if (resultSet.last()) {//make cursor to point to the last row in the ResultSet object
//                rowCount = resultSet.getRow();
//                resultSet.beforeFirst(); //make cursor to point to the front of the ResultSet object, just before the first row.
//            }
//            while (resultSet.next()) {
//                Map<String, Object> row = new HashMap<>();
//                for (int k = 1; k <= columnCount; k++) {
//                    // Note that the index is 1-based
//                    String colName = rsmd.getColumnName(k);
//                    Object colVal = resultSet.getObject(k);
//                    // row.put("Tcode", temp);
//                    row.put(colName, colVal);
//                }
//                Records.add(row);
//            }
//            int count = Records.size();
//            List functionsOfRisk = new ArrayList();
//            List setOfRisks = new ArrayList();
//            // List Actions = null;
//            //Integer flag = 0;
//            for (int l = 0; l < count; l++) {
//                //setOfRisks.add(Records.get(l).get("RISK_ID"));
//                String risk = (String) Records.get(l).get("RISK_ID");
//                functionsOfRisk.add(Records.get(l).get("FUN1"));
//                functionsOfRisk.add(Records.get(l).get("FUN2"));
//                System.out.println("functionsOfRisk" + functionsOfRisk);
//                // String query4 = "SELECT TCODE FROM FUNCTIONS WHERE FUNCTION_NAME=" + "'" + functionsOfRisk.get(0) + "'" + " OR" + "'" + "FUNCTION_NAME=" + functionsOfRisk.get(1) + "'";
//                String query4 = "CALL GetTCodeByFunctionName('" + functionsOfRisk.get(0) + "','" + functionsOfRisk.get(1) + "')";
//                resultSet = stmnt.executeQuery(query4);
//                rsmd = resultSet.getMetaData();
//                List Actions = new ArrayList();
//                columnCount = rsmd.getColumnCount();
//                while (resultSet.next()) {
//                    for (int m = 1; m <= columnCount; m++) {
//                        // Note that the index is 1-based
//                        Object colVal = resultSet.getObject(m);
//                        // row.put("Tcode", temp);
//                        Actions.add(colVal);
//                    }
//                }
//                System.out.println("Actions of functions" + Actions);
//                List riskviseCrossJoinOfActions = performCrossJoin(Actions);
//                System.out.println("riskviseCrossJoinOfActions" + riskviseCrossJoinOfActions);
//                compareCrossJoins(crossJoinedActions, riskviseCrossJoinOfActions, risk);
//            }
//            if (flag != 1) {
//                System.out.println("This Role does not contain any risk");
//            }
//
//        }
//
//        public void compareCrossJoins(List crossJoinedActions, List riskviseCrossJoinOfActions, String risk) {
//            Set<String> set = new HashSet<>(crossJoinedActions);
//            System.out.println("set:" + set);
//            System.out.println("crossJoinedActions" + crossJoinedActions);
//            List<String> commonElements = new ArrayList<>();
//            List<String> commonActionCombinations = new ArrayList<>();
//            for (Object element : riskviseCrossJoinOfActions) {
//                if (set.contains(element)) {
//                    commonElements.add((String) element);
//                }
//            }
//            commonActionCombinations = filterCommonElements(commonElements);
//            System.out.println("commonActionCombinations::" + commonActionCombinations);
//            if (commonActionCombinations.size() != 0)
//                flag = 1;
//            System.out.println("The below combination of Actions causes Risk\n" + commonActionCombinations + "\n And it belongs to the RiskID " + risk);
//        }
//
//        public List<String> filterCommonElements(List commonElements) {
//            List<String> commonActionCombinations = new ArrayList<>();
//            List<String> inputList = new ArrayList<>(commonElements);
//            //List<String> inputList = Arrays.asList(commonElements.toString());
//            for (String ele : inputList) {
//                String[] parts = ele.split("\\|");
//                for (int i = 0; i < parts.length - 1; i++) {
//                    String element1 = parts[i];
//                    String element2 = parts[i + 1];
//
//                    if (!element1.equals(element2)) {
//                        commonActionCombinations.add(ele);
//                        System.out.println(element1 + " is not equal to " + element2);
//                    } else {
//                        System.out.println(element1 + " is equal to " + element2);
//                    }
//                }
//            }
//            return commonActionCombinations;
//        }
//    }


}
