package com.uob.dge.cpmbatch;

import com.uob.dge.cpmbatch.models.BatchJobConfiguration;
import com.uob.dge.cpmbatch.processors.CleanupProcessor;
import com.uob.dge.cpmbatch.processors.ExportProcessor;
import com.uob.dge.cpmbatch.processors.ImportProcessor;
import com.uob.dge.cpmbatch.processors.NotificationProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackageClasses = {CleanupProcessor.class, ImportProcessor.class, ExportProcessor.class, NotificationProcessor.class, BatchJobConfiguration.class})
@SpringBootApplication
public class CpmBatchTestConfiguration {
    public static void main(String[] args) {
        SpringApplication.run(CpmBatchTestConfiguration.class, args);
    }
}
