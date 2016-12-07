package com.newland.bi.bigdata.txt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

public class HNTest2 {
	public static final DecimalFormat df = new DecimalFormat("#0");
	
	public void read(String path) {
		FileReader fr = null;
		BufferedReader br = null;
		try {
//			fr = new FileReader(new File(path));
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(path), "ISO-8859-2"));
			String readline = "";
			int cnt = 0;
			while((readline=br.readLine())!=null){
				if(cnt==567){
					System.out.println(readline);
				}
				cnt++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(fr!=null)
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(br!=null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	public static void main(String[] args) throws Exception {
//		String str1 = "abcd";
//		String str2 = "ab";
//		Double db1 = 4341077.0;
//		System.out.println(str1.contains(str2));
//		System.out.println(df.format(db1));
//		System.out.println(String.valueOf(db1));
//		
//		String a = "测试";
//		String b = "试测";
//		String a_code = "GBK";
//		String b_code = "UTF-8";
//		System.out.println(new String(a.getBytes(b_code),a_code));
//		System.out.println(new String(a.getBytes(b_code),b_code));
		HNTest2 ht = new HNTest2();
		ht.read("j:\\Work\\ETL\\上网查证\\海南\\上线\\经分上线\\生产环境\\errorlog\\20161109\\000006_0");
	}
}
