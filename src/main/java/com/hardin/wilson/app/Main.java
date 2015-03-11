package com.hardin.wilson.app;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import com.hardin.wilson.resource.CoordToNeighborhoodResource;
import com.hardin.wilson.resource.CoordinateResource;
import com.hardin.wilson.resource.KmlResource;
import com.hardin.wilson.resource.NeighborhoodNamesResource;
import com.hardin.wilson.resource.NeighborhoodResource;
import com.hardin.wilson.resource.RatingResource;
import com.hardin.wilson.resource.SmallKmlResource;

/**
 * Main class, runs server
 */
public class Main extends Application<HomeConfiguration> {
    
    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

    @Override
    public String getName() {
        return "FindMyHome Server";
    }

    @Override
    public void initialize(Bootstrap<HomeConfiguration> bootstrap) {
        bootstrap.addCommand(new FetchCommand("fetch", "run all fetch commands"));
        bootstrap.addCommand(new FetchCommand("desc", "fetch neighborhood descriptions"));
    }

    @Override
    public void run(HomeConfiguration configuration, Environment environment) {
        // initialize neighborhood data
        NeighborhoodContainer.init();
        
        // setup resources
        final HomeHealthCheck healthCheck = new HomeHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(new CoordinateResource());
        environment.jersey().register(new NeighborhoodNamesResource());
        environment.jersey().register(new NeighborhoodResource());
        environment.jersey().register(new KmlResource());
        environment.jersey().register(new SmallKmlResource());
        environment.jersey().register(new CoordToNeighborhoodResource());
        environment.jersey().register(new RatingResource());
        environment.servlets().addFilter("CorsFilter", new CorsFilter())
        		.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }

}
