package com.uob.meniga.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.uob.meniga.model.BatchJobConfig;

public class CommonUtil {
	
	public static String getField(ResultSet rs, String fieldName) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columns = rsmd.getColumnCount();
		
		for (int i = 1; i < columns+1; i++) {
			if (rsmd.getColumnName(i).equalsIgnoreCase(fieldName)) return rs.getString(i);
		}
		
		return "";
	}

	
	public static String removeNull(ResultSet rs,String delimiter) throws SQLException {
		String strVal = "";
		String outputVal = "";
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int columns = rsmd.getColumnCount();

			for (int i = 1; i < columns+1; i++) {
				strVal = rs.getString(i);
				
				if(i==1) {
					if (rs.wasNull()) {
						outputVal = outputVal  + "D" +delimiter;;
					}
					else {
						outputVal = outputVal  + "D" +delimiter+strVal;;
					}
				}
				
				else {
					if (rs.wasNull()) {
						outputVal = outputVal  + delimiter+ "";;
					}
					else {
						outputVal = outputVal  + delimiter+ strVal;;
					}
				}
			}	
			
			//outputVal = outputVal.substring(0, outputVal.length() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outputVal;
	}

}
