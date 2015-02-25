package com.hardin.wilson.resource;

import java.io.File;
import java.net.HttpURLConnection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;
import com.hardin.wilson.pojo.kml.GoogleKmlRoot;
import com.hardin.wilson.pojo.kml.Placemark;

/**
 * Finds a neighborhood by name
 */
@Path("/kml")
@Produces(MediaType.APPLICATION_XML)
public class KmlResource {
	private static final File BASE_KML_FILE = new File("data/neighborhoods.kml");
    
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
    	JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);

        GoogleKmlRoot kml = null;
        String kmlResponse = null;
        
        try {
        	kml = mapper.readValue(BASE_KML_FILE, GoogleKmlRoot.class);
        	
        	if (neighborhood != null) {
            	// Loop through each boundary and check if the name matches our neighborhood
        		// name and change its kml style to indicate that it is selected.
        		for(Placemark placemark : kml.getDocument().getFolder().getPlacemark()) {
        			if (placemark.getName().equals(neighborhood)) {
        				placemark.setStyleUrl("#selected");
        			}
        		}
        	}
        	
        	kmlResponse = mapper.writeValueAsString(kml);
        } catch (Exception e) {
			throw new WebApplicationException(Response
					.status(HttpURLConnection.HTTP_BAD_REQUEST)
					.entity(e.getMessage()).build());
        }
        
        return kmlResponse;
    }

}