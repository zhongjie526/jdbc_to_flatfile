package com.uob.meniga;

import org.springframework.batch.item.ItemProcessor;

import com.uob.meniga.model.Data;

public class DataItemProcessor implements ItemProcessor<Data, String> {
	
	@Override
	 public String process(Data user) throws Exception {
	  return user.getOutputValue();
	 }

}
