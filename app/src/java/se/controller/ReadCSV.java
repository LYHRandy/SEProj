/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.controller;

import java.io.*;
import java.util.*;
import se.dao.*;
import se.entity.*;
import com.csvreader.CsvReader;
/**
 * Does reading of the csv files
 */
public class ReadCSV {
    public Validate validate = new Validate();
    public static int demRowCount = 0;
    public static int locRowCount = 0;
    public static int locLookupRowCount = 0;
    
    /**
     * Reads and validates demographics.csv 
     * 
     * @param demFilePath   filepath
     * @param folderPath    folder to write to
     * @return TreeMap containing Rows of ArrayList of errors for demographics
     */
    public TreeMap<Integer, ArrayList<String>> readDemographics(String demFilePath, String folderPath) {
        //Starting row number after header
        int rowNum = 2;
        
        try{
            CsvReader reader = new CsvReader(demFilePath);
            reader.readHeaders();
            while(reader.readRecord()){
                String macAddress = reader.get(0);
                String name = reader.get(1);
                String password = reader.get(2);
                String email = reader.get(3);
                String gender = reader.get(4);
                //check if row has valid fields
                if(validate.validateDemographic(rowNum, macAddress, name, password, email, gender, folderPath)){
                    demRowCount++;
                }
                rowNum++;
            }
            
            reader.close();
        }
        catch(FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }
        catch(IOException ie){
            ie.printStackTrace();
        }
        
        //retrieve errors
        TreeMap<Integer, ArrayList<String>> demErrors = validate.demErrors;
        
        //add validated files into database
        UserDAO.add(Validate.demFilePath);
        return demErrors;
    }
    
    /**
     * Reads and validates location.csv 
     * 
     * @param locFilePath   filepath
     * @param folderPath    folder to write to
     * @return TreeMap containing Rows of ArrayList of errors for location
     */
    public TreeMap<Integer, ArrayList<String>> readLocation(String locFilePath, String folderPath){
        //Starting row number after header
        int rowNum = 2;
        
        //retrieve valid locations from location_lookup
        HashMap<String, String> locations = LocationLookupDAO.retrieveLocations();
        
        try{
            
            CsvReader reader = new CsvReader(locFilePath);
            reader.readHeaders();
            while(reader.readRecord()){
                String timeStamp = reader.get(0);
                String macAddress = reader.get(1);
                String locationID = reader.get(2);
                //check if row has valid fields
                if(validate.validateLocation(rowNum, timeStamp, macAddress, locationID, locations)){
                    locRowCount++;
                }
                rowNum++;
            }
            
            reader.close();
        }
        catch(FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }
        catch(IOException ie){
            ie.printStackTrace();
        }
        
        //retrieve errors
        TreeMap<Integer, ArrayList<String>> locErrors = validate.locErrors;
        
        //write validated records to CSV
        String filePath = WriteCSV.writeLocation(Validate.verifiedLocations, folderPath);
        
        //add records from CSV into database
        LocationDAO.add(filePath);
        return locErrors;
    }
    
    /**
     * Reads and validates location.csv for update
     * 
     * @param locFilePath   filepath
     * @param folderPath    folder to write to
     * @return TreeMap containing Rows of ArrayList of errors for location for File Update function
     */
    public TreeMap<Integer, ArrayList<String>> readUpdateLocation(String locFilePath, String folderPath){
        //Starting row number after header
        int rowNum = 2;
        
        //retrieve valid locations from location_lookup
        HashMap<String, String> locations = LocationLookupDAO.retrieveLocations();
        
        try{
            
            CsvReader reader = new CsvReader(locFilePath);
            reader.readHeaders();
            while(reader.readRecord()){
                String timeStamp = reader.get(0);
                String macAddress = reader.get(1);
                String locationID = reader.get(2);       
                //check if row has valid fields
                if(validate.validateLocation(rowNum, timeStamp, macAddress, locationID, locations) && !validate.validateAddLocation(rowNum,timeStamp,macAddress)){
                    locRowCount++;
                }
                rowNum++;
            }
            
            reader.close();
        }
        catch(FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }
        catch(IOException ie){
            ie.printStackTrace();
        }
        
        //retrieve errors
        TreeMap<Integer, ArrayList<String>> locErrors = validate.locErrors;
        
        //write validated records to CSV
        String filePath = WriteCSV.writeLocation(Validate.verifiedLocations, folderPath);
        
        //add records from CSV into database
        LocationDAO.addUpdate(filePath);
        return locErrors;
    }
    
    /**
     * Reads and validates location_lookup.csv
     * 
     * @param locLookupFilePath     filepath
     * @param folderPath            folder to write to
     * @return TreeMap containing Rows of ArrayList of errors for locationlookup
     */
    public TreeMap<Integer, ArrayList<String>> readLocationLookup(String locLookupFilePath, String folderPath){
        //Starting row number after header
        int rowNum = 2;
        
        try{
            
            CsvReader reader = new CsvReader(locLookupFilePath);
            reader.readHeaders();
            while(reader.readRecord()){
                String locationID = reader.get(0);
                String semanticPlace = reader.get(1);
                //check if row has valid fields
                if(validate.validateLocationLookup(rowNum, locationID, semanticPlace, folderPath)){
                    locLookupRowCount++;
                }
                rowNum++;
            }
            
            reader.close();
        }
        catch(FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }
        catch(IOException ie){
            ie.printStackTrace();
        }
        
        //retrieve errors
        TreeMap<Integer, ArrayList<String>> locLookupErrors =validate.locLookupErrors;
        
        //add records from CSV into database
        LocationLookupDAO.add(Validate.locLookupFilePath);
        return locLookupErrors;
    }
}
