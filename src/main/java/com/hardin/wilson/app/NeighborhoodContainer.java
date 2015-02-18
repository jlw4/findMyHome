package com.hardin.wilson.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardin.wilson.pojo.Neighborhood;
import com.hardin.wilson.pojo.Region;
import com.hardin.wilson.pojo.School;
import com.hardin.wilson.pojo.Schools;

/**
 * Working title..
 * 
 * Main object for sorting, storing neighborhoods etc
 * 
 * Singleton, use NeighborhoodContainer.getContainer()
 */
public class NeighborhoodContainer {
    
    // files
    public static final String REGION_FILE = "data/zillow_regions.json";
    public static final String SCHOOL_FILE = "data/school.json";
    
    public static final double SCHOOL_RADIUS = 0.0231;
    
    private List<Neighborhood> neighborhoods;
    private List<String> neighborhoodNames;
    private SortedMap<Double, Neighborhood> longMap;
    private SortedMap<Double, Neighborhood> latMap;
    private SortedMap<Integer, Neighborhood> schoolMap;
    
    private static NeighborhoodContainer instance;
    
    /**
     * Private constructor, reads in files and sets up 
     */
    private NeighborhoodContainer() {
        neighborhoods = new ArrayList<Neighborhood>();
        neighborhoodNames = new ArrayList<String>();
        longMap = new TreeMap<Double, Neighborhood>();
        latMap = new TreeMap<Double, Neighborhood>();
        schoolMap = new TreeMap<Integer, Neighborhood>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            ArrayList<Region> regions = mapper.readValue(new File(REGION_FILE), mapper.getTypeFactory().constructCollectionType(ArrayList.class, Region.class));
            for (Region r : regions) {
                neighborhoodNames.add(r.getName());
                Neighborhood n = new Neighborhood(r.getName(), r.getLatitude(), r.getLongitude());
                neighborhoods.add(n);
                longMap.put(r.getLongitude(), n);
                latMap.put(r.getLatitude(), n);
            }
            Schools schools = mapper.readValue(new File(SCHOOL_FILE), Schools.class);
            System.out.println("Parsing " + schools.getSchools().size() + " schools");
            for (School s : schools.getSchools()) {
                if (s.getGsRating() != 0) {
                    Set<Neighborhood> hoods = new HashSet<Neighborhood>();
                    double r = SCHOOL_RADIUS;
                    while (hoods.isEmpty()) {
                        hoods.addAll(latMap.headMap(s.getLat() + r).tailMap(s.getLat() - r).values());
                        hoods.retainAll(longMap.headMap(s.getLon() + r).tailMap(s.getLon() - r).values());
                        r += SCHOOL_RADIUS / 4;
                    }
                    for (Neighborhood n : hoods) {
                        n.addSchool(s);
                    }
                }
            }
            for (Neighborhood n : neighborhoods) {
                n.computeAverageSchoolRating();
                schoolMap.put(n.getSchoolRating(), n);
            }
            for (Integer score : schoolMap.keySet()) {
                System.out.println(schoolMap.get(score).getName() + " : " + score);
            }
        }
        catch (Exception e) {
            System.out.println("Error parsing region file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void init() {
        instance = new NeighborhoodContainer();
    }
    
    public static NeighborhoodContainer getContainer() {
        if (instance == null) {
            init();
        }
        return instance;
    }

    public List<Neighborhood> getNeighborhoods() {
        return neighborhoods;
    }

    public void setNeighborhoods(List<Neighborhood> neighborhoods) {
        this.neighborhoods = neighborhoods;
    }
    
}
