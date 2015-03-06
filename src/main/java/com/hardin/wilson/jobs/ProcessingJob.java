package com.hardin.wilson.jobs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardin.wilson.pojo.NeighborhoodRatings;

public abstract class ProcessingJob {
	private static Logger logger = LogManager.getLogger(ProcessingJob.class);
	
	/**
	 * Runs a specified processing job.
	 */
	public void run() throws Exception {}
	
	/**
	 * Performs a GET against the 'url' and returns the data from the response
	 * in a String format.
	 * 
	 * @param url The url to GET data from (ensure uri is encoded).
	 * @return A string of the data in the response message. Empty string if no message
	 * was returned or an error was encountered.
	 */
	public static String fetchResource(String url) {
		String data = "";
		
		try {
			URL urlObj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
			
			// Get response;
			con.getResponseCode();
			
			// Extract the response message.
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuffer response = new StringBuffer();
	 
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			data = response.toString();
		} catch (Exception e) {
			logger.error("Unable to send URL request to fetch data source: " + e.getMessage());
			e.printStackTrace(System.err);
		}
		
		return data;
	}
	
	/**
	 * 	Reads in the neighborhood ratings from disk into a POJO.
	 */
	public static List<NeighborhoodRatings> readRatingsFile() {
		// Read the existing neighborhood ratings file in
		ObjectMapper jsonMapper = new ObjectMapper();
		List<NeighborhoodRatings> nrs;
		try {
			nrs = jsonMapper.readValue(NeighborhoodRatings.ratingsFile, 
					new TypeReference<List<NeighborhoodRatings>>(){});
		} catch (Exception e) {
			logger.error("Error reading ratings from file: " + e.getMessage());
			System.err.println(e.getStackTrace());
			return null;
		}
		
		return nrs;
	}
	
	/**
	 * 	Writes the neighborhood ratings back to disk.
	 */
	public static void writeRatingsFile(List<NeighborhoodRatings> nrs) {
		// Write back to the file on disk with new updated schools ratings.
		try {
			String jsonString = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(nrs);
			PrintWriter out = new PrintWriter(NeighborhoodRatings.ratingsFile);
			out.write(jsonString);
			out.close();
		} catch (Exception e) {
			logger.error("Error writing JSON to file: " + e.getMessage());
			System.err.println(e.getStackTrace());
			return;
		}
	}
}
