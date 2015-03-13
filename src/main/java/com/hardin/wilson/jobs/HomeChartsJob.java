package com.hardin.wilson.jobs;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.List;

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
			+ "neighborhood=%s&chartDuration=5years&width=400&height=200";
	
	private static final String CHARTS_FILE = "view/img/charts/";
	
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
			String url = String.format(Z_CHARTS_API, UriEncoder.encode("\"" + n.getName() + "\""));
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
	 */
}
