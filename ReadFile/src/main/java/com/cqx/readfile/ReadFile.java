package com.cqx.readfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class ReadFile {
	public static void main(String[] args) {
		BufferedReader reader = null;		
		LineNumberReader lineNumberReader =null;
		if(args!=null && args.length==1){
			String file_name = args[0];
			if(!new File(file_name).exists()){
				System.out.println("[file_name]"+file_name+" is not a file, exception exit.");
				System.exit(0);
			}			
			try{
				File readFile = new File(file_name);
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(readFile)));	
				lineNumberReader = new LineNumberReader(reader);			
				int filecount = 0;
				while((lineNumberReader.readLine())!=null){
					filecount++;
				}
				System.out.println("读到的文件["+file_name+"]记录数为["+filecount+"].");
				lineNumberReader.close();
				reader.close();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(lineNumberReader!=null){
					try {
						lineNumberReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(reader!=null){
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			System.out.println("error input param , please reinput.");
			System.exit(1);
		}		
	}
}
