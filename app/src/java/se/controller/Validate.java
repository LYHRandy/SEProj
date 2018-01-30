/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.controller;

import se.dao.UserDAO;
import se.dao.LocationLookupDAO;
import se.dao.LocationDAO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import se.entity.*;
import se.dao.*;
import java.util.*;

/**
 * Contains methods for validation of fields within the different csv files
 */
public class Validate {
    public static TreeMap<Integer,ArrayList<String>> demErrors = new TreeMap<>();
    public static TreeMap<Integer,ArrayList<String>> locErrors = new TreeMap<>();
    public static TreeMap<Integer,ArrayList<String>> locLookupErrors = new TreeMap<>();
    public static HashMap<String,String[]> verifiedLocations = new HashMap<>();
    public static HashMap<String,Integer> verifiedLocationsID = new HashMap<>();
    public static String isBlank = " is blank";
    public static String demFilePath = "";
    public static String locLookupFilePath = "";
    public static String statYear = "";
    public static String statSchool = "";
    public static String statUsername = "";
    public static String statFloor = "";
    
    
    /**
     * Validate demographics
     * 
     * @param row           row to be checked
     * @param macAddress    Mac address to be checked
     * @param name          name to be checked
     * @param password      password to be checked
     * @param email         email to be checked
     * @param gender        gender to be checked
     * @param folderPath    folder path to be checked
     * @return              <code>true</code> if Mac address, name, password, email, gender, and folder path are valid
     *                      <code>false</code> if Mac address, name, password, email, gender, and folder path are invalid
     */
    public boolean validateDemographic(int row, String macAddress,String name,String password,String email,String gender, String folderPath){
        //initialise arraylist to store row errors
        ArrayList<String> rowErrors = new ArrayList<>();
        boolean valid = true;
        
        //check if fields are blank
        macAddress = macAddress.trim();
        if(macAddress.isEmpty()){
            rowErrors.add("mac address"+isBlank);
            valid = false;
        }
        
        name = name.trim();
        if(name.isEmpty()){
            rowErrors.add("name"+isBlank);
            valid = false;
        }
        
        password = password.trim();
        if(password.isEmpty()){
            rowErrors.add("password"+isBlank);
            valid = false;
        }
        
        email = email.trim();
        if(email.isEmpty()){
            rowErrors.add("email"+isBlank);
            valid = false;
        }
        
        gender = gender.trim().toUpperCase();
        if(gender.isEmpty()){
            rowErrors.add("gender"+isBlank);
            valid = false;
        }
        
        //if any fields are blank return errors
        if(!valid){
            demErrors.put(row, rowErrors);
            return valid;
        }
        
        //validate fields
        if(macAddress.length()!=40 || !macAddress.matches("[0-9a-fA-F]+")){
            rowErrors.add("invalid mac address");
            valid = false;
        }
        
        if(password.length()<8 || password.contains(" ")){
            rowErrors.add("invalid password");
            valid = false;
        }
                
        String emailError = "invalid email";
        if(!validateEmail(email)){
            rowErrors.add("invalid email");
            valid =false;
        }

        if(gender.length()!=1 || !(gender.equals("M") || gender.equals("F"))){
            rowErrors.add("invalid gender");
            valid = false;
        }
        
        //if all fields are valid 
        if(valid){ 
            //write validated row to CSV file
            User user = new User(macAddress, name, password, email, gender, statYear, statSchool, statUsername);
            demFilePath = WriteCSV.writeDemographics(user, folderPath);
        } else {
            demErrors.put(row, rowErrors);
        }
        return valid;
    }
    
