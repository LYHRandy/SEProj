/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.dao;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import se.connection.ConnectionManager;
import static se.dao.demographicDAO.totalNo;

/**
 * Database Access Object to access database to retrieve data for heatmap
 */
public class HeatmapDAO {
    
    /**
     * retrieves heatmap
     * @param floor     specified floor
     * @param datetime  specified date
     * @return HashMap of semantic place with number of people there and its crowd density from 1 - 6
     */
    public static HashMap<Integer,String[]> retrievebyQuery(String floor, String datetime){
        
        // Key —> record, Value —> Array[ semantic place, no.of people, density ]
        HashMap<Integer, String[]> heatMap = new HashMap<>();
        String dateTimeEnd = topKDAO.formatDate(datetime);
        String dateTimeStart = topKDAO.getStartDate(dateTimeEnd);
        
         try{
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(FINAL.MACADDRESS) AS NUMPPL, SEMANTICPLACE FROM\n" +
                                                            "	(SELECT LATEST.MACADDRESS,LOCATIONID FROM\n" +
                                                            "		(SELECT MAX(TIMESTAMP) AS LTS, MACADDRESS FROM app.LOCATION \n" +
                                                            "		WHERE TIMESTAMP >= ? AND TIMESTAMP < ?\n" +
                                                            "		GROUP BY MACADDRESS) AS LATEST \n" +
                                                            "	LEFT OUTER JOIN APP.LOCATION L\n" +
                                                            "	ON LTS = TIMESTAMP AND LATEST.MACADDRESS = L.MACADDRESS) AS FINAL\n" +
                                                            "RIGHT OUTER JOIN APP.location_lookup LL ON FINAL.LOCATIONID = LL.LOCATIONID\n" +
                                                            "WHERE FLOOR = ?\n" +
                                                            "GROUP BY SemanticPlace;");
            
            pstmt.setString(1, dateTimeStart);
            pstmt.setString(2, dateTimeEnd);
            pstmt.setString(3, floor);
            ResultSet rs = pstmt.executeQuery();
            int record = 1;
            while(rs.next()){
                String strCount = rs.getString(1);
                //No. of people at a semantic place
                int count = Integer.parseInt(strCount);
                // Semantic place
                String semanticPlace = rs.getString(2);
                
                // Array[]
                String[] semCount = new String[3];
                semCount[0] = semanticPlace;
                
                String density ="";
                if(count == 0){
                    density = "0";
                }else if(count <3){
                    density = "1";
                }else if(count <6){
                    density = "2";
                } else if(count <11){
                    density = "3";
                }else if(count <21){
                    density = "4";
                }else if(count <31){
                    density = "5";
                }else{
                    density = "6";
                }
                //Add into array
                semCount[1] = strCount;
                semCount[2] = density;
                //Add into hashmap
                heatMap.put(record,semCount);
                record++;
            }
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return heatMap;
    }
}


