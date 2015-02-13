package com.hardin.wilson.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardin.wilson.pojo.Neighborhood;
import com.hardin.wilson.pojo.Region;

/**
 * Working title..
 * 
 * Main object for sorting, storing neighborhoods etc
 */
public class NeighborhoodContainer {
    
    public static final String REGION_FILE = "data/zillow_regions.json";
    
    private List<Neighborhood> neighborhoods;
    
    public NeighborhoodContainer() {
        neighborhoods = new ArrayList<Neighborhood>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            ArrayList<Region> regions = mapper.readValue(new File(REGION_FILE), mapper.getTypeFactory().constructCollectionType(ArrayList.class, Region.class));
            System.out.println("Successfully deseralized " + regions.size() + " regions");
            // TODO: convert regions into neighborhoods
        }
        catch (Exception e) {
            System.out.println("Error parsing region file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Neighborhood> getNeighborhoods() {
        return neighborhoods;
    }

    public void setNeighborhoods(List<Neighborhood> neighborhoods) {
        this.neighborhoods = neighborhoods;
    }
    
}
