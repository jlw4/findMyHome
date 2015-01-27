package com.hardin.wilson.resource;

import java.net.HttpURLConnection;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;
import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.hardin.wilson.pojo.Coordinate;

/**
 * Resource for converting address to long/lat
 */

@Path("/coordinate")
@Produces(MediaType.APPLICATION_JSON)
public class CoordinateResource {
    private final AtomicLong counter;
    private static final Geocoder geocoder = new Geocoder();
    
    public CoordinateResource() {
        counter = new AtomicLong();
    }
    
    @GET
    @Timed
    public Coordinate getCoordinate(@QueryParam("address") String address) {
        if (address == null) {
            throw new WebApplicationException(
                    Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                      .entity("address parameter is mandatory")
                      .build()
                  );
        }
        try {
            GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress("Paris, France").setLanguage("en").getGeocoderRequest();
            GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
        }
        catch (Exception e) {
            
            e.printStackTrace();
            throw new WebApplicationException(
                    Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
                      .entity("error processing address")
                      .build()
                  );
        }
        // TODO: convert address to coordinate
        return new Coordinate(counter.incrementAndGet(), 0.0, 0.0);
    }

}
