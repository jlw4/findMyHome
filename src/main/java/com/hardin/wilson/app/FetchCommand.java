package com.hardin.wilson.app;

import java.util.ArrayList;
import java.util.List;

import com.hardin.wilson.jobs.GreatSchoolsJob;
import com.hardin.wilson.jobs.ProcessingJob;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;

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
        System.out.println("fetching data!");
        
        // Run all jobs.
		for (ProcessingJob job : jobs) {
			job.run();
		}
    }

}
