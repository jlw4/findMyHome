package com.hardin.wilson.jobs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ProcessingJob {
	private static Logger log = LogManager.getLogger(ProcessingJob.class);
	
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
			log.error("Unable to send URL request to fetch data source: " + e.getMessage());
			e.printStackTrace(System.err);
		}
		
		return data;
	}
}
