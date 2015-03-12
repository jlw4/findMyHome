package com.hardin.wilson.resource;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.hardin.wilson.app.NeighborhoodContainer;
import com.hardin.wilson.pojo.Neighborhood;

@Path("/container")
@Produces(MediaType.APPLICATION_JSON)
public class ContainerResource {
    
    @Timed
    @GET
    public Map<String, Neighborhood> getNeighborhoods() {
        return NeighborhoodContainer.getContainer().getMap();
    }

}
