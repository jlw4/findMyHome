package com.hardin.wilson.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.codahale.metrics.annotation.Timed;
import com.hardin.wilson.app.NeighborhoodContainer;
import com.hardin.wilson.pojo.Coordinate;
import com.hardin.wilson.pojo.Neighborhood;

/**
 * Converts a latLong to a neighborhood
 */
@Path("coordToHood")
public class CoordToNeighborhoodResource {
    
    public CoordToNeighborhoodResource() {
        
    }
    
    @GET
    @Timed
    public Neighborhood getNeighborhood(@QueryParam("lat") double lat, @QueryParam("long") double longitude) {
        Coordinate coord = new Coordinate(longitude, lat);
        for (Neighborhood n : NeighborhoodContainer.getContainer().getNeighborhoods()) {
            if (n.isInsideBounds(coord))
                return n;
        }
        Neighborhood n = new Neighborhood();
        n.setName("none");
        return n;
    }
}
