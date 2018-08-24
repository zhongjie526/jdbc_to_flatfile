package com.uob.meniga.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;
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
	
	 @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o == null || getClass() != o.getClass()) return false;
	        BatchJobConfig that = (BatchJobConfig) o;
	        return Objects.equals(outputFolder, that.outputFolder) &&
	                Objects.equals(outputFileName, that.outputFileName) &&
	                Objects.equals(query, that.query) &&
	                Objects.equals(sourceSystemCode, that.sourceSystemCode) &&
	                Objects.equals(countryCode, that.countryCode) &&
	                Objects.equals(delimiter, that.delimiter) &&
	                Objects.equals(hashSumCol, that.hashSumCol);
	    }

	    @Override
	    public int hashCode() {

	        return Objects.hash(outputFolder, outputFileName, query, sourceSystemCode, countryCode, delimiter, hashSumCol);
	    }

	    @Override
	    public String toString() {
	        return "BatchJobConfig{" +
	                "outputFolder='" + outputFolder + '\'' +
	                ", outputFileName='" + outputFileName + '\'' +
	                ", query='" + query + '\'' +
	                ", sourceSystemCode='" + sourceSystemCode + '\'' +
	                ", countryCode='" + countryCode + '\'' +
	                ", delimiter='" + delimiter + '\'' +
	                ", hashSumCol='" + hashSumCol + '\'' +
	                '}';
	    }
    
    

}
