package com.uob.meniga;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;

import com.uob.meniga.model.BatchJobConfig;
import com.uob.meniga.model.Data;
import com.uob.meniga.util.CommonUtil;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	public DataSource dataSource;
	
    @Autowired
    protected BatchJobConfig configuration;
	
//	@Bean
//	public DataSource dataSource() {
//		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
////		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
////		dataSource.setUrl("jdbc:mysql://localhost:3306/meniga?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true");
////		dataSource.setUsername("root");
////		dataSource.setPassword("root");
////		dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
////		dataSource.setUrl("jdbc:sqlserver://localhost:1433;databaseName=meniga");
////		dataSource.setUsername("sa");
////		dataSource.setPassword("yourStrong(!)Password");
//		
//		return dataSource;
//	}
	
	
	public class DataRowMapper implements RowMapper<Data>{
		@Override
		public Data mapRow(ResultSet rs, int rowNum) throws SQLException{
			Data user = new Data();
			user.setOutputValue(CommonUtil.removeNull(rs));
			return user;
		}
	}
	
	public JdbcCursorItemReader<Data> reader(){
		JdbcCursorItemReader<Data> reader = new JdbcCursorItemReader<Data>();
		reader.setDataSource(dataSource);
		reader.setSql(configuration.getQuery());
		reader.setRowMapper(new DataRowMapper());
		return reader;
	}
	
    @Bean
    public ItemProcessor<Data, String> processor() {
        return new DataItemProcessor();
    }
	
	@Bean
	public FlatFileItemWriter<String> userWriter(){
		FlatFileItemWriter<String> writer = new FlatFileItemWriter<String>();
		writer.setResource(new FileSystemResource(new File(configuration.getOutputFolder()+configuration.getOutputFileName())));
		writer.setLineAggregator(new PassThroughLineAggregator<String>());
		
		return writer;
	}
	
	
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Data, String> chunk(10)
				.reader(reader())
				.processor(processor())
				.writer(userWriter())
				.build();
	}
	
	@Bean
	public Job exportUserJob() {
		return jobBuilderFactory.get("exportUserJob")
				.incrementer(new RunIdIncrementer())
				.flow(step1())
				.end()
				.build();
	}
}
