package com.newland.bi.bigdata.changecode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangeCode {
	
	private String scan_path = "";
	private String scan_rule = "";
	protected String read_code = "";
	protected String write_code = "";
	private boolean isLoop = true; // 是否进入每个文件夹扫描(-r)
	
	public boolean isLoop() {
		return isLoop;
	}

	public void setLoop(boolean isLoop) {
		this.isLoop = isLoop;
	}

	public String getScan_rule() {
		return scan_rule;
	}

	public void setScan_rule(String scan_rule) {
		this.scan_rule = scan_rule;
	}

	public String getScan_path() {
		return scan_path;
	}

	public void setScan_path(String scan_path) {
		this.scan_path = scan_path;
	}

	public String getRead_code() {
		return read_code;
	}

	public void setRead_code(String read_code) {
		this.read_code = read_code;
	}

	public String getWrite_code() {
		return write_code;
	}

	public void setWrite_code(String write_code) {
		this.write_code = write_code;
	}
	
	/**
	 * 扫描过滤出文件列表
	 * */
	public List<String> Scan(String scanpath){
//		System.out.println("[scanpath]"+scanpath);
		List<String> result = new Vector<String>();
		File file = null;
		Pattern pat;
        Matcher mat;
        boolean matched = false;
		try{
			if(scanpath.trim().length()>0 && scan_rule.trim().length()>0){
				file = new File(scanpath);
				if(file.isDirectory()){
					File[] fl = file.listFiles();
					for(int i=0;i<fl.length;i++){
						if(fl[i].isFile()){
							// 规则
							pat = Pattern.compile(scan_rule);
							// 名称
					        mat = pat.matcher(fl[i].getName());
					        // 名称是否匹配规则
					        matched = mat.matches();
					        if(matched){
//					        	System.out.println("[matched]"+fl[i].getPath());
					        	result.add(fl[i].getPath());
					        }
						}else if(fl[i].isDirectory() && isLoop){
							result.addAll(Scan(fl[i].getPath()));
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 扫描过滤出文件列表
	 * */
	public List<String> Scan(String scanpath, String _scan_rule){
//		System.out.println("[scanpath]"+scanpath+"[_scan_rule]"+_scan_rule);
		List<String> result = new Vector<String>();
		File file = null;
		Pattern pat;
        Matcher mat;
        boolean matched = false;
		try{
			if(scanpath.trim().length()>0 && _scan_rule.trim().length()>0){
				file = new File(scanpath);
				if(file.isDirectory()){
					File[] fl = file.listFiles();
					for(int i=0;i<fl.length;i++){
						if(fl[i].isFile()){
							// 规则
							pat = Pattern.compile(_scan_rule);
							// 名称
					        mat = pat.matcher(fl[i].getName());
					        // 名称是否匹配规则
					        matched = mat.matches();
					        if(matched){
//					        	System.out.println("[matched]"+fl[i].getPath());
					        	result.add(fl[i].getPath());
					        }
						}else if(fl[i].isDirectory() && isLoop){
							result.addAll(Scan(fl[i].getPath(), _scan_rule));
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	public void Change(){
		List<String> changelist = Scan(scan_path);
		BufferedReader reader = null;
		BufferedWriter writer = null;	
		try{
			//read and write
			for(int i=0;i<changelist.size();i++){
				File readFile = new File(changelist.get(i));
				File writeFile = new File(changelist.get(i)+"bak");
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(readFile), read_code));
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(writeFile), write_code));
				String _tmp = null;
				while((_tmp=reader.readLine())!=null){
					writer.write(_tmp);
					writer.write("\r\n");
				}
				reader.close();
				writer.flush();
				writer.close();
				if(readFile.exists()){
					boolean delete = readFile.delete();
					if(delete){
						writeFile.renameTo(readFile);
					}
				}
			}
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
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 获得子目录
	 * */
	public List<String> getEachSubDirectory(String parentPath){
		File file = null;
		List<String> sublist = new Vector<String>();
		try{
			file = new File(parentPath);
			if(file.isDirectory()){
				File[] fl = file.listFiles();
				for(int i=0;i<fl.length;i++){
					if(fl[i].isDirectory()){
						String tmpPath = fl[i].getPath();
						if(tmpPath.endsWith("CVS")){
						}else{
							sublist.add(fl[i].getPath());
							System.out.println(fl[i].getPath());
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sublist;
	}
	
	public static void main(String[] args) {
		ChangeCode cc = new ChangeCode();
		cc.setScan_path("H:/Work/WorkSpace/MyEclipse10/edc-bigdata-crawler/edc-bigdata-TXDMsearchCrawler");
		cc.setScan_rule(".*\\.java");
		cc.setRead_code("UTF-8");
		cc.setWrite_code("GBK");
		cc.Change();
//		cc.getEachSubDirectory("d:/Work/CVS/BI/SSC/BIGDATA2.0/Develop/SourceCode/Code/edc-bigdata/edc-bigdata-crawler");
//		cc.getEachSubDirectory("d:/Work/CVS/BI/SSC/BIGDATA2.0/Develop/SourceCode/Code/edc-bigdata/edc-bigdata-dataCollect");
//		cc.getEachSubDirectory("d:/Work/CVS/BI/SSC/BIGDATA2.0/Develop/SourceCode/Code/edc-bigdata/edc-bigdata-dataCombine");
//		cc.getEachSubDirectory("d:/Work/CVS/BI/SSC/BIGDATA2.0/Develop/SourceCode/Code/edc-bigdata/edc-bigdata-dataFileSort");
//		cc.getEachSubDirectory("d:/Work/CVS/BI/SSC/BIGDATA2.0/Develop/SourceCode/Code/edc-bigdata/edc-bigdata-fileToHbase");
//		cc.getEachSubDirectory("d:/Work/CVS/BI/SSC/BIGDATA2.0/Develop/SourceCode/Code/edc-bigdata/edc-bigdata-flume");	
//		cc.getEachSubDirectory("d:/Work/CVS/BI/SSC/BIGDATA2.0/Develop/SourceCode/Code/edc-bigdata/edc-bigdata-interface");	
//		cc.getEachSubDirectory("d:/Work/CVS/BI/SSC/BIGDATA2.0/Develop/SourceCode/Code/edc-bigdata/edc-bigdata-join");
//		cc.getEachSubDirectory("d:/Work/CVS/BI/SSC/BIGDATA2.0/Develop/SourceCode/Code/edc-bigdata/edc-bigdata-other");
//		cc.getEachSubDirectory("d:/Work/CVS/BI/SSC/BIGDATA2.0/Develop/SourceCode/Code/edc-bigdata/edc-bigdata-statistic");
//		cc.getEachSubDirectory("d:/Work/CVS/BI/SSC/BIGDATA2.0/Develop/SourceCode/Code/edc-bigdata/edc-bigdata-xml");
	}
}
