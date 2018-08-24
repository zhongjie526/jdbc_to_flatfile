package com.uob.meniga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.batch.JobLauncherCommandLineRunner;

import com.uob.meniga.model.BatchJobConfig;

@SpringBootApplication
public class MenigaExtractorApplication extends JobLauncherCommandLineRunner{
	private static final Logger logger = LoggerFactory.getLogger(MenigaExtractorApplication.class);
	
	@Autowired
    private BatchJobConfig configuration;

    public MenigaExtractorApplication(JobLauncher jobLauncher, JobExplorer jobExplorer) {
        super(jobLauncher, jobExplorer);
    }
	
	public static void main(String[] args) {
		
		
		SpringApplicationBuilder app = new SpringApplicationBuilder(MenigaExtractorApplication.class).web(WebApplicationType.NONE);
		app.build().addListeners(new ApplicationPidFileWriter(String.format("./pids/%s.pid", System.getProperty("spring.profiles.active"))));
		app.run(args);
		
        //System.exit(SpringApplication.exit(app.context()));
		
        int exitValue = SpringApplication.exit(app.context());
        if (args == null || args.length != 1 || !"Testing".equals(args[0])) {
            System.exit(exitValue);
        }       
        
	}
	
    @Override
    public void run(String... args) throws JobExecutionException{
    	logger.info("Starting up {}", configuration);
        
        if (args == null || args.length != 1 || !"Testing".equals(args[0])) {
        	logger.info("Start running app ");
        	super.run(args);
        }
    }
}
