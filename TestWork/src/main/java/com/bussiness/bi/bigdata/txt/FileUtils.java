package com.bussiness.bi.bigdata.txt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class FileUtils {
	public static BufferedReader reader = null;
	private static Map<String, BufferedReader> peser_result = null;
	private byte[] lineChar = "\n".getBytes();
	private String writePath = null;
	private OutputStream fileOut = null;
	private int maxcount = 0;
	private int offset = 0;
	
	public void setWritePath(String writePath) {
		this.writePath = writePath;
	}

	public void setMaxcount(int maxcount) {
		this.maxcount = maxcount;
	}

	/**
	 * 字符串转输入流
	 * */
	private InputStream StringToInputStream(String param){
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
	
	/**
	 * 输出流转输入流，根据行数要求打印
	 * */
	private void getBatch(OutputStream ops, int num){
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
	
	/**
	 * 根据行数要求打印输入流
	 * */
	private void getBatch(BufferedReader _reader, int num){
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
	
	/**
	 * 用xslt处理xml
	 * */
	private void transformXmlByXslt(File srcXml, String xslt) {
		if(srcXml!=null && srcXml.length()>0 && xslt!=null && !xslt.trim().equals("")){
			System.out.println("ok");
	    }
	 }
	
	/**
	 * 测试，把输入流写入Map
	 * */
	private void test(){
		peser_result = new HashMap<String, BufferedReader>();
		peser_result.put("a", new BufferedReader(new InputStreamReader(new ByteArrayInputStream("123".getBytes()))));
		peser_result.put("b", new BufferedReader(new InputStreamReader(new ByteArrayInputStream("456".getBytes()))));
		peser_result.put("c", new BufferedReader(new InputStreamReader(new ByteArrayInputStream("789".getBytes()))));
	}
	
	/**
	 * 关闭Map中的输入流
	 * */
	private void close(){
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
	 * xslt业务测试
	 * */
	public void xsltTest(){
		test();
		close();
		for(Map.Entry<String, BufferedReader> peser : peser_result.entrySet()){
			BufferedReader reader = peser.getValue();
			System.out.println(reader);
		}
		transformXmlByXslt(new File("d:/tmp/2.txt"), null);
		try{
			OutputStream os = new ByteArrayOutputStream();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
			for(int i=0;i<20;i++){
				bw.write("test"+i);
				bw.newLine();
			}
			bw.flush();
			os.flush();
			InputStream ips = new ByteArrayInputStream(((ByteArrayOutputStream)os).toByteArray());
			reader = new BufferedReader(new InputStreamReader(ips));
			getBatch(reader, 5);
			getBatch(reader, 5);
			getBatch(reader, 5);
			getBatch(reader, 5);
			getBatch(reader, 5);
			getBatch(reader, 5);
			bw.close();
			os.close();
			ips.close();
//			reader.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * linux：在/tmp目录下创建临时文件
	 * */
	public void FileCreateTmp() throws Exception{
		File.createTempFile("hive-rowcontainer", "");
	}
	
	/**
	 * 关闭输出流
	 * 如果不能关闭，并且输出流为空，则新增输出流
	 * */
	private void getWrite() {
		if(isClose()){
			closeWrite();
		}else{
			if(fileOut == null && writePath!=null
					&& writePath.trim().length()>0)
				try {
					fileOut = new FileOutputStream(writePath);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * 写入
	 * */
	private boolean write(String content) {
		if(fileOut != null){
			try {
				if(offset==0){
					fileOut.write(content.getBytes());
				}else{
					fileOut.write(lineChar);
					fileOut.write(content.getBytes());					
				}
				fileOut.flush();
				offset++;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 写入测试
	 * */
	public void writeTest(String content, int cnt) throws IOException{
		for(int i=0;i<cnt;i++){
			getWrite();
			if(!write(content))break;
		}
	}
	
	/**
	 * 能否关闭
	 * */
	private boolean isClose(){
		return offset+1>maxcount;
	}
	
	/**
	 * 关闭输出流
	 * */
	private void closeWrite(){
		if(fileOut != null){
			try {
				fileOut.close();
				fileOut = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 备份文件
	 * */
	public void backFile(String localBakFile){
		String memoryGzWriter = "abc";
		try (OutputStream fileOut = new FileOutputStream(localBakFile)) {
			fileOut.write(memoryGzWriter.getBytes());
			fileOut.flush();
		} catch (Exception e) {
			throw new RuntimeException("无法创建文件:" + localBakFile, e);
		}
		// 构造本地OK文件
		try (OutputStream fileOut = new FileOutputStream(localBakFile + ".ok")) {
			fileOut.flush();
		} catch (Exception e) {
			throw new RuntimeException("无法创建文件:" + localBakFile + ".ok", e);
		}
	}
	
	public static void main(String[] args) throws IOException{
		FileUtils fu = new FileUtils();
		fu.setWritePath("d:\\tmp\\com\\1.txt");//设置输出文件
		fu.setMaxcount(2);//设置单个文件最大输出行数
		fu.writeTest("abc123", 100);//写入测试
//		fu.backFile("d:\\tmp\\com\\2.txt");//备份文件测试
//		fu.xsltTest();//xslt业务测试
	}
}
