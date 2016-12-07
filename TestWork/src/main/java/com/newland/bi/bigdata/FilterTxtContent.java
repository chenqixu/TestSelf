package com.newland.bi.bigdata;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FilterTxtContent {
	
	public void filter(String filename, String rule){
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename), "GBK"));
			String _tmp = null;
			while((_tmp=reader.readLine())!=null){
				if(_tmp.indexOf(rule)>=0){
					System.out.println(_tmp);
				}
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new FilterTxtContent().filter("d:/Work/TOOL/BI.txt","SSC/BIGDATA2.0/Develop/SourceCode/Code/edc-bigdata/edc-bigdata-crawler/edc-bigdata-SNsearchCrawler");
	}
}
