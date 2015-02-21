package com.hardin.wilson.resource;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.hardin.wilson.app.NeighborhoodContainer;

/**
 * Simple resource for returning a list of neighborhood names
 */
@Path("/names")
@Produces(MediaType.APPLICATION_JSON)
public class NeighborhoodNamesResource {
    
    @GET
    @Timed
    public Set<String> getNames() {
        return NeighborhoodContainer.getContainer().getNames();
    }

}
