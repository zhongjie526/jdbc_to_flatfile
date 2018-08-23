package com.uob.dge.cpmbatch;

import com.uob.dge.cpmbatch.processors.BatchProcessorBase;
import com.uob.dge.cpmbatch.utils.RequestResponseLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@SpringBootApplication
public class CpmBatch implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(CpmBatch.class);
    @Autowired
    protected BatchProcessorBase theProcessor;

    public static void main(String[] args) {
        SpringApplicationBuilder app = new SpringApplicationBuilder(CpmBatch.class).web(WebApplicationType.NONE);
        app.build().addListeners(new ApplicationPidFileWriter(String.format("./pids/%s.pid", System.getProperty("spring.profiles.active"))));
        app.run(args);

        int exitValue = SpringApplication.exit(app.context());
        if (args == null || args.length != 1 || !"Testing".equals(args[0])) {
            System.exit(exitValue);
        }
    }

    @Bean
    public RestTemplate restTemplate() {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.<ClientHttpRequestInterceptor>singletonList(new RequestResponseLoggingInterceptor()));
        return restTemplate;
    }

    @Override
    public void run(String... args) {
        logger.info("Starting up {}", theProcessor);
        if (args == null || args.length != 1 || !"Testing".equals(args[0])) {
            theProcessor.process();
        }
    }
}
