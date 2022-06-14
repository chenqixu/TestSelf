package com.bussiness.bi.bigdata.db;

import java.util.Date;

public class SupperTest {
	public static void main(String[] args) {
		DBOracleTest dbot = new DBOracleTest();
		dbot.finalize();
		dbot = null;
		System.out.println(new Date()+" "+DataSourceUtils.getDataSourceState(DataSourceUtils.getConfSource()));
		int i = 2;
		while(i>0){
			System.out.println("[i]"+i);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i--;
		}
		System.out.println(new Date()+" "+DataSourceUtils.getDataSourceState(DataSourceUtils.getConfSource()));		
	}
}
