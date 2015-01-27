package com.hardin.wilson.app;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import com.hardin.wilson.resource.CoordinateResource;
import com.hardin.wilson.resource.HelloResource;

/**
 * Main class, runs server
 */
public class Main extends Application<HomeConfiguration> {
    
    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<HomeConfiguration> bootstrap) {
        // TODO: set stuff up
    }

    @Override
    public void run(HomeConfiguration configuration, Environment environment) {
        final HelloResource resource = new HelloResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
            );
        final HomeHealthCheck healthCheck =
                new HomeHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(new CoordinateResource());
        environment.jersey().register(resource);
    }

}
