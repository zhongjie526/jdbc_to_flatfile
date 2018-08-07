package com.uob.meniga.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class CommonUtil {
	
	public static String removeNull(ResultSet rs) throws SQLException {
		String strVal = "";
		String outputVal = "";
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int columns = rsmd.getColumnCount();

			for (int i = 1; i < columns+1; i++) {
				strVal = rs.getString(i);
				if (rs.wasNull()) {
					outputVal = outputVal + "" + ",";;
				}
				else {
					outputVal = outputVal + strVal + ",";;
				}
			}	
			
			outputVal = outputVal.substring(0, outputVal.length() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outputVal;
	}

}
