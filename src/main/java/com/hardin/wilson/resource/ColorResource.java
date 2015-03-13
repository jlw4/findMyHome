package com.hardin.wilson.resource;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.codahale.metrics.annotation.Timed;
import com.hardin.wilson.pojo.kml.GoogleKmlRoot;
import com.hardin.wilson.pojo.kml.Style;

/**
 * Returns a sorted list of colors used in the map in hex (RRGGBB)
 */
@Path("/colors")
@Produces(MediaType.APPLICATION_JSON)
public class ColorResource {
    
    private static final File BASE_KML_FILE = new File("data/neighborhoods.kml");
    private static final Logger logger = Logger.getLogger(ColorResource.class);
    private static String[] colors;
    
    public static final int NUM_COLORS = 5;
    
    public ColorResource() {
        try {
            colors = new String[NUM_COLORS];
            JAXBContext jaxbContext = JAXBContext.newInstance(GoogleKmlRoot.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            GoogleKmlRoot kml = (GoogleKmlRoot) jaxbUnmarshaller.unmarshal(BASE_KML_FILE);
            for (Style style : kml.document.styles) {
                try {
                    int i = Integer.parseInt(style.id);
                    if (i >= 0 && i < NUM_COLORS) {
                        String color = "#" + style.polyStyle.color.substring(6);
                        color += style.polyStyle.color.substring(4, 6);
                        color += style.polyStyle.color.substring(2, 4);
                        colors[i] = color;
                    }
                }
                catch (NumberFormatException nfe) {
                    // no problem die quietly
                }
            }
        }
        catch (JAXBException e) {
            logger.error("Crap!", e);
        }
    }

    @GET
    @Timed
    public String[] getColors() {
        return colors;
    }

}
