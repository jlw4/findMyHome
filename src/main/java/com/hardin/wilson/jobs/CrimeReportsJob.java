package com.hardin.wilson.jobs;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.util.UriEncoder;
import com.hardin.wilson.app.NeighborhoodContainer;
import com.hardin.wilson.pojo.Coordinate;
import com.hardin.wilson.pojo.CrimeReport;
import com.hardin.wilson.pojo.Neighborhood;
import com.hardin.wilson.pojo.NeighborhoodRatings;
import com.hardin.wilson.pojo.Rating;

/**
 * Class that contains the logic for a job that grabs crime data from Opendata
 * Seattle. check out the following URL to see that dataset being accessed.
 * 
 * https://data.seattle.gov/Public-Safety/Seattle-Police-Department-911-Incident
 * -Response/3k2p-39jp?
 * 
 * @author Cameron
 */
public class CrimeReportsJob extends ProcessingJob {
	private static final Logger logger = LogManager.getLogger(CrimeReportsJob.class);
	private static final String BASE_CRIME_URL = "https://data.seattle.gov/resource/3k2p-39jp.json?";
	
	private static final double LOG_SCALE_MAX = 1000.0;
	
	private static final double ASSAULTS_WEIGHT = 5.0;
	private static final double HOMICIDE_WEIGHT = 50.0;
	private static final double AUTO_THEFTS_WEIGHT = 3.0;
	private static final double ROBBERY_WEIGHT = 2.0;
	private static final double THEFT_WEIGHT = 1.5;

	public void run() {
		// We only want events in the last year.
		String uri = "$where=event_clearance_date>'2014-03-01T19:00:00Z'"

				// These are the subgroup types we want. More than 20+ exist
				// if we decide to add more later.
				+ " AND event_clearance_subgroup='ASSAULTS'"
				+ " OR event_clearance_subgroup='HOMICIDE'"
				+ " OR event_clearance_subgroup='AUTO THEFTS'"
				+ " OR event_clearance_subgroup='ROBBERY'"
				+ " OR event_clearance_subgroup='THEFT'"

				// Select the columns from the dataset we are interested in.
				+ "&$select=cad_cdw_id,event_clearance_subgroup,"
				+ "longitude,latitude,event_clearance_date"

				+ "&$limit=50000";

		List<CrimeReport> crimes = new ArrayList<CrimeReport>();
		ObjectMapper mapper = new ObjectMapper();
		int offset = 0;
		int records = 0;

		try {
			// The maximum number of records we can grab from openData is 50000
			// at a time so we need to page through the data set to get
			// everything.
			do {
				String encodedUri = UriEncoder.encode(uri + "&$offset="
						+ offset);
				String response = fetchResource(BASE_CRIME_URL + encodedUri);

				List<CrimeReport> newCrimes = mapper.readValue(response,
						new TypeReference<ArrayList<CrimeReport>>() {
						});
				crimes.addAll(newCrimes);
				records = newCrimes.size();
				offset += 50000;
			} while (records == offset);
		} catch (Exception e) {
			logger.error("Unable to read crime data from OpenData Seattle: "
					+ e.getMessage());
		}

		// Get all the neighborhood objects.
		List<Neighborhood> neighborhoods = NeighborhoodContainer.getContainer()
				.getNeighborhoods();

		Map<Neighborhood, Double> crimeCount = new HashMap<Neighborhood, Double>();

		double maxCount = 0.0;
		for (Neighborhood neighborhood : neighborhoods) {
			double count = 0.0;
			for (CrimeReport crime : crimes) {
				if (neighborhood.isInsideBounds(new Coordinate(crime
						.getLongitude(), crime.getLatitude()))) {
					switch (crime.getEvent_clearance_subgroup()) {
					case "ASSAULTS":
						count += ASSAULTS_WEIGHT;
					case "HOMICIDE":
						count += HOMICIDE_WEIGHT;
					case "AUTO THEFTS":
						count += AUTO_THEFTS_WEIGHT;
					case "ROBBERY":
						count += ROBBERY_WEIGHT;
					case "THEFT":
						count += THEFT_WEIGHT;
					}
				}
			}
			maxCount = Math.max(maxCount, count);
			crimeCount.put(neighborhood, count);
		}

		// Standardize all values onto a logarithmic scale.
		for (Neighborhood n : neighborhoods) {
			crimeCount.put(n,
					(((crimeCount.get(n)) / maxCount) * LOG_SCALE_MAX));
		}
		
		// Get current ratings data.
		List<NeighborhoodRatings> nrs = readRatingsFile();

		// Set the max to be 50% higher than log max so that no neighborhood has
		// a 100 crime rating.
		double maxCountLog = Math.log10(LOG_SCALE_MAX * 1.5);
		for (Neighborhood n : neighborhoods) {
			for (NeighborhoodRatings nr : nrs) {
				if (n.getName().equals(nr.getName())) {
					crimeCount.put(n, ((Math.log10(crimeCount.get(n)) * 100) / maxCountLog));
					nr.getRatings().put(Rating.SAFETY, 100 - (int) Math.rint(crimeCount.get(n)));
					System.out.println(n.getName() + ": " + (int) Math.rint(crimeCount.get(n)));
				}
			}
		}
		
		// Update ratings.
		writeRatingsFile(nrs);
	}
}