    /**
     * Check if row already exists
     * 
     * @param row           row number
     * @param timeStamp     DateTime of User at Location
     * @param macAdd        MacAddress of User
     * @return              if row exist in database
     */
    public boolean validateAddLocation(int row,String timeStamp,String macAdd){
        boolean checkLocation = false;
        if(!LocationDAO.checkUpdate(timeStamp, macAdd)){
            checkLocation = true;
            ArrayList<String> getRowErrors = new ArrayList<>();
            getRowErrors.add("duplicate row");
            locErrors.put(row, getRowErrors);
        }
        return checkLocation;
    }
    
    
    /**
     * Validate location
     * 
     * @param row           row to be checked
     * @param timeStamp     time stamp to be checked
     * @param macAddress    Mac address to be checked
     * @param locationID    location ID to be checked
     * @param locations     locations to be checked
     * @return              <code>true</code> if time stamp, Mac address, location ID and locations are valid
     *                      <code>false</code> if time stamp, Mac address, location ID and locations are invalid 
     */
    public boolean validateLocation(int row, String timeStamp, String macAddress,String locationID, HashMap<String, String> locations){
        
        //initialise arraylist to store errors
        ArrayList<String> rowErrors = new ArrayList<>();
        boolean valid = true;
        
        //check if fields are blank
        timeStamp = timeStamp.trim();
        if(timeStamp.isEmpty()){
            rowErrors.add("timestamp"+isBlank);
            valid = false;
        }
        
        macAddress = macAddress.trim();
        String macRegex = "[0-9a-fA-F]+";
        if(macAddress.isEmpty()){
            rowErrors.add("mac address"+isBlank);
            valid = false;
        }
        
        locationID = locationID.trim();
        if(locationID.isEmpty()){
            rowErrors.add("location id"+isBlank);
            valid = false;
        }
        
        //if there are blank fields return errors
        if(!valid){
            locErrors.put(row, rowErrors);
            return valid;
        }
        
        //check if fields are valid
        if(!(locations.containsKey(locationID))){
            rowErrors.add("invalid location");
            valid = false;
        }
        
        if(macAddress.length()!=40 || !macAddress.matches(macRegex)){
            rowErrors.add("invalid mac address");
            valid = false;
        }
        
        if(!timeStamp.isEmpty()){
            if(timeStamp.length() != 19){
                rowErrors.add("invalid timestamp");
                valid = false;
            } else{
                SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                format.setLenient(false);
                try{
                    format.parse(timeStamp);
                }catch(ParseException pe){
                    rowErrors.add("invalid timestamp");
                    valid = false;
                    pe.printStackTrace();
                }
            }
        }
        
        if(valid){
            //Location location = new Location(timeStamp, macAddress, locationID);
            //LocationDAO.add(location);
            String[] locationDetails = {timeStamp,macAddress,locationID};
            String priKey = timeStamp+macAddress;
            if(verifiedLocations.containsKey(priKey)){
                int rowNum = verifiedLocationsID.get(priKey);
                ArrayList<String> getRowErrors = new ArrayList<>();
                getRowErrors.add("duplicate row");
                verifiedLocations.put(priKey, locationDetails);
                verifiedLocationsID.put(priKey, row);
                locErrors.put(rowNum, getRowErrors);
                valid = false;
            } else {
                verifiedLocations.put(priKey, locationDetails);
                verifiedLocationsID.put(priKey, row);
            }
        } else {
            locErrors.put(row, rowErrors);
        }
 
        return valid;
    }
    
    /**
     * Validate location lookup
     * 
     * @param row               row to be checked
     * @param locationID        location ID to be checked
     * @param semanticPlace     semantic place to be checked
     * @param folderPath        folder path to be checked
     * @return                  <code>true</code> if row, location ID, semantic place, and folder path are valid
     *                          <code>false</code> if row, location ID, semantic place, and folder path are invalid
     */
    public boolean validateLocationLookup(int row, String locationID, String semanticPlace, String folderPath){
        
        //initialise arraylist to store errors
        ArrayList<String> rowErrors = new ArrayList<>();
        boolean valid = true;
        
        //check if there are blank fields
        locationID = locationID.trim();
        int locationIDvalue = 0;
        if(locationID.isEmpty()){
            rowErrors.add("location id"+isBlank);
            valid = false;
        }
        
        semanticPlace = semanticPlace.trim(); 
        if(semanticPlace.isEmpty()){
            rowErrors.add("semantic place"+isBlank);
            valid = false;
        }
        
        //if there are blank fields return errors
        if(!valid){
            locLookupErrors.put(row, rowErrors);
            return valid;
        }
        
        //check if fields are valid
        try{
            if(valid){
                locationIDvalue = Integer.parseInt(locationID);
            }
        }
        catch(NumberFormatException e){
            rowErrors.add("invalid location id");
            valid = false;
        }
        
        if(locationIDvalue <= 0 && valid){
            rowErrors.add("invalid location id");
            valid = false;
        }
        
        if(!validateSemanticPlace(semanticPlace)){
            rowErrors.add("invalid semantic place");
            valid = false;
        }
        
        
        if(valid){
            LocationLookup locLookup = new LocationLookup(locationID, semanticPlace, statFloor);
            locLookupFilePath = WriteCSV.writeLocationLookup(locLookup, folderPath);
            //LocationLookupDAO.add(locLookup);
        } else {
            locLookupErrors.put(row, rowErrors);
        }
        
        return valid;
    }
    
