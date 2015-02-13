package com.hardin.wilson.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A "region" for deserializing a Zillow region (zillow_regions.xml)
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Region {
    
    private String name;
    private double longitude;
    private double latitude;
    
    public Region() {
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

}
