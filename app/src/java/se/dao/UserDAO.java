/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.dao;

import java.util.ArrayList;
import se.connection.ConnectionManager;
import se.entity.User;
import java.sql.*;
import java.util.HashMap;
import static se.dao.demographicDAO.totalNo;

/**
 * Database Access Object to access database for CRUD operations regarding users
 */
public class UserDAO {
    
    private static String adminUsername = "admin";
    private static String adminPassword = "seg4t4";
    private static ArrayList<User> users = new ArrayList<>();
    private static ConnectionManager connManager = new ConnectionManager();
   
    /**
     * 
     * @param username      username to be checked 
     * @param password      password to be checked
     * @return              returns if the user is an admin or not
     */
    public static int checkUser(String username, String password){
        /*return values:
            not user: 0
            User: 1
            Admin: 2
        */
        if(username.equals(adminUsername) && password.equals(adminPassword)){
            return 2;
        }
        if(validUserInput(username, password)){
            return 1;
        }
        return 0;
        
    }
    
    /**
     * 
     * @param user          username to be checked
     * @param password      password to be checked
     * @return              <code>true</code> if user input is valid
     *                      <code>false</code> if user is invalid
     */
    public static boolean validUserInput(String user, String password){
        
        HashMap<String, String> userPassMap = retrieveUserPassword();
        if(userPassMap.containsKey(user)){
            String retrievedPass = userPassMap.get(user);
            if(retrievedPass.equals(password)){
                return true;
            }
        }
        return false;
    }
    /**
     * 
     * @return      returns HashMap of username and password
     */
    public static HashMap<String, String> retrieveUserPassword(){
        
        HashMap<String, String> userPassMap = new HashMap<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("SELECT email, password FROM demographics;");
            rs = pstmt.executeQuery();
            while(rs.next()){
                String email = rs.getString(1);
                int indexAt = email.indexOf("@");
                String username = email.substring(0,indexAt);
                String password = rs.getString(2);
                userPassMap.put(username,password);
            }
            
        }catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            ConnectionManager.close(conn,pstmt,rs);
        }
        return userPassMap;
    }
    
    /**
     * 
     * @param fileName      adds the data from the specified file name into the demographics table
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
                "INTO TABLE DEMOGRAPHICS \n" +
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
     * Deletes all data in the demographics table
     */
    public static void deleteAll() {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = connManager.getConnection();
            pstmt = conn.prepareStatement("TRUNCATE TABLE DEMOGRAPHICS");
            int result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            connManager.close(conn,pstmt);
        }
    }
}
