package com.uob.meniga;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;

import com.uob.meniga.model.BatchJobConfig;
import com.uob.meniga.model.Data;
import com.uob.meniga.util.CommonUtil;
import com.uob.meniga.util.HeaderFooterWriter;

@Configuration
@EnableBatchProcessing
@Scope(value = "prototype")
public class BatchConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(BatchConfiguration.class);

	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	public DataSource dataSource;
	
    @Autowired
    BatchJobConfig configuration;
	
	
	public class DataRowMapper implements RowMapper<Data>{
		@Override
		public Data mapRow(ResultSet rs, int rowNum) throws SQLException{
			logger.info("processing data");
			if (rowNum == 1){
				hfwriter.reset();
			}
			Data data = new Data();
			String dataOutput = CommonUtil.removeNull(rs,configuration.getDelimiter());
			data.setOutputValue(dataOutput);
			String hashSum = CommonUtil.getField(rs, configuration.getHashSumCol());
			data.setHashSum(hashSum);
			return data;
		}
	}
	
	public JdbcCursorItemReader<Data> dataReader(){
		logger.info("reading data");
		JdbcCursorItemReader<Data> reader = new JdbcCursorItemReader<Data>();
		reader.setDataSource(dataSource);
		reader.setSql(configuration.getQuery());
		reader.setRowMapper(new DataRowMapper());
		return reader;
	}
	
    
    @Autowired
    HeaderFooterWriter hfwriter;
    

	public FlatFileItemWriter<String> dataWriter(HeaderFooterWriter hfwriter){
		logger.info("writing data");
		FlatFileItemWriter<String> writer = new FlatFileItemWriter<String>();
		
		writer.setHeaderCallback(hfwriter);
		writer.setFooterCallback(hfwriter);
		writer.setResource(new FileSystemResource(new File(configuration.getOutputFolder()+configuration.getOutputFileName())));
		writer.setLineAggregator(new PassThroughLineAggregator<String>());
		
		return writer;
	}
	
	
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Data, String> chunk(10000)
				.reader(dataReader())
				.processor(hfwriter)
				.writer(dataWriter(hfwriter))
				.listener(hfwriter)
				.build()
				;
	}
	
	
	@Bean
	public Job exportUserJob() {
		return jobBuilderFactory.get("JDBCextractorJob")
				.incrementer(new RunIdIncrementer())
				.flow(step1())
				.end()
				.build();
	}
}
