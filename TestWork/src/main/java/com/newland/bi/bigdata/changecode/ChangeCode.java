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

import com.cqx.process.LogInfoFactory;

public class ChangeCode {

	private static LogInfoFactory logger = LogInfoFactory.getInstance(ChangeCode.class);
	/**
	 * 扫描路径
	 */
	private String scan_path = "";
	/**
	 * 扫描规则
	 */
	private String scan_rule = "";
	/**
	 * 读取文件的编码
	 */
	protected String read_code = "";
	/**
	 * 写入文件的编码
	 */
	protected String write_code = "";
	/**
	 * 是否进入每个文件夹扫描(-r)
	 */
	private boolean isLoop = true;
	
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
	 * @return
	 */
	public List<String> scan() {
		return scan(scan_path, scan_rule);
	}
	
	/**
	 * 扫描过滤出文件列表
	 * @param scanpath
	 * @return
	 */
	public List<String> scan(String scanpath){
		return scan(scanpath, scan_rule);
	}
	
	/**
	 * 扫描过滤出文件列表
	 * @param scanpath
	 * @param _scan_rule
	 * @return
	 */
	public List<String> scan(String scanpath, String _scan_rule){
		logger.debug("[scanpath]"+scanpath+"[_scan_rule]"+_scan_rule);
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
					        	logger.debug("[matched]"+fl[i].getPath());
					        	result.add(fl[i].getPath());
					        }
						}else if(fl[i].isDirectory() && isLoop){
							result.addAll(scan(fl[i].getPath(), _scan_rule));
						}
					}
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		// 结果打印
		for(String _str : result) {
			logger.debug(_str);
		}
		return result;
	}
	
	public void change(String filepath, String read_code, String write_code) {
		BufferedReader reader = null;
		BufferedWriter writer = null;	
		try{
			//read and write
			File readFile = new File(filepath);
			File writeFile = new File(filepath + "bak");
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
	
	public void change(String filepath){
		change(filepath, read_code, write_code);
	}

	public void change(){
		for(String _filepath : scan(scan_path)){
			change(_filepath, read_code, write_code);
		}
	}
	
	/**
	 * 获得子目录
	 * @param parentPath
	 * @return
	 */
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
	
	/**
	 * 通过路径读取文件内容到List
	 * @param path
	 * @return
	 */
	protected List<String> read(String path) {
		File file = null;
		BufferedReader reader = null;
		List<String> sublist = new Vector<String>();
		try{
			file = new File(path);
			if(file.isFile()){
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(file), read_code));
				String _tmp = null;
				while((_tmp=reader.readLine())!=null){
					sublist.add(_tmp);
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
		}
		return sublist;
	}
	
	public static void main(String[] args) {
		ChangeCode cc = new ChangeCode();
		cc.setScan_path("D:/Document/Workspaces/Git/TestSelf");
		cc.setScan_rule(".*\\.java");
		cc.setRead_code("GBK");
		cc.setWrite_code("UTF-8");
//		cc.change();
		cc.scan();
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
