package com.hardin.wilson.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardin.wilson.pojo.Coordinate;
import com.hardin.wilson.pojo.Neighborhood;
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

	public static final double SCHOOL_MARGIN = 0.015;

	private SortedMap<String, Neighborhood> neighborhoods;
	private SortedMap<Double, Neighborhood> longMap;
	private SortedMap<Double, Neighborhood> latMap;

	private static NeighborhoodContainer instance;

	/**
	 * Private constructor, reads in files and sets up
	 */
	private NeighborhoodContainer() {
		neighborhoods = new TreeMap<String, Neighborhood>();
		longMap = new TreeMap<Double, Neighborhood>();
		latMap = new TreeMap<Double, Neighborhood>();
		try {
			initNeighborhoods();
			initBoundary();
			assignSchoolsToNeighborhoods();
			for (Neighborhood n : neighborhoods.values()) {
				n.computeAverageSchoolRating();
			}
			System.out.println("Successfully computed school ratings");
		} catch (Exception e) {
			System.out.println("Error initializing container: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void initNeighborhoods() throws IOException, JsonParseException, JsonMappingException {
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
	}
	
	// pre: initNeighborhoods()
	private void initBoundary() throws JAXBException {
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
                        System.out.println("error parsing coordinate for neighborhood " + n.getName());
                        System.out.println("error: " + nfe.getMessage());
                    }
                }
            }
        }
        // make sure every neighborhood has a boundary
        for (Neighborhood n : neighborhoods.values()) {
            if (n.getBoundary().size() < 3) {
                System.out.println("boundary error for " + n.getName());
            }
        }
	}
	
	// pre: initBoundary()
	private void assignSchoolsToNeighborhoods() throws IOException, JsonParseException, JsonMappingException {
	    ObjectMapper mapper = new ObjectMapper();
	    Schools schools = mapper.readValue(new File(SCHOOL_FILE),
                Schools.class);
        System.out.println("Parsing " + schools.getSchools().size()
                + " schools");
        for (School s : schools.getSchools()) {
            if (s.getGsRating() != 0) {
                for (Neighborhood n : neighborhoods.values()) {
                    // does this school belong to this neighborhood?
                    // case 1: school long lat is close to center
                    Coordinate sCoord = new Coordinate(s.getLat(), s.getLon());
                    // TODO: finish. lunch time now
                    
                }
                
                
                /* Old routine
                Set<Neighborhood> hoods = new HashSet<Neighborhood>();
                double r = SCHOOL_RADIUS;
                while (hoods.isEmpty()) {
                    hoods.addAll(latMap.headMap(s.getLat() + r)
                            .tailMap(s.getLat() - r).values());
                    hoods.retainAll(longMap.headMap(s.getLon() + r)
                            .tailMap(s.getLon() - r).values());
                    r += SCHOOL_RADIUS / 4;
                }
                for (Neighborhood n : hoods) {
                    n.addSchool(s);
                }
                */
            }
        }
	}

	public static void init() {/*
		try {
			ObjectMapper mapper = new ObjectMapper();
			ArrayList<Region> regions = mapper.readValue(
					new File(REGION_FILE),
					mapper.getTypeFactory().constructCollectionType(
							ArrayList.class, Region.class));
			
            JAXBContext jaxbContext = JAXBContext.newInstance(GoogleKmlRoot.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            GoogleKmlRoot kml = (GoogleKmlRoot) jaxbUnmarshaller.unmarshal(new File("data/neighborhoods.kml"));
            
            Placemark[] placemarks = kml.document.folder.placemarks;
            
            Iterator<Region> itr = regions.iterator();
            while (itr.hasNext()) {
            	boolean match = false;
            	Region region = itr.next();
            	for (Placemark p : placemarks) {
            		if (p.name.equals(region.getName())) {
            			match = true;
            		}
            	}
            	if (!match) {
            		itr.remove();
            	}
            }
			
            for (Placemark p : placemarks) {
            	boolean match = false;
            	for (Region r : regions) {
            		if (p.name.equals(r.getName())) {
            			match = true;
            		}
            	}
        		if (!match) {
        			Region newRegion = new Region();
        			newRegion.setLatitude(0.0);
        			newRegion.setLongitude(0.0);
        			newRegion.setName(p.name);
        			regions.add(newRegion);
        		}
            }
			
			String outr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(regions);
			PrintWriter out = new PrintWriter(new File("data/new.json"));
			out.write(outr);
			out.close();
		} catch (Exception e) {
			System.out.println();
		}
		*/
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
	
	private double distance(Coordinate c1, Coordinate c2) {
	    double d1 = c1.getLatitude() - c2.getLatitude();
	    double d2 = c1.getLongitude() - c2.getLongitude();
	    return Math.sqrt((d1 * d1) + (d2 * d2));
	}
}
