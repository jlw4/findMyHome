package com.hardin.wilson.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.hardin.wilson.pojo.Rating;

/**
 * Simple resource for returning a list of ratings
 */
@Path("/ratings")
@Produces(MediaType.APPLICATION_JSON)
public class RatingResource {
    
    @GET
    @Timed
    public List<String> getRatings() {
        return Rating.getValues();
    }

}
