package com.uob.dge.cpmbatch.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@ConditionalOnProperty(name = "batch.processor", havingValue = "CleanupProcessor")
public class CleanupProcessor extends BatchProcessorBase {
    private static final Logger logger = LoggerFactory.getLogger(CleanupProcessor.class);

    @Override
    public int processImpl() {
        int returnCode = 0;
        logger.info("Starting House Keeping");
        if (configuration.getQueries() != null && !configuration.getQueries().isEmpty()) {
            for (String query : configuration.getQueries()) {
                processQuery(query);
            }
        } else {
            logger.error("No queries detected");
            returnCode = 1;
        }
        logger.info("Ending House Keeping");
        return returnCode;
    }

    private void processQuery(String query) {
        logger.info("Starting House Keeping on - {}", query);
        int rows = parameterJdbcTemplate.update(query, new HashMap());
        logger.info("Ending House Keeping - {}:{}", rows, query);
    }
}
