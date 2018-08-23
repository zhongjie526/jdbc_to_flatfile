package com.uob.dge.cpmbatch.processors;

import com.uob.dge.cpmbatch.models.InputOutputColumn;
import oracle.sql.TIMESTAMP;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "batch.processor", havingValue = "ExportProcessor")
public class ExportProcessor extends BatchProcessorBase {
    private static final Logger logger = LoggerFactory.getLogger(ExportProcessor.class);

    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat formatFull = new SimpleDateFormat("yyyyMMddHHmmss");
    private static char fieldDelimiter = 0x07;

    private static String padField(String pFieldValue, boolean isLast) {
        String padFieldValue = pFieldValue;

        padFieldValue = getValidString(padFieldValue);
        String trimedValue = padFieldValue.trim();


        return isLast ? trimedValue : trimedValue + fieldDelimiter;
    }

    private static String getValidString(String sParamString) {
        if (sParamString == null
                || sParamString.trim().equalsIgnoreCase("null"))
            return "";
        else
            return sParamString.trim();
    }

    @Override
    public int processImpl() throws IOException {
        int returnCode = 0;
        logger.info("Starting Export");
        if (configuration.getQueries() == null || configuration.getQueries().isEmpty()) {
            logger.error("No queries detected");
            returnCode = 1;
        } else if (configuration.getFileName() == null || StringUtils.isEmpty(configuration.getFileName())) {
            logger.error("No Filename defined");
            returnCode = 1;
        } else if (configuration.getHeaders() == null || configuration.getHeaders().isEmpty()) {
            logger.error("No File structure - headers defined");
            returnCode = 1;
        } else if (configuration.getBodies() == null || configuration.getBodies().isEmpty()) {
            logger.error("No File structure - bodies defined");
            returnCode = 1;
        } else if (configuration.getFooters() == null || configuration.getFooters().isEmpty()) {
            logger.error("No File structure - footers defined");
            returnCode = 1;
        } else {
            String query = configuration.getQueries().get(0);
            processQuery(query);
        }
        logger.info("Ending Export");
        return returnCode;
    }

    private void processQuery(String query) throws IOException {
        File folder = new File(configuration.getOutputFolder());
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File filePath = new File(folder.getAbsolutePath(), configuration.getFileName());
        final int[] recordCount = {0};
        final double[] hashField = {0};
        try (BufferedWriter output = new BufferedWriter(new FileWriter(filePath))) {
            logger.info("Starting Export on - {}", query);
            prepareHeader(output);
            parameterJdbcTemplate.query(query, new HashMap(), new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet resultSet) throws SQLException {
                    try {
                        hashField[0] += writeRow(output, convertToMap(resultSet));
                    } catch (IOException e) {
                        throw new SQLException(e.getMessage());
                    }
                    recordCount[0]++;
                }
            });
            prepareFooter(output, recordCount[0], hashField[0]);
            logger.info("Ending Export - {}:{}", recordCount[0], query);
        }
    }

    private double writeRow(BufferedWriter output, Map row) throws IOException {
        int count = 0;
        double hashField = 0;
        StringBuilder strBuilder = new StringBuilder();
        for (InputOutputColumn column : configuration.getBodies()) {
            count++;
            if (column.getFieldName().equalsIgnoreCase("recordType")) {
                strBuilder.append(padField("D", count >= configuration.getBodies().size()));
            } else if (column.getFieldName().equalsIgnoreCase("filler")) {
                strBuilder.append(padField("", count >= configuration.getBodies().size()));
            } else {
                if (row.containsKey(column.getFieldName())) {
                    if (column.getFieldName().equalsIgnoreCase(configuration.getHashField())){
                        hashField = Double.parseDouble(convert(row.get(column.getFieldName()), column.getDatatype()));
                    }
                    strBuilder.append(padField(convert(row.get(column.getFieldName()), column.getDatatype()), count >= configuration.getBodies().size()));
                }
            }
        }
        strBuilder.append(System.getProperty("line.separator"));
        output.write(strBuilder.toString());
        output.flush();
        return hashField;
    }

    private String convert(Object value, String datatype) {
        if (datatype.equalsIgnoreCase("String")) {
            if (value != null && value instanceof TIMESTAMP) {
                try {
                    return format.format(((TIMESTAMP)value).dateValue());
                } catch (SQLException e) {
                    logger.warn("Unable to get date value from timestamp", e);
                    return "";
                }
            } else {
                return String.format("%s", value);
            }
        } else if (datatype.equalsIgnoreCase("Integer")) {
            if (value instanceof BigDecimal) {
                return String.format("%d", ((BigDecimal) value).intValue());
            }else if (value instanceof Double) {
                return String.format("%d", ((Double) value).intValue());
            } else {
                return String.format("%d", (int)value);
            }
        } else if (datatype.equalsIgnoreCase("Long")) {
            if (value instanceof BigDecimal) {
                return String.format("%ld", ((BigDecimal) value).longValue());
            } else {
                return String.format("%ld",  (long)value);
            }
        } else if (datatype.equalsIgnoreCase("Double")) {
            if (value instanceof BigDecimal) {
                return String.format("%.5f", ((BigDecimal) value).doubleValue());
            } else {
                return String.format("%.5f", (double) value);
            }
        }
        return null;
    }

    private void prepareFooter(BufferedWriter output, int recordCount, double hashField) throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        int count = 0;
        for (InputOutputColumn column : configuration.getFooters()) {
            count++;
            if (column.getFieldName().equalsIgnoreCase("recordType")) {
                strBuilder.append(padField("T", count >= configuration.getFooters().size()));
            } else if (column.getFieldName().equalsIgnoreCase("recordCount")) {
                strBuilder.append(padField(Integer.toString(recordCount), count >= configuration.getFooters().size()));
            } else if (column.getFieldName().equalsIgnoreCase("hashField")) {
                strBuilder.append(padField(convert(hashField, column.getDatatype()), count >= configuration.getFooters().size()));
            }
        }
        strBuilder.append(System.getProperty("line.separator"));
        output.write(strBuilder.toString());
        output.flush();
    }

    private void prepareHeader(BufferedWriter output) throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        int count = 0;
        for (InputOutputColumn column : configuration.getHeaders()) {
            count++;
            if (column.getFieldName().equalsIgnoreCase("recordType")) {
                strBuilder.append(padField("H", count >= configuration.getHeaders().size()));
            } else if (column.getFieldName().equalsIgnoreCase("sourceSystemCode")) {
                strBuilder.append(padField(configuration.getSystemCode(), count >= configuration.getHeaders().size()));
            } else if (column.getFieldName().equalsIgnoreCase("countryCode")) {
                strBuilder.append(padField(configuration.getCountry(), count >= configuration.getHeaders().size()));
            } else if (column.getFieldName().equalsIgnoreCase("processingDate")) {
                strBuilder.append(padField(format.format(new Date()), count >= configuration.getHeaders().size()));
            } else if (column.getFieldName().equalsIgnoreCase("processingDateFull")) {
                strBuilder.append(padField(formatFull.format(new Date()), count >= configuration.getHeaders().size()));
            }
        }
        strBuilder.append(System.getProperty("line.separator"));
        output.write(strBuilder.toString());
        output.flush();

    }
}
