/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.entity;

/**
 * Location Lookup Object to be used for CRUD
 */
public class LocationLookup {
    private String locationId;
    private String semanticPlace;
    private String floor;
    
    /**
     * locationlookup constructor
     * 
     * @param locationId locationID of specific semanticPlace
     * @param semanticPlace Name of semanticPlace
     * @param floor Floor of which semanticPlace is located at
     */
    public LocationLookup(String locationId, String semanticPlace, String floor){
        this.locationId = locationId;
        this.semanticPlace = semanticPlace;
        this.floor = floor;
    }
    
    /**
     * returns locationID
     * @return locationID of a specific SemanticPlace
     */
    public String getLocationId() {
        return locationId;
    }

    /**
     * returns semanticplace
     * @return name of location of specific locationID
     */
    public String getSemanticPlace() {
        return semanticPlace;
    }
    
    /**
     * returns floor
     * @return floor of which location is at
     */
    public String getFloor() {
        return floor;
    }
}
