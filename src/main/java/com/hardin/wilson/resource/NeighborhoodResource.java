package com.hardin.wilson.resource;

import java.net.HttpURLConnection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;
import com.hardin.wilson.app.NeighborhoodContainer;
import com.hardin.wilson.pojo.Neighborhood;

/**
 * Finds a neighborhood by name
 */
@Path("/neighborhood")
@Produces(MediaType.APPLICATION_JSON)
public class NeighborhoodResource {
    
    @GET
    @Timed
    public Neighborhood getNeighborhood(@QueryParam("name") String name) {
        Neighborhood n = NeighborhoodContainer.getContainer().getNeighborhood(name);
		if (n != null) {
			return n;
		} else {
			throw new WebApplicationException(Response
					.status(HttpURLConnection.HTTP_BAD_REQUEST)
					.entity("neighborhood " + name + " not found").build());
		}
    }

}
