package com.uob.dge.cpmbatch.models;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "batch")
public class BatchJobConfiguration {

    @Value("${batch.name}")
    private String name;

    @Value("${batch.processor}")
    private String processor;

    @Value("${batch.fileName:}")
    private String fileName;

    @Value("${batch.tableName:}")
    private String tableName;

    @Value("${batch.pushField:}")
    private String pushField;

    @Value("${batch.hashField:}")
    private String hashField;


    @Value("${batch.pushCif:}")
    private String pushCif;

    @Value("${batch.updateInstructionsAfterPush:}")
    private String updateInstructionsAfterPush;

    @Value("${batch.outputFolder}")
    private String outputFolder;

    @Value("${batch.inputFolder}")
    private String inputFolder;

    @Value("${batch.country}")
    private String country;

    @Value("${batch.systemCode}")
    private String systemCode;

    @Value("${batch.pushApplicationCode}")
    private String pushApplicationCode;

    @Value("${batch.pushApplicationSubCode}")
    private String pushApplicationSubCode;

    @Value("${batch.pushServiceUrl}")
    private String pushServiceUrl;

    @Value("${batch.pushServiceVersion}")
    private String pushServiceVersion;


    private List<String> queries;
    private List<String> keys;

    private List<InputOutputColumn> headers;
    private List<InputOutputColumn> bodies;
    private List<InputOutputColumn> footers;

    public List<String> getQueries() {
        return queries;
    }

    public void setQueries(List<String> queries) {
        this.queries = queries;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getInputFolder() {
        return inputFolder;
    }

    public void setInputFolder(String inputFolder) {
        this.inputFolder = inputFolder;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public List<InputOutputColumn> getHeaders() {
        return headers;
    }

    public void setHeaders(List<InputOutputColumn> headers) {
        this.headers = headers;
    }

    public List<InputOutputColumn> getBodies() {
        return bodies;
    }

    public void setBodies(List<InputOutputColumn> bodies) {
        this.bodies = bodies;
    }

    public List<InputOutputColumn> getFooters() {
        return footers;
    }

    public void setFooters(List<InputOutputColumn> footers) {
        this.footers = footers;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public String getPushField() {
        return pushField;
    }

    public void setPushField(String pushField) {
        this.pushField = pushField;
    }

    public String getUpdateInstructionsAfterPush() {
        return updateInstructionsAfterPush;
    }

    public void setUpdateInstructionsAfterPush(String updateInstructionsAfterPush) {
        this.updateInstructionsAfterPush = updateInstructionsAfterPush;
    }

    public String getPushCif() {
        return pushCif;
    }

    public void setPushCif(String pushCif) {
        this.pushCif = pushCif;
    }

    public String getPushApplicationSubCode() {
        return pushApplicationSubCode;
    }

    public void setPushApplicationSubCode(String pushApplicationSubCode) {
        this.pushApplicationSubCode = pushApplicationSubCode;
    }

    public String getPushServiceUrl() {
        return pushServiceUrl;
    }

    public void setPushServiceUrl(String pushServiceUrl) {
        this.pushServiceUrl = pushServiceUrl;
    }

    public String getPushServiceVersion() {
        return pushServiceVersion;
    }

    public void setPushServiceVersion(String pushServiceVersion) {
        this.pushServiceVersion = pushServiceVersion;
    }

    public String getPushApplicationCode() {
        return pushApplicationCode;
    }

    public void setPushApplicationCode(String pushApplicationCode) {
        this.pushApplicationCode = pushApplicationCode;
    }

    public String getHashField() {
        return hashField;
    }

    public void setHashField(String hashField) {
        this.hashField = hashField;
    }
}
