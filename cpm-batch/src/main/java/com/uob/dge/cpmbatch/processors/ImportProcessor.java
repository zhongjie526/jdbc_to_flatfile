package com.uob.dge.cpmbatch.processors;

import com.uob.dge.cpmbatch.models.InputOutputColumn;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "batch.processor", havingValue = "ImportProcessor")
public class ImportProcessor extends BatchProcessorBase {
    private static final Logger logger = LoggerFactory.getLogger(ImportProcessor.class);

    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    @Override
    public int processImpl() throws IOException, ParseException {
        int returnCode = 0;
        logger.info("Starting Import");
        if (configuration.getTableName() == null || StringUtils.isEmpty(configuration.getTableName())) {
            logger.error("No table defined");
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
        } else if (configuration.getKeys() == null || configuration.getKeys().isEmpty()) {
            logger.error("No File structure - keys defined");
            returnCode = 1;
        } else {
            File folder = new File(configuration.getInputFolder());
            File filePath = new File(folder.getAbsolutePath(), configuration.getFileName());
            if (filePath.exists()) {
                processFile(filePath);
            } else {
                logger.error("Input File {}, not available", filePath);
                returnCode = 1;
            }
        }
        logger.info("Ending Import");
        return returnCode;
    }

    private void processFile(File filePath) throws IOException, ParseException {
        boolean header = false;
        int recordCount = 0;
        try (BufferedReader output = new BufferedReader(new FileReader(filePath))) {
            logger.info("Starting Import on - {}", filePath);
            String line;
            while ((line = output.readLine()) != null) {
                Map<String, String> lineMap = convertToMap(line);
                if (lineMap.containsKey("recordType") && lineMap.get("recordType").equals("H")) {
                    header = true;
                    checkHeader(lineMap);
                } else if (lineMap.containsKey("recordType") && lineMap.get("recordType").equals("T")) {
                    if (header) {
                        checkFooter(lineMap, recordCount);
                    } else {
                        throw new IOException("Invalid Format - missing header");
                    }
                } else if (lineMap.containsKey("recordType") && lineMap.get("recordType").equals("D")) {
                    if (header) {
                        processRecord(lineMap);
                        recordCount++;
                    } else {
                        throw new IOException("Invalid Format - missing header");
                    }
                }
            }
            logger.info("Ending Import on - {}:{}", recordCount, filePath);
        }
    }

    private void processRecord(Map<String, String> lineMap) throws ParseException {
        Map<String, Object> insertParams = new HashMap<>();
        Map<String, Object> updateParams = new HashMap<>();
        Map<String, Object> queryParams = new HashMap<>();
        String insertBuilder = prepareInsertStatement(lineMap, insertParams);
        String updateBuilder = prepareUpdateStatement(lineMap, updateParams);
        String queryBuilder = prepareQueryStatement(lineMap, queryParams);
        logger.info(queryBuilder);
        int rowCount = parameterJdbcTemplate.queryForObject(queryBuilder, queryParams, Integer.class);
        if (rowCount > 0) {
            logger.info(updateBuilder);
            parameterJdbcTemplate.update(updateBuilder, updateParams);
        } else {
            logger.info(insertBuilder);
            parameterJdbcTemplate.update(insertBuilder, insertParams);
        }
    }

