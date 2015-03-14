package com.hardin.wilson.jobs;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.util.UriEncoder;
import com.hardin.wilson.app.NeighborhoodContainer;
import com.hardin.wilson.pojo.Neighborhood;

public class HomeChartsJob extends ProcessingJob {
	private static Logger logger = Logger.getLogger(HomeChartsJob.class);
	
	private static final String Z_CHARTS_API = "http://www.zillow.com/webservice/GetRegionChart.htm?"
			+ "zws-id=X1-ZWz1e2jeba9wy3_52lx1&unit-type=dollar&city=Seattle&state=WA&"
			+ "neighborhood=%s&chartDuration=5years&width=600&height=200";
	
	private static final String CHARTS_FILE = "view/img/charts/";
	
	private static Map<String, String> neighborhoodRemap;
	
	/*
	 * Atlantic
	 * Broadmoor
	 * Central District
	 * Denny-Blaine
	 * Georgetown
	 * Industrial District
	 * Interbay
	 * International District
	 * Pioneer Square
	 * Queen Anne
	 */
	
	public HomeChartsJob() {
		neighborhoodRemap = new HashMap<>();
		neighborhoodRemap.put("Atlantic", "Leschi");
		neighborhoodRemap.put("Broadmoor", "Montlake");
		neighborhoodRemap.put("Central District", "Capitol Hill");
		neighborhoodRemap.put("Denny-Blaine", "Madison Park");
		neighborhoodRemap.put("Georgetown", "South Park");
		neighborhoodRemap.put("Industrial District", "North Delridge");
		neighborhoodRemap.put("Interbay", "Magnolia");
		neighborhoodRemap.put("International District", "Downtown");
		neighborhoodRemap.put("Pioneer Square", "Downtown");
		neighborhoodRemap.put("Queen Anne", "North Queen Anne");
		neighborhoodRemap.put("West Seattle", "Fairmount Park");
		neighborhoodRemap.put("South Lake Union", "Eastlake");
	}
	
	@Override
	public void run() {
		DocumentBuilder builder;
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
		} catch (Exception e) {
			logger.error("Unable to create document builder: " + e.getMessage());
			return;
		}
		
		List<Neighborhood> neighborhoods = NeighborhoodContainer.getContainer().getNeighborhoods();
		for (Neighborhood n : neighborhoods) {
			
			// Fix name discrepencies between our neighborhood names and zillow charts.
			String fetchName = neighborhoodRemap.get(n.getName());
			if (fetchName == null) {
				fetchName = n.getName();
			} 
			
			String url = String.format(Z_CHARTS_API, UriEncoder.encode("\"" + fetchName + "\""));
			String xml = fetchResource(url);
			
			try {
				InputSource source = new InputSource(new StringReader(xml));
				Document doc = builder.parse(source); 
				
				String chartUrl = doc.getElementsByTagName("url").item(0).getTextContent();
				saveChart(chartUrl, n.getName());
			} catch (Exception e) {
				logger.error("Unable to build document tree from xml for home charts: " + e.getMessage());
			}
		}
	}
	
	/**
	 * Fetches and then locally saves a chart referenced by the 
	 * 'chartUrl' under the given 'filename'.
	 */
	public void saveChart(String chartUrl, String neighborhoodName) {
		
		try {
			InputStream in = new BufferedInputStream(new URL(chartUrl).openStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n = 0;
			while (-1 != (n = in.read(buf))) {
				out.write(buf, 0, n);
			}
			out.close();
			in.close();
			byte[] response = out.toByteArray();
			
			FileOutputStream fos = new FileOutputStream(CHARTS_FILE + neighborhoodName + ".jpg");
			fos.write(response);
			fos.close();
		} catch (Exception e) {
			logger.error("Unable to save chart for neighborhood " + neighborhoodName
					+ " locally: " + e.getMessage());
		}
	}
}
