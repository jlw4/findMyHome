package com.hardin.wilson.app;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardin.wilson.pojo.Coordinate;
import com.hardin.wilson.pojo.Descriptions;
import com.hardin.wilson.pojo.Neighborhood;
import com.hardin.wilson.pojo.Rating;
import com.hardin.wilson.pojo.Region;
import com.hardin.wilson.pojo.School;
import com.hardin.wilson.pojo.Schools;
import com.hardin.wilson.pojo.kml.GoogleKmlRoot;
import com.hardin.wilson.pojo.kml.Placemark;
import com.hardin.wilson.pojo.kml.Polygon;

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
	public static final String KML_FILE = "data/neighborhoods.kml";
	public static final String DESCRIPTIONS_FILE = "data/neighborhood_descriptions.json";

	public static final double SCHOOL_MARGIN = 0.015;
	public static final String SEATTLE_DISTRICT_ID = "229";

	private SortedMap<String, Neighborhood> neighborhoods;
	private SortedMap<Double, Neighborhood> longMap;
	private SortedMap<Double, Neighborhood> latMap;

	private static NeighborhoodContainer instance;
	private static Logger logger = Logger.getLogger(NeighborhoodContainer.class);

	/**
	 * Private constructor, reads in files and sets up
	 */
	private NeighborhoodContainer() {
		neighborhoods = new TreeMap<String, Neighborhood>();
		longMap = new TreeMap<Double, Neighborhood>();
		latMap = new TreeMap<Double, Neighborhood>();
		setup();
	}
	
	private void setup() {
	    try {
            initNeighborhoods();
            initBoundary();
            initDescriptions();
            initRandomRatings();
            assignSchoolsToNeighborhoods();
        } catch (Exception e) {
            logger.log(Level.ERROR, "Error initializing container: " + e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.log(Level.ERROR, sw.toString());
        }
	}
	
	// temporary, just adds some random ratings so I have something to show in the ui
	private void initRandomRatings() {
	    Random randy = new Random();
	    for (Rating r : Rating.values()) {
	        for (Neighborhood n : neighborhoods.values()) {
	            n.addRating(r, randy.nextInt(100));
	        }
	    }
	}
	
	private void initNeighborhoods() throws IOException, JsonParseException, JsonMappingException {
	    logger.info("Initializing neighborhoods..");
	    long time = System.currentTimeMillis();
	    ObjectMapper mapper = new ObjectMapper();
        ArrayList<Region> regions = mapper.readValue(
                new File(REGION_FILE),
                mapper.getTypeFactory().constructCollectionType(
                        ArrayList.class, Region.class));
        for (Region r : regions) {
            Neighborhood n = new Neighborhood(r.getName(), r.getLatitude(), r.getLongitude());
            neighborhoods.put(n.getName(), n);
            longMap.put(r.getLongitude(), n);
            latMap.put(r.getLatitude(), n);
        }
        time = System.currentTimeMillis() - time;
        logger.info("Successfully initalized neighborhoods in " + time + " ms");
	}
	
	// pre: initNeighborhoods()
	private void initBoundary() throws JAXBException {
	    logger.info("Initializing boundaries..");
        long time = System.currentTimeMillis();
	    JAXBContext jaxbContext = JAXBContext.newInstance(GoogleKmlRoot.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        GoogleKmlRoot kml = (GoogleKmlRoot) jaxbUnmarshaller.unmarshal(new File(KML_FILE));
        Placemark[] placemarks = kml.document.folder.placemarks;
        for (Placemark p : placemarks) {
            Neighborhood n = neighborhoods.get(p.name);
            String coords = "";
            if (p.multiGeometry != null) {
                for (Polygon poly : p.multiGeometry.polygons) {
                    coords += poly.outerBoundaryIs.linearRing.coordinates;
                }
            }
            else {
                coords = p.polygon.outerBoundaryIs.linearRing.coordinates;
            }
            // ugly string parsing to make a coordinate, yay!
            for (String s : coords.split("\\s+")) {
                if (!s.isEmpty()) {
                    s = s.substring(0, s.length() - 2);
                    String[] longLat = s.split(",");
                    try {
                        n.addCoordinate(new Coordinate(Double.parseDouble(longLat[0]),Double.parseDouble(longLat[1])));
                    }
                    catch (NumberFormatException nfe) {
                        logger.log(Level.WARN, "error parsing coordinate for neighborhood " + n.getName());
                        logger.log(Level.WARN, nfe.getMessage());
                    }
                }
            }
        }
        // make sure every neighborhood has a boundary
        for (Neighborhood n : neighborhoods.values()) {
            if (n.getBoundary().size() < 3) {
                logger.error("boundary error for " + n.getName());
            }
        }
        time = System.currentTimeMillis() - time;
        logger.info("Successfully initalized boundaries in " + time + " ms");
	}
	
	// pre: initNeighborhoods()
	private void initDescriptions() {
	    ObjectMapper mapper = new ObjectMapper();
	    try {
	        Descriptions desc = mapper.readValue(new File(DESCRIPTIONS_FILE), Descriptions.class);
	        for (String name : desc.getCitations().keySet()) {
	            neighborhoods.get(name).addDescription(desc.getDescription(name), desc.getCitation(name));
	        }
	    }
	    catch (Exception e) {
	        logger.log(Level.ERROR, "Error initializing descriptions", e);
	    }
	}
	
	// pre: initBoundary()
	// Assigns schools to the neighborhoods they belong to, and finally has all the schools compute their average score
	// Schools are added to a neighborhood if the neighborhood has a boundary point within SCHOOL_MARGIN distance
	// Also, every neighborhood needs at least 1 middle school, high school and elementary school,
	// so after the boundary assignments, neighborhoods missing a school or two are given their closest of each type
	private void assignSchoolsToNeighborhoods() {
	    try {
    	    long time = System.currentTimeMillis();
    	    ObjectMapper mapper = new ObjectMapper();
    	    Schools schools = mapper.readValue(new File(SCHOOL_FILE), Schools.class);
            logger.info("Parsing " + schools.getSchools().size() + " schools");
            Set<School> highSchools = new HashSet<School>();
            Set<School> middleSchools = new HashSet<School>();
            Set<School> elementarySchools = new HashSet<School>();
            for (School s : schools.getSchools()) {
                if (s.getGsRating() == 0 || s.getDistrictId() == null || !s.getDistrictId().equals(SEATTLE_DISTRICT_ID))
                    continue;
                if (s.getGradeRange().contains("K"))
                    highSchools.add(s);
                else if (s.getGradeRange().contains("8"))
                    middleSchools.add(s);
                else if (s.getGradeRange().contains("12"))
                    elementarySchools.add(s);
                else
                    logger.log(Level.WARN, "Failed to classify school " + s.getName());
                boolean foundNeighborhood = false;
                Coordinate sCoord = new Coordinate(s.getLon(), s.getLat());
                for (Neighborhood n : neighborhoods.values()) {
                    // case 1: assign school to all neighborhoods that have a boundary coord within SCHOOL_MARGIN distance of school
                    for (Coordinate c : n.getBoundary()) {
                        if (sCoord.distance(c) < SCHOOL_MARGIN) {
                            n.addSchool(s);
                            foundNeighborhood = true;
                            break;
                        }
                    }
                }
                if (!foundNeighborhood) {
                    logger.log(Level.WARN, "Didn't find a boundary for " + s.getName());
                    // we didn't find a neighborhood for this school with our boundary search, find the closest center
                    // NOTE: this doesn't happen with a SCHOOL_MARGIN = 0.015, but may happen if we reduce it so I'm leaving it here
                    double minDist = Double.MAX_VALUE;
                    Neighborhood closest = null;
                    for (Neighborhood n : neighborhoods.values()) {
                        double distance = sCoord.distance(n.getCenter());
                        if (distance < minDist) {
                            minDist = distance;
                            closest = n;
                        }
                    }
                    logger.info("Adding to " + closest.getName() + " distance = " + minDist);
                    closest.addSchool(s);
                }
            }
            // make sure every neighborhood has at least one of each school
            for (Neighborhood n : neighborhoods.values()) {
                boolean hasHS = false;
                boolean hasMS = false;
                boolean hasEM = false;
                for (School s : n.getSchools()) {
                    if (highSchools.contains(s))
                        hasHS = true;
                    else if (middleSchools.contains(s))
                        hasMS = true;
                    else if(elementarySchools.contains(s))
                        hasEM = true;
                    else
                        logger.log(Level.WARN, "Failed to classify school " + s.getName());
                }
                if (!hasHS)
                    addClosestSchool(highSchools, n);
                if (!hasMS)
                    addClosestSchool(middleSchools, n);
                if (!hasEM)
                    addClosestSchool(elementarySchools, n);
            }
            for (Neighborhood n : neighborhoods.values()) {
                n.computeAverageSchoolRating();
            }
            time = System.currentTimeMillis() - time;
            logger.info("Successfully initialized schools in " + time + " ms");
	    }
	    catch (Exception e) {
	        logger.log(Level.ERROR, "Error initializing schools", e);
	    }
	}
	
	private void addClosestSchool(Set<School> schools, Neighborhood n) {
	    double minDist = Double.MAX_VALUE;
	    School closest = null;
	    for (School s : schools) {
            Coordinate sCoord = new Coordinate(s.getLon(), s.getLat());
	        double dist = n.getCenter().distance(sCoord);
	        if (dist < minDist) {
	            minDist = dist;
	            closest = s;
	        }
	    }
	    n.addSchool(closest);
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

	// returns null if key is not found
	public Neighborhood getNeighborhood(String name) {
		return neighborhoods.get(name);
	}

	public Set<String> getNames() {
		return neighborhoods.keySet();
	}

	public List<Neighborhood> getNeighborhoods() {
		return new ArrayList<Neighborhood>(neighborhoods.values());
	}
}
