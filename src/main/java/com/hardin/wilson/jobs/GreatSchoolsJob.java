package com.hardin.wilson.jobs;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hardin.wilson.pojo.School;
import com.hardin.wilson.pojo.Schools;

public class GreatSchoolsJob extends ProcessingJob {
	private static final Logger log = LogManager.getLogger(GreatSchoolsJob.class);

	private static final String GS_URL = 
			"http://api.greatschools.org/schools/WA/Seattle?key=e8w1s8bocy0yanumjauksaxx&limit=10000";

	private static final File OUTPUT = new File("data/school.json");

	/**
	 * 
	 * Gets school information and outputs it to a file.
	 * 
	 */
	@Override
	public void run() {
		String response = fetchResource(GS_URL);

		Schools schools = null;
		try {
			// Marshal the xml data into the schools object.
			ObjectMapper xmlMapper = new XmlMapper();
			schools = xmlMapper.readValue(response, Schools.class);
		} catch (Exception e) {
			log.error("Error marshalling data into schools object: "
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

		// Write to the JSON data file.
		try {
			String jsonString = new ObjectMapper()
					.writerWithDefaultPrettyPrinter().writeValueAsString(schools);
			PrintWriter out = new PrintWriter(OUTPUT);
			out.write(jsonString);
			out.close();
		} catch (Exception e) {
			log.error("Error writing JSON to file: " + e.getMessage());
			System.err.println(e.getStackTrace());
			return;
		}
	}
}
