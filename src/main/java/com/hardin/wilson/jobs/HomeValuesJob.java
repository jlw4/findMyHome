package com.hardin.wilson.jobs;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.util.UriEncoder;
import com.hardin.wilson.app.NeighborhoodContainer;
import com.hardin.wilson.pojo.Neighborhood;
import com.hardin.wilson.pojo.NeighborhoodRatings;
import com.hardin.wilson.pojo.Rating;

public class HomeValuesJob extends ProcessingJob {
	private static Logger logger = Logger.getLogger(HomeValuesJob.class);
	
	private static final String Z_DEMO_API = "http://www.zillow.com/webservice/GetDemographics.htm"
			+ "?zws-id=X1-ZWz1e2jeba9wy3_52lx1&city=Seattle&state=WA&neighborhood=%s";
	
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
		Map<Neighborhood, Double> homeValues = new HashMap<Neighborhood, Double>();
		double max = 0;
		
		for (Neighborhood n : neighborhoods) {
			
			String url = String.format(Z_DEMO_API, UriEncoder.encode("\"" + n.getName() + "\""));
			String xml = fetchResource(url);
			
			try {
				InputSource source = new InputSource(new StringReader(xml));
				Document doc = builder.parse(source);
				
				NodeList nl = doc.getElementsByTagName("attribute");
				
				for (int i = 0; i < nl.getLength(); i++) {
					Node node = nl.item(i);
					String test = node.getFirstChild().getTextContent();
					if (test.equals("Zillow Home Value Index")) {
						double median = (double) Integer.parseInt(
								node.getChildNodes().item(1).getFirstChild().getTextContent());
						homeValues.put(n, median);
						max = Math.max(median, max);
						break;
					}
				}
				
			} catch (Exception e) {
				logger.error("Unable to build document tree from xml for home values: " + e.getMessage());
				return;
			}
		}
		
		max = max * 1.2;
		for (Neighborhood n : neighborhoods) {
			
			// Extremely janky solution to the two neighborhoods that do not match.
			if (n.getName().equals("Queen Anne")) {
				homeValues.put(n, 662000.0);
			} 
			if (n.getName().equals("West Seattle")) {
				homeValues.put(n, 425000.0);
			}
			
			
			homeValues.put(n, homeValues.get(n) / max);
			homeValues.put(n, Math.rint((100 - (homeValues.get(n) * 100))));
		}
		
		// Get current ratings data.
		List<NeighborhoodRatings> nrs = readRatingsFile();
		
		for (NeighborhoodRatings nr : nrs) {
			for (Neighborhood n : neighborhoods) {
				if (n.getName().equals(nr.getName())) {
					nr.getRatings().put(Rating.AFFORDABILITY, homeValues.get(n).intValue());
				}
			}
		}
		
		// Update the ratings data
		writeRatingsFile(nrs);
	}
}
