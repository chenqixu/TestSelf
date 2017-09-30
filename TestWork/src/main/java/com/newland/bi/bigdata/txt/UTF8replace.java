package com.newland.bi.bigdata.txt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class UTF8replace {
	public static char euro = (char)8364;
	public static String line = "\\|";
	public static void main(String[] args) {
		BufferedReader reader = null;
		LineNumberReader lineNumberReader =null;
		Writer out = null;
		if(args!=null && args.length==1){
			String file_name = args[0];
			if(!new File(file_name).exists()){
				System.out.println("[file_name]"+file_name+" is not a file, exception exit.");
				System.exit(0);
			}			
			try{
				File readFile = new File(file_name);
//				reader = new BufferedReader(new InputStreamReader(
//						new FileInputStream(readFile), "GBK"));	
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(readFile), "UTF-8"));	
				lineNumberReader = new LineNumberReader(reader);
				File writeFile = new File(file_name+".tmp");
				out = new OutputStreamWriter(new FileOutputStream(writeFile), "UTF-8");
				String str = "";
				int count = 0;
				while((str=lineNumberReader.readLine())!=null){
					count++;
					out.write(str.replaceAll(line, String.valueOf(euro)));
					out.write("\r\n");
				}
				lineNumberReader.close();
				reader.close();
				out.flush();
				out.close();
				System.out.println("file count:"+count);
				if(count==0){
					System.out.println("输入的文件为空或转码异常");
					System.exit(1);
				}
			}catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			}finally{
				if(lineNumberReader!=null){
					try {
						lineNumberReader.close();
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
				if(reader!=null){
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
				if(out!=null){
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			}
		}else{
			System.out.println("error input param , please reinput.");
			System.exit(1);
		}
	}
}

