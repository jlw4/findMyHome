package com.hardin.wilson.app;

import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import com.hardin.wilson.jobs.GreatSchoolsJob;
import com.hardin.wilson.jobs.ProcessingJob;

public class FetchCommand extends Command {
	
	List<ProcessingJob> jobs;

    protected FetchCommand(String name, String description) {
        super(name, description);
        
		jobs = new ArrayList<ProcessingJob>();
		jobs.add(new GreatSchoolsJob());
    }

    @Override
    public void configure(Subparser arg0) {}

    @Override
    public void run(Bootstrap<?> arg0, Namespace arg1) throws Exception {
        
        // Run all jobs.
		for (ProcessingJob job : jobs) {
			job.run();
		}
    }

}
