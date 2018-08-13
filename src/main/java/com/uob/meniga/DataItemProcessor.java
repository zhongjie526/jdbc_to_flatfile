package com.uob.meniga;

import org.springframework.batch.item.ItemProcessor;

import com.uob.meniga.model.Data;

public class DataItemProcessor implements ItemProcessor<Data, String> {
	
	@Override
	 public String process(Data data) throws Exception {
	  return data.getOutputValue();
	 }

}