    private String prepareUpdateStatement(Map<String, String> lineMap, Map<String, Object> params) throws ParseException {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("UPDATE %s SET  ", configuration.getTableName()));
        boolean first = true;
        for (InputOutputColumn column : configuration.getBodies()) {
            if (column.getFieldName().equalsIgnoreCase("recordType") || configuration.getKeys().contains(column.getFieldName()) || !StringUtils.isEmpty(column.getSeqNo())) {
                continue;
            }
            first = addIntoBuilder(builder, first, " , ", " %s=:%s ", column.getFieldName(), params, getData(lineMap, column));
        }
        builder.append(" WHERE ");
        first = true;
        for (String key : configuration.getKeys()) {
            first = addIntoBuilder(builder, first, " AND ", " %s=:%s ", key, params, lineMap.get(key));
        }
        return builder.toString();
    }

    private String prepareQueryStatement(Map<String, String> lineMap, Map<String, Object> params) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("SELECT count(*) FROM %s WHERE  ", configuration.getTableName()));
        boolean first = true;
        for (String key : configuration.getKeys()) {
            first = addIntoBuilder(builder, first, " AND ", " %s=:%s ", key, params, lineMap.get(key));
        }
        return builder.toString();
    }

    private String prepareInsertStatement(Map<String, String> lineMap, Map<String, Object> params) throws ParseException {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("INSERT INTO %s ( ", configuration.getTableName()));
        boolean first = true;
        for (InputOutputColumn column : configuration.getBodies()) {
            if (column.getFieldName().equalsIgnoreCase("recordType")) {
                continue;
            }
            first = addIntoBuilder(builder, first, " , ", " %s ", column.getFieldName(), null, null);
        }
        builder.append(" ) values (");
        first = true;
        for (InputOutputColumn column : configuration.getBodies()) {
            if (column.getFieldName().equalsIgnoreCase("recordType")) {
                continue;
            }
            if (StringUtils.isEmpty(column.getSeqNo())) {
                first = addIntoBuilder(builder, first, " , ", " :%s ", column.getFieldName(), params, getData(lineMap, column));
            } else {
                first = addIntoBuilder(builder, first, " , ", " %s ", column.getSeqNo(), null, null);
            }
        }
        builder.append(" )");
        return builder.toString();
    }

    private Object getData(Map<String, String> lineMap, InputOutputColumn column) throws ParseException {
        String value = lineMap.get(column.getFieldName());
        if (StringUtils.isEmpty(value)) {
            return null;
        } else {
            if (column.getDatatype().equalsIgnoreCase("String")) {
                return value;
            } else if (column.getDatatype().equalsIgnoreCase("Integer")) {
                return Integer.parseInt(value);
            } else if (column.getDatatype().equalsIgnoreCase("Double")) {
                return Double.parseDouble(value);
            } else if (column.getDatatype().equalsIgnoreCase("Long")) {
                return Long.parseLong(value);
            } else if (column.getDatatype().equalsIgnoreCase("Date")) {
                return format.parse(value);
            } else {
                return value;
            }
        }
    }

    private void checkFooter(Map<String, String> line, int recordCount) throws IOException {
        if (!line.containsKey("recordCount") || !line.get("recordCount").equals(Integer.toString(recordCount))) {
            throw new IOException(String.format("Invalid format - wrong count - %s", line.get("recordCount")));
        }
    }

    private Map<String, String> convertToMap(String line) throws IOException {
        if (line.startsWith("H")) {
            return convertToMap(line, configuration.getHeaders());
        } else if (line.startsWith("T")) {
            return convertToMap(line, configuration.getFooters());
        } else if (line.startsWith("D")) {
            return convertToMap(line, configuration.getBodies());
        } else {
            throw new IOException(String.format("Invalid format - unknown recordType - %s", line));
        }
    }

    private Map<String, String> convertToMap(String line, List<InputOutputColumn> columns) throws IOException {
        Map<String, String> result = new LinkedHashMap<>();
        try {
            int begin = 0;
            for (InputOutputColumn column : columns) {
                if (!StringUtils.isEmpty(column.getSeqNo())) {
                    result.put(column.getFieldName(), column.getSeqNo());
                } else if (begin >= line.length() && column.getFieldName().equalsIgnoreCase("filler")) {
                    result.put(column.getFieldName(), "");
                } else {
                    String item = line.substring(begin, column.getLength() + begin);
                    begin = begin + column.getLength();
                    result.put(column.getFieldName(), item.trim());
                }
            }
        } catch (Exception ex) {
            throw new IOException(String.format("Invalid format  - %s", line));
        }
        return result;
    }

    private void checkHeader(Map<String, String> line) throws IOException {
        if (!line.containsKey("sourceSystemCode") || !line.get("sourceSystemCode").equals(configuration.getSystemCode())) {
            throw new IOException(String.format("Invalid format - wrong system Code - %s", line.get("sourceSystemCode")));
        }
        if (!line.containsKey("countryCode") || !line.get("countryCode").equals(configuration.getCountry())) {
            throw new IOException(String.format("Invalid format - wrong country - %s", line.get("countryCode")));
        }
    }
}
