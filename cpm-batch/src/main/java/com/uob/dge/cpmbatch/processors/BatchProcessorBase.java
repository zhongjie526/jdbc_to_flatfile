package com.uob.dge.cpmbatch.processors;

import com.uob.dge.cpmbatch.models.BatchJobConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public abstract class BatchProcessorBase implements ExitCodeGenerator {
    private static final Logger logger = LoggerFactory.getLogger(BatchProcessorBase.class);

    protected int exitCode = 0;

    @Autowired
    protected BatchJobConfiguration configuration;

    @Autowired
    protected NamedParameterJdbcTemplate parameterJdbcTemplate;

    public abstract int processImpl() throws IOException, ParseException;

    public void process() {
        try {
            exitCode = processImpl();
        } catch (Exception ex) {
            logger.error("Unable to perform batch {} ", configuration.getName());
            logger.error("Stack", ex);
            exitCode = 1;
        } finally {
            logger.info("Batch {} exiting with exit code : {}", configuration.getName(), exitCode);
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }


    protected Map convertToMap(ResultSet resultSet) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        ResultSetMetaData md = resultSet.getMetaData();
        int columns = md.getColumnCount();
        for (int i = 1; i <= columns; i++) {
            result.put(md.getColumnName(i), resultSet.getObject(i));
        }
        return result;
    }

    protected boolean addIntoBuilder(StringBuilder builder, boolean first, String separator, String format, String fieldName, Map<String, Object> params, Object data) {
        if (!first) {
            builder.append(separator);
        } else {
            first = false;
        }
        builder.append(String.format(format, fieldName, fieldName));
        if (params != null) {
            params.put(fieldName, data);
        }
        return first;
    }

}
