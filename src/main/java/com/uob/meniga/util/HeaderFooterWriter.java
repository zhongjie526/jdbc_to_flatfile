package com.uob.meniga.util;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.uob.meniga.model.Data;



@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "batch")
public class HeaderFooterWriter implements FlatFileHeaderCallback, FlatFileFooterCallback,ChunkListener,ItemProcessor<Data, String>{

	private int rowCount=0;
	private String businessDate;
	private String systemDateTime;
	private DateFormat dtForBusiness=new SimpleDateFormat("yyyyMMdd");
	private DateFormat dtForSystem=new SimpleDateFormat("yyyyMMddHHmmss");
	BigDecimal hashsum = new BigDecimal(0);
	
    @Value("${batch.delimiter}")
    private String delimiter;
    @Value("${batch.sourceSystemCode}")
    private String sourceSystemCode;
    @Value("${batch.countryCode}")
    private String countryCode;
	

	public void writeHeader(Writer writer) throws IOException {
		Date date = new Date();
		businessDate = dtForBusiness.format(date);
		systemDateTime = dtForSystem.format(date);
		writer.write("H"+delimiter+sourceSystemCode+delimiter+countryCode+delimiter+businessDate+delimiter+systemDateTime);
		
    }
	
	public void writeFooter(Writer writer) throws IOException {
		writer.write("T"+delimiter+rowCount+delimiter+hashsum);

    }
	
	public String getSourceSystemCode() {
		return sourceSystemCode;
	}

	public void setSourceSystemCode(String sourceSystemCode) {
		this.sourceSystemCode = sourceSystemCode;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@Override
	public void afterChunk(ChunkContext arg0) {
		rowCount += arg0.getStepContext().getStepExecution().getWriteCount();
	}
	
	@Override
	public void afterChunkError(ChunkContext arg0) {
		
	}
	
	@Override
	public void beforeChunk(ChunkContext arg0) {
	}
	
	@Override
	public String process(Data data) throws Exception {

		if(data.getHashSum()!=null) {
			hashsum = hashsum.add(new BigDecimal(data.getHashSum()));
		}
		return data.getOutputValue();
		
	}

}
