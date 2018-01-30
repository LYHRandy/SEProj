/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.entity;

/**
 * Location Object to be used for CRUD
 */
public class Location {
    private String timeStamp;
    private String macAddress;
    private String locationID;
    
    /**
     * location entity constructor
     * 
     * @param timeStamp dateTime of location
     * @param macAddress macAddress at specific location
     * @param locationID locationID of which user is at, at specific time
     */
    public Location(String timeStamp, String macAddress, String locationID){
        this.timeStamp = timeStamp;
        this.macAddress = macAddress;
        this.locationID = locationID;
    }
    
    /**
     * returns timestamp
     * @return timeStamp of user in location
     */
    public String getTimeStamp() {
        return timeStamp;
    }
    
    /**
     * returns macaddress
     * @return MacAddress in location 
     */
    public String getMacAddress() {
        return macAddress;
    }
    
    /**
     * returns locationID
     * @return locationID of which user is at
     */
    public String getLocationID() {
        return locationID;
    }

}
