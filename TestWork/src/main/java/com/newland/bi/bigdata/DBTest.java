package com.newland.bi.bigdata;

import java.io.File;
//import java.sql.SQLException;
//import java.sql.Statement;

public class DBTest {
	public static void main(String[] args) {
//		Statement st = null;
//		try {
//			st.setFetchSize(100);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		String CONF_FILE_NAME = "d:/axax.l";
		if(!new File(CONF_FILE_NAME).exists()){
			System.out.println("[CONF_FILE_NAME]"+CONF_FILE_NAME+" is not a file, exception exit.");
			System.exit(0);
		}
	}
}