    /**
     * Method to validate emails
     * 
     * @param email     email to be checked
     * @return          <code>true</code> if email is valid
     *                  <code>false</code> if email is invalid
     */
    public boolean validateEmail(String email){
        //check @ exist
        int indexAt = email.indexOf("@");
        if(indexAt == -1){
            return false;
        }
        
        //front half of email
        String firstHalf = email.substring(0,indexAt);
        //index of separator
        int indexOfDot = firstHalf.lastIndexOf('.');
        //if . exists
        if(indexOfDot==-1){
            return false;
        }
        //extract year and check
        String year = firstHalf.substring(indexOfDot+1);
        String yearMatch = "2013|2014|2015|2016|2017";
        if(!year.matches(yearMatch)){
            return false;
        }
        //check front part has valid characters: 0-9, a-z, A-Z, "."
        String id = firstHalf.substring(0,indexOfDot);
        char[] idArray = id.toCharArray();
        for (char c: idArray){
            boolean valid = ((c >= 'a') && (c <= 'z')) || 
                            ((c >= 'A') && (c <= 'Z')) || 
                            ((c >= '0') && (c <= '9')) ||
                            (c=='.');

            if (!valid){
                return false;
            }
        }
        
        //second half of email
        String secondHalf = email.substring(indexAt+1);
        //check .smu.edu.sg domain exists
        String checkMail = ".smu.edu.sg";
        int domainIndex = secondHalf.indexOf(checkMail);
        if(domainIndex == -1){
            return false;
        }
        //split into school and domain
        String school = secondHalf.substring(0,domainIndex);
        String domain = secondHalf.substring(domainIndex);
        //check school
        String checkSchool = "sis|business|accountancy|socsc|law|economics";
        if(!school.matches(checkSchool)){
            return false;
        }
        //check domain
        if(!domain.equals(".smu.edu.sg")){
            return false;
        }
        
        statYear = year;
        statUsername = firstHalf;
        statSchool = school;
        return true;  
    }
    
    /**
     * validate semantic place
     * 
     * @param semanticPlace     semantic place to be checked
     * @return                  <code>true</code> if semantic place is valid
     *                          <code>false</code> if semantic place is invalid
     */
    public boolean validateSemanticPlace(String semanticPlace){
        
        //standard starting string
        String building = "SMUSIS";
        
        //check building
        String spBuilding = semanticPlace.substring(0,6);
        if(!spBuilding.equals(building)){
            return false;
        }
        
        //check L/B
        String spLB = semanticPlace.substring(6,7);
        if(!(spLB.equals("L") || spLB.equals("B"))){
            return false;
        }
        
        //check sementic place is present
        if(!(semanticPlace.length() > 8)){
            return false;
        }
        
        
        //check floor and specific location
        try{
            String spFloor = semanticPlace.substring(7,8);
            int spFloorInt = Integer.parseInt(spFloor);
            statFloor = spLB + spFloor;
        } catch (NumberFormatException nfe){
            return false;
        } catch (IndexOutOfBoundsException iobe){
            return false;
        }
        
        return true;
    }
}