/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.dao;

import java.util.ArrayList;
import se.connection.ConnectionManager;
import se.entity.Location;
import java.sql.*;
import java.util.HashMap;

/**
 * Database Access Object to access database for CRUD operations regarding location
 */
public class LocationDAO {
    private static ConnectionManager connManager = new ConnectionManager();
    
    /**
     * inputs CSV data into database
     * @param fileName      load file with specified file name
     */
    public static void add(String fileName){
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        fileName = fileName.replace('\\','/');
        //fileName = "D:/Program Files (x86)/is203-2017_G4T4/app/build/web/data/" + fileName;
        try{
            conn = connManager.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("LOAD DATA LOCAL INFILE '" + fileName + "' \n" +
                "INTO TABLE LOCATION \n" +
                "FIELDS TERMINATED BY ',' \n" +
                "ENCLOSED BY '\"'\n" +
                "LINES TERMINATED BY '\\n'\n");
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
        finally{
            ConnectionManager.close(conn,stmt,rs);
        }
    }
    
    
    /**
     * 
     * @param fileName add the updated data from the specified file name
     */
    public static void addUpdate(String fileName){
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        fileName = fileName.replace('\\','/');
        //fileName = "D:/Program Files (x86)/is203-2017_G4T4/app/build/web/data/" + fileName;
        try{
            conn = connManager.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("LOAD DATA LOCAL INFILE '" + fileName + "' IGNORE \n" +
                "INTO TABLE LOCATION \n" +
                "FIELDS TERMINATED BY ',' \n" +
                "ENCLOSED BY '\"'\n" +
                "LINES TERMINATED BY '\\n'\n");
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
        finally{
            ConnectionManager.close(conn,stmt,rs);
        }
    }
    
    /**
     * Deletes all data in location table
     */
    public static void deleteAll() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = connManager.getConnection();
            pstmt = conn.prepareStatement("TRUNCATE TABLE LOCATION");
            int result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            connManager.close(conn,pstmt);
        }
    }
    
    /**
     * Check if record exists within database
     * @param time date time as read in csv records uploaded by user
     * @param macAdd macAddress as read in csv records uploaded by user
     * @return true if location row can be found in location table in database
     */
    public static boolean checkUpdate(String time, String macAdd){
        Boolean update = false;
        Connection conn = null;
        PreparedStatement ptmt = null;
        ResultSet rs = null;
        System.out.println(time + "  "  + macAdd);
        try{
            conn = connManager.getConnection();
            ptmt = conn.prepareStatement("SELECT count(*) FROM LOCATION where timestamp = ? AND macaddress = ?");
            ptmt.setString(1, time);
            ptmt.setString(2, macAdd);
            rs = ptmt.executeQuery();
            while(rs.next()){
                update = true;
            }
        } 
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            ConnectionManager.close(conn,ptmt,rs);
        }
        return update;
    }
}
