package com.hardin.wilson;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

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
        // TODO: do some stuff
    }

}
