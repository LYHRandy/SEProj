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
import java.sql.Statement;
import java.util.HashMap;
import se.connection.ConnectionManager;
import se.entity.Location;
import se.entity.LocationLookup;

/**
 * Database Access Object to access database for CRUD operations regarding location Lookup
 */
public class LocationLookupDAO {
    private static ConnectionManager connManager = new ConnectionManager();
    private static HashMap<String, String> locations = new HashMap<>();
    
    /**
     * 
     * @param fileName load file with specified file name
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
                "INTO TABLE LOCATION_LOOKUP \n" +
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
     * Delete all data from location lookup in data table
     */
    public static void deleteAll() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        locations.clear();

        try {
            conn = connManager.getConnection();
            pstmt = conn.prepareStatement("DELETE FROM LOCATION_LOOKUP");
            int result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            connManager.close(conn,pstmt);
        }
    }
    
    /**
     * 
     * @return HashMap of location ID and semantic place
     */
    public static HashMap<String, String> retrieveLocations(){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = connManager.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM LOCATION_LOOKUP");
            rs = pstmt.executeQuery();
            while(rs.next()){
                locations.put(rs.getString(1), rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            connManager.close(conn,pstmt,rs);
        }
        return locations;
    }
}
