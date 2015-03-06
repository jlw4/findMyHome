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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardin.wilson.pojo.Coordinate;
import com.hardin.wilson.pojo.Descriptions;
import com.hardin.wilson.pojo.Neighborhood;
import com.hardin.wilson.pojo.NeighborhoodRatings;
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
	public static final String KML_FILE = "data/neighborhoods.kml";
	public static final String DESCRIPTIONS_FILE = "data/neighborhood_descriptions.json";

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
            initRatings();
        } catch (Exception e) {
            logger.log(Level.ERROR, "Error initializing container: " + e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.log(Level.ERROR, sw.toString());
        }
	}
	
	// temporary, just adds some random ratings so I have something to show in the ui
	private void initRatings() {
	    try {
	    	List<NeighborhoodRatings> nrs = new ObjectMapper().readValue(NeighborhoodRatings.ratingsFile, 
	    			new TypeReference<List<NeighborhoodRatings>>(){});
	    	for (Neighborhood n : neighborhoods.values()) {
	    		for (NeighborhoodRatings nr : nrs) {
	    			if (n.getName().equals(nr.getName())) {
	    				n.setRatings(nr.getRatings());
	    			}
	    		}
	    	}
	    } catch (Exception e) {
	        logger.log(Level.ERROR, "Error initializing ratings from file", e);
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
