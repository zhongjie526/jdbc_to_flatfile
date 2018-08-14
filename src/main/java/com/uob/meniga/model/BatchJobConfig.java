package com.uob.meniga.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "batch")
public class BatchJobConfig {
	
    @Value("${batch.outputFolder}")
    private String outputFolder;
    
    @Value("${batch.outputFileName}")
    private String outputFileName;
    
    @Value("${batch.query}")
    private String query;
    
    @Value("${batch.sourceSystemCode}")
    private String sourceSystemCode;
    
    @Value("${batch.countryCode}")
    private String countryCode;
    
    @Value("${batch.delimiter}")
    private String delimiter;
    
    @Value("${batch.hashSumCol}")
    private String hashSumCol;
    
    public String getHashSumCol() {
		return hashSumCol;
	}

	public void setHashSumCol(String hashSumCol) {
		this.hashSumCol = hashSumCol;
	}

	public String getSourceSystemCode() {
		return sourceSystemCode;
	}

	public void setSourceSystemCode(String sourceSystemCode) {
		this.sourceSystemCode = sourceSystemCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}
    
    

}
