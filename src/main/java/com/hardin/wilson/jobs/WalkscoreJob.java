package com.hardin.wilson.jobs;

import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardin.wilson.app.NeighborhoodContainer;
import com.hardin.wilson.pojo.Neighborhood;
import com.hardin.wilson.pojo.NeighborhoodRatings;
import com.hardin.wilson.pojo.Rating;
import com.hardin.wilson.pojo.TransitScore;
import com.hardin.wilson.pojo.WalkScore;

public class WalkscoreJob extends ProcessingJob {
	private static Logger logger = Logger.getLogger(WalkscoreJob.class);
	
	private static final String QUERY_PARAMS = "wsapikey=f732515e1de3721af2497ffd8266adbf" 
			+ "&city=Seattle&state=WA&lat=%s&lon=%s";
	
	private static final String WALKSCORE_PREFIX = "http://api.walkscore.com/score?format=json&";
	private static final String TRANSITSCORE_PREFIX = "http://transit.walkscore.com/transit/score/?";
	
	@Override
	public void run() {
		List<Neighborhood> hoods = NeighborhoodContainer.getContainer().getNeighborhoods();
		List<NeighborhoodRatings> nrs = readRatingsFile();
		ObjectMapper mapper = new ObjectMapper();
		
		for (Neighborhood hood : hoods) {
			// Inject the longitude and latitude values.
			String formatURI = String.format(QUERY_PARAMS, hood.getLongitude(), hood.getLatitude());
			String walkscoreJson = fetchResource(WALKSCORE_PREFIX + formatURI);
			String transitscoreJson = fetchResource(TRANSITSCORE_PREFIX + formatURI);
			
			WalkScore walkScore = null;
			TransitScore transitScore = null;
			try {
				walkScore = mapper.readValue(walkscoreJson, WalkScore.class);
				transitScore = mapper.readValue(transitscoreJson, TransitScore.class);
			} catch (Exception e) {
				logger.error("Unable to deserialize walkscore data: " + e.getMessage());
			}
			
			// Update rating info to store walkscore and transit score ratings.
			for (NeighborhoodRatings nr : nrs) {
				if (nr.getName().equals(hood.getName())) {
					nr.getRatings().put(Rating.WALK_SCORE, walkScore.getWalkscore());
					nr.getRatings().put(Rating.TRANSIT_SCORE, transitScore.getTransit_score());
				}
			}
		}
		
		writeRatingsFile(nrs);
	}
}
