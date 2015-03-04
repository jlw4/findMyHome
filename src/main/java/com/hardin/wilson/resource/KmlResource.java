package com.hardin.wilson.resource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.codahale.metrics.annotation.Timed;
import com.hardin.wilson.pojo.kml.GoogleKmlRoot;
import com.hardin.wilson.pojo.kml.Placemark;

/**
 * Finds a neighborhood by name
 */
@Path("/kml")
@Produces(MediaType.APPLICATION_XML)
public class KmlResource {
    
	private static final File BASE_KML_FILE = new File("data/neighborhoods.kml");
	private Map<String, String> kmlMap;
	private static final Logger logger = Logger.getLogger(KmlResource.class);
	
	public static final String NONE = "none";
    
	public KmlResource() {
	    logger.info("Loading Kml Map");
	    long time = System.currentTimeMillis();
	    kmlMap = new HashMap<String, String>();
		try {
		    JAXBContext jaxbContext = JAXBContext.newInstance(GoogleKmlRoot.class);
	        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	        GoogleKmlRoot kml = (GoogleKmlRoot) jaxbUnmarshaller.unmarshal(BASE_KML_FILE);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            jaxbMarshaller.marshal(kml, baos);
            kmlMap.put(NONE, baos.toString());
	        for (Placemark placemark : kml.document.folder.placemarks) {
                placemark.styleUrl = "#selected";
                baos = new ByteArrayOutputStream();
                jaxbMarshaller.marshal(kml, baos);
                kmlMap.put(placemark.name, baos.toString());
                placemark.styleUrl = "#poly-95CAFF-1-127";
            }
		} catch (Exception e) {
			System.err.println("Fatal error initializing KML resource");
			e.printStackTrace();
			System.exit(1);
		}
		time = System.currentTimeMillis() - time;
		logger.info("Loaded Kml Map in " + time + " ms.");
	}
	
    /**
     * 
     * Resource to allow google maps to access kml data for drawing neighborhood
     * boundaries.
     * 
     * @param neighborhood The neighborhood to be highlighted.
     * @return The kml to display the boundary shapes.
     */
    @GET
    @Timed
    public String getKml(@QueryParam("neighborhood") String neighborhood) {
        if (kmlMap.containsKey(neighborhood)) {
            return kmlMap.get(neighborhood);
        }
        else {
            throw new WebApplicationException(Response
				.status(HttpURLConnection.HTTP_BAD_REQUEST)
				.entity("Unknown neighborhood").build());
        }
    }

}