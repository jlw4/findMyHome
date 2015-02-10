package com.hardin.wilson.jobs;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hardin.wilson.pojo.School;
import com.hardin.wilson.pojo.Schools;

public class GreatSchoolsJob implements ProcessingJob {
	
	// TODO: Move API key to a config file.
	private static final String GS_URL = 
			"http://api.greatschools.org/schools/WA/Seattle?key=e8w1s8bocy0yanumjauksaxx&limit=10000";
	
	private static final File OUT = new File("data/school.json");
	
	/**
	 * 
	 * Gets school information and outputs it to a file.
	 * 
	 */
	public void run() throws Exception {
		// Setup target URL.
		URL urlObj = new URL(GS_URL);
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
		
		// Marshal the xml data into the schools object.
		ObjectMapper xmlMapper = new XmlMapper();
		Schools schools = xmlMapper.readValue(response.toString(), Schools.class);
		
		// Iterate over schools and remove the non-public schools as we only
		// want to work with public for now.
		Iterator<School> itr = schools.getSchools().iterator();
		while (itr.hasNext()) {
			School school = itr.next();
			if (!school.getType().equals("public")) {
				itr.remove();
			}
		}
		
		// Write to the JSON data file.
		String jsonString = new ObjectMapper().writerWithDefaultPrettyPrinter()
				.writeValueAsString(schools);
		PrintWriter out = new PrintWriter(OUT);
		out.write(jsonString);
		out.close();
        
	}
}
