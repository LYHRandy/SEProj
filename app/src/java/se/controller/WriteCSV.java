/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.controller;

import com.csvreader.*;
import java.io.*;
import se.entity.*;
import java.util.*;

/**
 * Performs writing of validated rows into csv
 */
public class WriteCSV {
    /**
     * Writes validated user data to CSV
     * 
     * @param user          field details
     * @param folderPath    folder to write CSV
     * @return              filePath of the new csv file to which the new data is written to
     */
    public static String writeDemographics(User user, String folderPath){
        //defining output file path
        String outputFile = folderPath + File.separator + "validatedDemographics.csv";
        
        try{
            CsvWriter writer = new CsvWriter(new FileWriter(outputFile,true),',');
            writer.write(user.getMacAddress());
            writer.write(user.getName());
            writer.write(user.getPassword());
            writer.write(user.getEmail());
            writer.write(user.getGender());
            writer.write(user.getYear());
            writer.write(user.getSchool());
            writer.write(user.getUsername());
            writer.endRecord();
            
            writer.close();
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return outputFile;
    }
    /**
     * Writes validation location_lookup to CSV
     * 
     * @param locationlookup    location lookup to be checked
     * @param folderPath        folder path to be checked
     * @return                  returns location lookup that have been written in to folder path
     */
    public static String writeLocationLookup(LocationLookup locationlookup, String folderPath){
        //defining output file path
        String outputFile = folderPath + File.separator + "validatedLocationLookup.csv";
        
        try{
            CsvWriter writer = new CsvWriter(new FileWriter(outputFile,true),',');
            writer.write(locationlookup.getLocationId());
            writer.write(locationlookup.getSemanticPlace());
            writer.write(locationlookup.getFloor());
            writer.endRecord();
            
            writer.close();
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return outputFile;
    }
    
    /**
     * Write validated location to CSV
     * 
     * @param verifiedLocations     verified locations to be checked
     * @param folderPath            folder path to be checked
     * @return                      returns verified locations that have been written in to folder path
     */
    public static String writeLocation(HashMap<String,String[]> verifiedLocations,String folderPath){
        //defining output file path
        String outputFile = folderPath + File.separator + "validatedLocation.csv";
        try{
            CsvWriter writer = new CsvWriter(new FileWriter(outputFile,true),',');
            Iterator i = verifiedLocations.entrySet().iterator();
            while(i.hasNext()){
                Map.Entry entry = (Map.Entry)i.next();
                writer.writeRecord((String[])entry.getValue());
            }
            
            writer.close();
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return outputFile;
    }
    
}