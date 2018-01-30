/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import se.connection.ConnectionManager;

/**
 * Database Access Object to access database for retrieval for breakdown by year and gender
 */
public class demographicDAO {
    public static int totalNo = 0;
    /**
     * retrieves demographic breakdown by query
     * 
     * @param datetime Date, Time specified by user
     * @param q1 query specified by user (either year, school, gender)
     * @param q2 query specified by user (either year, school, gender) - May be empty
     * @param q3 query specified by user (either year, school, gender) - May be empty
     * @return LinkedHashMap of students in a specified datetime based on query specified by user
     */
    public static LinkedHashMap<String,LinkedHashMap<String,LinkedHashMap<String,Integer>>> queryBy(String datetime, String q1,String q2, String q3){
        
        //linked hashmap to maintain display sequence
        LinkedHashMap<String,LinkedHashMap<String,LinkedHashMap<String,Integer>>> demoMap = new LinkedHashMap<>();
        String dateTimeEnd = topKDAO.formatDate(datetime);
        String dateTimeStart = topKDAO.getStartDate(dateTimeEnd);
        try{
            Connection conn = ConnectionManager.getConnection();
            //Query by first order
            PreparedStatement pstmt = conn.prepareStatement("SELECT TEMP.FIELDS, COUNT(MACADDRESS) FROM \n" +
                                                            "  (SELECT DISTINCT D.MACADDRESS, "+q1+" FROM LOCATION L\n" +
                                                            "    INNER JOIN DEMOGRAPHICS D \n" +
                                                            "    ON L.MACADDRESS = D.MACADDRESS\n" +
                                                            "   WHERE TIMESTAMP >= ? AND TIMESTAMP < ?) AS USERS\n" +
                                                            "    RIGHT OUTER JOIN \n" +
                                                            "    (SELECT FIELDS FROM DEMO_FIELDS WHERE TYPE = \""+q1+"\") AS TEMP\n" +
                                                            "    ON USERS."+q1+" = TEMP.FIELDS\n" +
                                                            "GROUP BY TEMP.FIELDS\n" +
                                                            "ORDER BY TEMP.FIELDS"+((q1.equals("Gender"))?" desc":""));

            pstmt.setString(1, dateTimeStart);
            pstmt.setString(2, dateTimeEnd);
            ResultSet rs = pstmt.executeQuery();
            totalNo = 0;
            while(rs.next()){
                String field1 = rs.getString(1);
                String count1 = rs.getString(2);
                totalNo = Integer.parseInt(count1) + totalNo;
                demoMap.put(field1+","+count1,null);
                if(!q2.isEmpty()){
                    //if there is second order,query again 
                    pstmt = conn.prepareStatement("SELECT TEMP.FIELDS, COUNT(MACADDRESS) FROM \n" +
                                                  "   (SELECT DISTINCT D.MACADDRESS, "+q1+","+q2+" FROM LOCATION L\n" +
                                                  "    INNER JOIN DEMOGRAPHICS D \n" +
                                                  "    ON L.MACADDRESS = D.MACADDRESS\n" +
                                                  "    WHERE TIMESTAMP >= ? AND TIMESTAMP < ? \n" +
                                                  "    AND "+q1+" = \""+field1+"\") AS USERS\n" +
                                                  "    RIGHT OUTER JOIN \n" +
                                                  "    (SELECT FIELDS FROM DEMO_FIELDS WHERE TYPE =\""+q2+"\") AS TEMP\n" +
                                                  "    ON USERS."+q2+" = TEMP.FIELDS\n" +
                                                  "GROUP BY TEMP.FIELDS\n" +
                                                  "ORDER BY TEMP.FIELDS"+((q2.equals("Gender"))?" desc":""));
                    pstmt.setString(1, dateTimeStart);
                    pstmt.setString(2, dateTimeEnd);
                    ResultSet rs2 = pstmt.executeQuery();
                    LinkedHashMap<String,LinkedHashMap<String,Integer>> secondQuery = new LinkedHashMap<>();
                    while(rs2.next()){
                        String field2 = rs2.getString(1);
                        String count2 = rs2.getString(2);
                        secondQuery.put(field2+","+count2, null);
                        
                        if(!q3.isEmpty()){
                            //if there is third order, query again
                            pstmt = conn.prepareStatement("SELECT TEMP.FIELDS, COUNT(MACADDRESS) FROM \n" +
                                                          "  (SELECT DISTINCT D.MACADDRESS, "+q1+","+q2+","+q3+" FROM LOCATION L\n" +
                                                          "    INNER JOIN DEMOGRAPHICS D \n" +
                                                          "    ON L.MACADDRESS = D.MACADDRESS\n" +
                                                          "    WHERE TIMESTAMP >= ? AND TIMESTAMP < ? \n" +
                                                          "    AND "+q1+" = \""+field1+
                                                          "\"    AND "+q2+" = \""+field2+"\") AS USERS\n" +
                                                          "    RIGHT OUTER JOIN \n" +
                                                          "    (SELECT FIELDS FROM DEMO_FIELDS WHERE TYPE = \""+q3+"\") AS TEMP\n" +
                                                          "    ON USERS."+q3+" = TEMP.FIELDS\n" +
                                                          "GROUP BY TEMP.FIELDS\n" +
                                                          "ORDER BY TEMP.FIELDS"+((q3.equals("Gender"))?" desc":""));
                            pstmt.setString(1, dateTimeStart);
                            pstmt.setString(2, dateTimeEnd);
                            ResultSet rs3 = pstmt.executeQuery();
                            LinkedHashMap<String,Integer> thirdQuery = new LinkedHashMap<>();
                            while(rs3.next()){
                                String field3 = rs3.getString(1);
                                int count3 = Integer.parseInt(rs3.getString(2));
                                thirdQuery.put(field3, count3);
                            }
                            secondQuery.put(field2+","+count2,thirdQuery);
                        }
                    }
                    demoMap.put(field1+","+count1,secondQuery);
                }
                
            }
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return demoMap;
    }
}
