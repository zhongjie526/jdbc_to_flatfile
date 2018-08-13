package com.uob.meniga.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class DataTest {

	@Test
	public void testGetOutputValue() {
		Data data = new Data();
		data.setOutputValue("123abc");
		assertEquals("123abc",data.getOutputValue());

	}

	@Test
	public void testSetOutputValue() {
		Data data = new Data();
		data.setOutputValue("123abc");
		assertEquals("123abc",data.getOutputValue());
	}

}
