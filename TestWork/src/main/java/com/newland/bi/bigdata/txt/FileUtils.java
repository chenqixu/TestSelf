package com.newland.bi.bigdata.txt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import com.newland.bi.bigdata.xml.IXmlReader1;
import com.newland.bi.bigdata.xml.IXmlReaderImpl1;

public class FileUtils {
	public static BufferedReader reader = null;
	private static Map<String, BufferedReader> peser_result = null;
	public static InputStream StringToInputStream(String param){
		InputStream tInputStringStream = null;
		if(param!=null && !param.trim().equals("")){
			try{
				tInputStringStream = new ByteArrayInputStream(param.getBytes());
			}catch (Exception ex){
				ex.printStackTrace();
			}
		}
		return tInputStringStream;
	}
	
	public static void getBatch(OutputStream ops, int num){
		try{
			InputStream ips = new ByteArrayInputStream(((ByteArrayOutputStream)ops).toByteArray());
			BufferedReader reader = new BufferedReader(new InputStreamReader(ips));
			String str = "";
			int count = 0;
			while((str=reader.readLine())!=null){
				System.out.println(str);
				count++;
				if(count==num)break;
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public static void getBatch(BufferedReader _reader, int num){
		try{
			String str = "";
			int count = 0;
			while(_reader!=null && num>0 && (str=_reader.readLine())!=null){
				System.out.println(str);
				count++;
				if(count==num)break;
			}
			System.out.println("[read end]"+str);
			if(str==null && _reader!=null){
				System.out.println("[close]"+str+"[reader]"+_reader);
				_reader.close();
				System.out.println("[close]"+str+"[reader]"+_reader);
				_reader = null;
				System.out.println("[close]"+str+"[reader]"+_reader);
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public static void transformXmlByXslt(File srcXml, String xslt) {
		if(srcXml!=null && srcXml.length()>0 && xslt!=null && !xslt.trim().equals("")){
			System.out.println("ok");
	    }
	 }
	
	public static void test(){
		peser_result = new HashMap<String, BufferedReader>();
		peser_result.put("a", new BufferedReader(new InputStreamReader(new ByteArrayInputStream("123".getBytes()))));
		peser_result.put("b", new BufferedReader(new InputStreamReader(new ByteArrayInputStream("456".getBytes()))));
		peser_result.put("c", new BufferedReader(new InputStreamReader(new ByteArrayInputStream("789".getBytes()))));
	}
	
	public static void close(){
		for(Map.Entry<String, BufferedReader> peser : peser_result.entrySet()){
			BufferedReader reader = peser.getValue();
			if(reader!=null){
				try{
					reader.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		peser_result = null;
	}
	
	/**
	 * linux：在/tmp目录下创建临时文件
	 * */
	public void FileCreateTmp() throws Exception{
		File.createTempFile("hive-rowcontainer", "");
	}
	
	public static void main(String[] args) {
//		test();
//		close();
//		for(Map.Entry<String, BufferedReader> peser : peser_result.entrySet()){
//			BufferedReader reader = peser.getValue();
//			System.out.println(reader);
//		}
//		transformXmlByXslt(new File("d:/2.txt"), null);
//		try{
//			OutputStream os = new ByteArrayOutputStream();
//			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
//			for(int i=0;i<20;i++){
//				bw.write("test"+i);
//				bw.newLine();
//			}
//			bw.flush();
//			os.flush();
//			InputStream ips = new ByteArrayInputStream(((ByteArrayOutputStream)os).toByteArray());
//			reader = new BufferedReader(new InputStreamReader(ips));
//			getBatch(reader, 5);
//			getBatch(reader, 5);
//			getBatch(reader, 5);
//			getBatch(reader, 5);
//			getBatch(reader, 5);
//			getBatch(reader, 5);
//			bw.close();
//			os.close();
//			ips.close();
////			reader.close();
//		}catch(IOException ex){
//			ex.printStackTrace();
//		}
	}
}
