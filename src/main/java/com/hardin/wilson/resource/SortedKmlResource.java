package com.hardin.wilson.resource;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.codahale.metrics.annotation.Timed;
import com.hardin.wilson.app.NeighborhoodContainer;
import com.hardin.wilson.pojo.Neighborhood;
import com.hardin.wilson.pojo.Rating;
import com.hardin.wilson.pojo.kml.GoogleKmlRoot;
import com.hardin.wilson.pojo.kml.Placemark;

@Path("/sortedKml")
@Produces(MediaType.APPLICATION_XML)
public class SortedKmlResource {
    
    private static final File BASE_KML_FILE = new File("data/neighborhoods.kml");
    private static final Logger logger = Logger.getLogger(SortedKmlResource.class);
    private GoogleKmlRoot kml;
    
    public SortedKmlResource() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(GoogleKmlRoot.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            kml = (GoogleKmlRoot) jaxbUnmarshaller.unmarshal(BASE_KML_FILE);
        }
        catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * Generates kml for the given ratings
     */
    @GET
    @Timed
    public GoogleKmlRoot getKml(@QueryParam("ratings") List<String> ratings,
                                @QueryParam("neighborhood") String neighborhood) {
        try {
            Map<String, String> styles = new HashMap<>();
            if (ratings != null && !ratings.isEmpty()) {
                for (String rating : ratings) {
                    if (!Rating.getValues().contains(rating)) {
                        logger.info("rejecting invalid rating");
                        throw new WebApplicationException(Response
                                .status(HttpURLConnection.HTTP_BAD_REQUEST)
                                .entity("invalid rating: " + rating).build());
                    }
                }
                List<Neighborhood> neighborhoods = NeighborhoodContainer.getContainer().getNeighborhoods();
                // compute a score from 0-8 for each neighborhood based on given ratings
                int chunkSize = ( 100 * ratings.size() ) / 9;
                for (Neighborhood n : neighborhoods) {
                    int score = 0;
                    for (String rating : ratings) {
                        score += n.getRating(rating);
                    }
                    score /= chunkSize;
                    score = Math.min(8, score); // fix for edge case..
                    styles.put(n.getName(), "#" + score);
                }
            }
            if (neighborhood != null) {
                styles.put(neighborhood, "#selected");
            }
            // so it sucks to synchronize the kml here, but fortunately Google caches kml,
            // so this resource shouldn't get hit too hard. We could change this to perform a deep clone of kml
            // at the beginning of each call, but I'm not sure that would perform any better.
            synchronized (kml) {
                for (Placemark placemark : kml.document.folder.placemarks) {
                    if (styles.containsKey(placemark.name))
                        placemark.styleUrl = styles.get(placemark.name);
                    else
                        placemark.styleUrl = "#poly-95CAFF-1-127";
                }
                return kml;
            }
        }
        catch (Exception e) {
            logger.error("Crap!", e);
        }
        throw new WebApplicationException(Response
                .status(HttpURLConnection.HTTP_INTERNAL_ERROR)
                .entity("sorry!").build());
    }

}
