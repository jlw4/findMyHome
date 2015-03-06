package com.hardin.wilson.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hardin.wilson.app.NeighborhoodContainer;
import com.hardin.wilson.pojo.Coordinate;
import com.hardin.wilson.pojo.Neighborhood;
import com.hardin.wilson.pojo.NeighborhoodRatings;
import com.hardin.wilson.pojo.Rating;
import com.hardin.wilson.pojo.School;
import com.hardin.wilson.pojo.Schools;

/**
 *	A Job that will grab current school rating information for Seattle and update a local
 *	ratings file with the new rating information.
 */
public class GreatSchoolsJob extends ProcessingJob {
	private static Logger logger = Logger.getLogger(GreatSchoolsJob.class);

	private static final String GS_URL = 
			"http://api.greatschools.org/schools/WA/Seattle?key=e8w1s8bocy0yanumjauksaxx&limit=10000";
	
	public static final double SCHOOL_MARGIN = 0.015;
	public static final String SEATTLE_DISTRICT_ID = "229";

	
	@Override
	public void run() {
		String response = fetchResource(GS_URL);

		Schools schools = null;
		try {
			// Marshal the xml data into the schools object.
			ObjectMapper xmlMapper = new XmlMapper();
			schools = xmlMapper.readValue(response, Schools.class);
		} catch (Exception e) {
			logger.error("Error marshalling data into schools object: "
					+ e.getMessage());
			System.err.println(e.getStackTrace());
			return;
		}

		// Iterate over schools and remove the non-public schools as we only
		// want to work with public for now.
		Iterator<School> itr = schools.getSchools().iterator();
		while (itr.hasNext()) {
			School school = itr.next();
			if (!school.getType().equals("public")) {
				itr.remove();
			}
		}
		
		// Read ratings in, update with school ratings, write back to disk.
		List<NeighborhoodRatings> nrs = readRatingsFile();
		updateSchoolRatings(schools, nrs);
		writeRatingsFile(nrs);
	}
	
	/**
	 * Takes schools and updates the neighborhood ratings with new school rating information
	 * based on Great Schools data.
	 */
	private void updateSchoolRatings(Schools schools, List<NeighborhoodRatings> neighborhoodRatings) {

		long time = System.currentTimeMillis();
		logger.info("Parsing " + schools.getSchools().size() + " schools");
		
		List<Neighborhood> neighborhoods = NeighborhoodContainer.getContainer().getNeighborhoods();
		Map<Neighborhood, List<School>> assignedSchools = new HashMap<>();
		
		// Initialize an empty list of assigned schools for each neighborhood in the mapping.
		for (Neighborhood n : neighborhoods) {
			assignedSchools.put(n, new ArrayList<School>());
		}
		
		Set<School> highSchools = new HashSet<School>();
		Set<School> middleSchools = new HashSet<School>();
		Set<School> elementarySchools = new HashSet<School>();
		
		for (School s : schools.getSchools()) {
			if (s.getGsRating() == 0 || s.getDistrictId() == null
					|| !s.getDistrictId().equals(SEATTLE_DISTRICT_ID))
				continue;
			if (s.getGradeRange().contains("K"))
				highSchools.add(s);
			else if (s.getGradeRange().contains("8"))
				middleSchools.add(s);
			else if (s.getGradeRange().contains("12"))
				elementarySchools.add(s);
			else
				logger.log(Level.WARN,
						"Failed to classify school " + s.getName());
			boolean foundNeighborhood = false;
			Coordinate sCoord = new Coordinate(s.getLon(), s.getLat());
			for (Neighborhood n : neighborhoods) {
				// case 1: assign school to all neighborhoods that have a
				// boundary coord within SCHOOL_MARGIN distance of school
				for (Coordinate c : n.getBoundary()) {
					if (sCoord.distance(c) < SCHOOL_MARGIN) {
						assignedSchools.get(n).add(s);
						foundNeighborhood = true;
						break;
					}
				}
			}
			if (!foundNeighborhood) {
				logger.log(Level.WARN, "Didn't find a boundary for " + s.getName());
				// we didn't find a neighborhood for this school with our
				// boundary search, find the closest center
				// NOTE: this doesn't happen with a SCHOOL_MARGIN = 0.015, but
				// may happen if we reduce it so I'm leaving it here
				double minDist = Double.MAX_VALUE;
				Neighborhood closest = null;
				for (Neighborhood n : neighborhoods) {
					double distance = sCoord.distance(n.getCenter());
					if (distance < minDist) {
						minDist = distance;
						closest = n;
					}
				}
				logger.info("Adding to " + closest.getName() + " distance = "+ minDist);
				
				assignedSchools.get(closest).add(s);
			}
		}
		// make sure every neighborhood has at least one of each school
		for (Neighborhood n : neighborhoods) {
			boolean hasHS = false;
			boolean hasMS = false;
			boolean hasEM = false;
			for (School s : assignedSchools.get(n)) {
				if (highSchools.contains(s))
					hasHS = true;
				else if (middleSchools.contains(s))
					hasMS = true;
				else if (elementarySchools.contains(s))
					hasEM = true;
				else
					logger.log(Level.WARN, "Failed to classify school " + s.getName());
			}
			if (!hasHS)
				assignedSchools.get(n).add(getClosestSchool(highSchools, n));
			if (!hasMS)
				assignedSchools.get(n).add(getClosestSchool(middleSchools, n));
			if (!hasEM)
				assignedSchools.get(n).add(getClosestSchool(elementarySchools, n));
		}
		for (Neighborhood n : neighborhoods) {
			// Compute average rating
			int sum = 0;
			for (School s : assignedSchools.get(n)) {
				sum += s.getGsRating() * 10;
			}
			
			for (NeighborhoodRatings nr : neighborhoodRatings) {
				if (nr.getName().equals(n.getName())) {
					nr.getRatings().put(Rating.SCHOOL.getName(), 
							(int) (sum / (assignedSchools.get(n).size() * 1.0)));
				}
			}
			
		}
		time = System.currentTimeMillis() - time;
		logger.info("Successfully initialized schools in " + time + " ms");
	}
	
	/**
	 * Gets the closest school in proximity to a neighborhood n from a given set of
	 * schools.
	 */
	private School getClosestSchool(Set<School> schools, Neighborhood n) {
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
	    return closest;
	}
}
