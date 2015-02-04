package com.hardin.wilson.app;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;

public class FetchCommand extends Command {

    protected FetchCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public void configure(Subparser arg0) {}

    @Override
    public void run(Bootstrap<?> arg0, Namespace arg1) throws Exception {
        // TODO: fetch data
        System.out.println("fetching command!");
    }

}
