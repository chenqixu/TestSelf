package com.newland.bi.bigdata.hbase;

import java.util.Random;

public class Mod {
	//用于取模
	private static Random random = new Random();
	//手机号取模，region预分区数
	public static final int REGION_PARTITION_NUM = 200;

	/**
	 * @description: 对号码取模
	 * @param telNumber
	 * */
	private String generateModKey(long telnumber){
		long tempLong;
		try{
			random.setSeed(telnumber);
			tempLong =  Math.abs(random.nextLong()%REGION_PARTITION_NUM);
			if(tempLong < 10){
				return "00"+tempLong;
			}else if(tempLong < 100){
				return "0"+tempLong;
			}else if(tempLong < REGION_PARTITION_NUM){
				return ""+tempLong;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		Mod m = new Mod();
//		for(int i=0;i<=1000;i++){
			System.out.println(m.generateModKey(Long.valueOf("13976079335")));
//		}
	}
}
