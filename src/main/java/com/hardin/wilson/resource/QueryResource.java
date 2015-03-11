package com.hardin.wilson.resource;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

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
import com.hardin.wilson.pojo.Rating;

@Path("/query")
@Produces(MediaType.APPLICATION_JSON)
public class QueryResource {
    
    @GET
    @Timed
    public List<Neighborhood> getNeighborhood(@QueryParam("ratings") List<String> ratings) {
        for (String rating : ratings) {
            if (!Rating.getValues().contains(rating)) {
                throw new WebApplicationException(Response
                        .status(HttpURLConnection.HTTP_BAD_REQUEST)
                        .entity("invalid rating: " + rating).build());
            }
        }
        return NeighborhoodContainer.getContainer().getSortedList(ratings);
    }

}
