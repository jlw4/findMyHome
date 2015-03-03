package com.hardin.wilson.jobs;

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
	private static final Logger log = LogManager
			.getLogger(CrimeReportsJob.class);
	private static final String BASE_CRIME_URL = "https://data.seattle.gov/resource/3k2p-39jp.json?";
	private static final String OUTPUT = "data/crimes.json";
	
	private static final double ASSAULTS_WEIGHT = 1.0;
	private static final double HOMICIDE_WEIGHT = 1.0;
	private static final double AUTO_THEFTS_WEIGHT = 1.0;
	private static final double ROBBERY_WEIGHT = 1.0;
	private static final double THEFT_WEIGHT = 1.0;

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

		try {
			List<CrimeReport> crimes = new ArrayList<CrimeReport>();
			ObjectMapper mapper = new ObjectMapper();
			int offset = 0;
			int records = 0;
			
			// The maximum number of records we can grab from openData is 50000
			// at a time so we need to page through the data set to get everything.
			do {
				String encodedUri = UriEncoder.encode(uri + "&$offset=" + offset);
				String response = fetchResource(BASE_CRIME_URL + encodedUri);

				List<CrimeReport> newCrimes = mapper
						.readValue(response, new TypeReference<ArrayList<CrimeReport>>() {});
				crimes.addAll(newCrimes);
				records = newCrimes.size();
				offset += 50000;
			} while (records == offset);
			
			// Get all the neighborhood objects.
			List<Neighborhood> neighborhoods = NeighborhoodContainer.getContainer().getNeighborhoods();
			
			Map<Neighborhood, Double> crimeCount = new HashMap<Neighborhood, Double>();
			
			double maxCount = 0.0;
			for (Neighborhood neighborhood : neighborhoods) {
				double count = 0.0;
				for (CrimeReport crime : crimes) {
					if (neighborhood.isInsideBounds(new Coordinate(crime.getLongitude(), crime.getLatitude()))) {
						switch(crime.getEvent_clearance_subgroup()) {
							case "ASSAULTS" 	:	count += ASSAULTS_WEIGHT;
							case "HOMICIDE" 	: 	count += HOMICIDE_WEIGHT;
							case "AUTO THEFTS" 	:	count += AUTO_THEFTS_WEIGHT;
							case "ROBBERY"		:	count += ROBBERY_WEIGHT;
							case "THEFT"		: 	count += THEFT_WEIGHT;
						}
					}
				}
				maxCount = Math.max(maxCount, count);
				crimeCount.put(neighborhood, count);
			}

			
			for (Neighborhood n : neighborhoods) {
				crimeCount.put(n, Math.floor(((crimeCount.get(n) / maxCount) * 100)) );
				System.out.println(n.getName() + ": " + crimeCount.get(n));
			}
			
			/*
			// Write this massive JSON string to file
			PrintWriter out = new PrintWriter(OUTPUT);
			out.write(mapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(crimes));
			out.close();*/
		} catch (Exception e) {
			log.error("Unable to write to file " + OUTPUT + ": "
					+ e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}
