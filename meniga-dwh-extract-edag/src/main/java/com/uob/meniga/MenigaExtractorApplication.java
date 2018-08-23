package com.uob.meniga;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class MenigaExtractorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MenigaExtractorApplication.class, args);
	}
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
}
