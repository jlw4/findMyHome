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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

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
    public GoogleKmlRoot getKml(@QueryParam("neighborhood") String neighborhood) {
    	GoogleKmlRoot kml = null;

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(GoogleKmlRoot.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            kml = (GoogleKmlRoot) jaxbUnmarshaller.unmarshal(BASE_KML_FILE);
            
            if (neighborhood != null) {
            	// If a neighborhood query param was specified, then we need to find this name
            	// and set the style URL to be the '#selected' style.
	            for (Placemark placemark : kml.document.folder.placemarks) {
	            	if (placemark.name.equals(neighborhood)) {
	            		placemark.styleUrl = "#selected";
	            		break;
	            	}
	            }
            }

        } catch (Exception e) {
			throw new WebApplicationException(Response
					.status(HttpURLConnection.HTTP_BAD_REQUEST)
					.entity(e.getMessage()).build());
        }
        
        return kml;
    }

}