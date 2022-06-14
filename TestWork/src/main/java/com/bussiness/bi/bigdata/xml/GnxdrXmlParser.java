package com.bussiness.bi.bigdata.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import com.newland.bi.ResultXML;
import com.newland.bi.XMLData;

public class GnxdrXmlParser {
	private String readpath = "";
	private String filename = "";
	public GnxdrXmlParser(String _readpath, String _filename){
		this.readpath = _readpath;
		this.filename = _filename;
	}
	public void process(){
		FileReader fr = null;
		BufferedReader br = null;		
		try{
			fr = new FileReader(this.readpath+this.filename);
			br = new BufferedReader(fr);
			StringBuffer xml = new StringBuffer();
			String tmp = "";
			while((tmp=br.readLine())!=null){
				xml.append(tmp);
			}
			br.close();
			fr.close();
			// 解析XML，生成SQL语句并写入文件
			xmlparser(xml.toString(), this.readpath, this.filename);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(br!=null){
				try{
					br.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			if(fr!=null){
				try{
					fr.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	private void xmlparser(String xml, String path, String filename){
		StringBuffer sb = new StringBuffer();
		ResultXML rx = new ResultXML();
		XMLData xd = new XMLData(xml);
		rx.rtFlag = true;
		rx.bXmldata = true;
		rx.xmldata = xd;
		rx.setbFlag(false);
		rx.setRowFlagInfo("property");
		rx.First();
		while(!rx.isEof()){
			StringBuffer tmp = new StringBuffer();
			String name = rx.getColumnsValue("name");
			if(name.equals("multiOrSinglePathToUTAp")){
				name = "multiOrSinglePathToUTAP";
			}
			String value = rx.getColumnsValue("value");			
			String description = rx.getColumnsValue("description");
			tmp.append("insert into data_load_combine_conf(id,name,value,description) values('");
			tmp.append(filename.substring(0, filename.length()-4));
			tmp.append("','");
			tmp.append(name.trim());
			tmp.append("','");
			tmp.append(value.trim());
			tmp.append("','");
			tmp.append(description.trim());
			tmp.append("');");
			sb.append(tmp.toString());
			sb.append("\r\n");
			rx.Next();
		}
		//没有DayOrHour
		if(sb.toString().indexOf("DayOrHour")<0){
			StringBuffer tmp = new StringBuffer();
			String name = "DayOrHour";
			String value = "hour";
			String description = "设置合并时按天目录还是按小时目录,hour表示小时目录,day表示天目录";
			tmp.append("insert into data_load_combine_conf(id,name,value,description) values('");
			tmp.append(filename.substring(0, filename.length()-4));
			tmp.append("','");
			tmp.append(name.trim());
			tmp.append("','");
			tmp.append(value.trim());
			tmp.append("','");
			tmp.append(description.trim());
			tmp.append("');");
			sb.append(tmp.toString());
			sb.append("\r\n");
		}
		//新增的配置-CYW-防止丢数据
		sb.append("insert into data_load_combine_conf(id,name,value,description) values('");
		sb.append(filename.substring(0, filename.length()-4));
		sb.append("','ifNeedMvCtlFile','true','合并程序处理完一个文件后，校验源文件是否需要移动目录');");		
		sb.append("\r\n");
		sb.append("insert into data_load_combine_conf(id,name,value,description) values('");
		sb.append(filename.substring(0, filename.length()-4));
		sb.append("','mvCtlFileToDestDire','D:/1/sourceDir2/','检验文件移动路径(Linux系统路径,必须以斜扛结尾)');");		
		sb.append("\r\n");
		sb.append("insert into data_load_combine_conf(id,name,value,description) values('");
		sb.append(filename.substring(0, filename.length()-4));
		sb.append("','ifReadCtlFile','true','是否需要读取校验文件记录数据');");		
		sb.append("\r\n");
		sb.append("insert into data_load_combine_conf(id,name,value,description) values('");
		sb.append(filename.substring(0, filename.length()-4));
		sb.append("','inputCtlFileDir','D:/1/sourceDir/','校验文件存放路径(Linux系统路径,必须以斜扛结尾)');");		
		sb.append("\r\n");		
		
		File filepath = null;
		File newfile = null;
		FileOutputStream fos = null;
		try{
			filepath = new File(path+"newfile/");
			if(!filepath.exists())
				filepath.mkdir();
			newfile = new File(path+"newfile/"+filename.substring(0, filename.length()-4)+".sql");
			fos = new FileOutputStream(newfile, false);
			fos.write(sb.toString().getBytes());
			fos.flush();
			fos.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(fos!=null){
				try{
					fos.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
//		String gnxdr_path = "d:/Work/ETL/采集合并/gnxdr配置/";
//		String hwlte_path = "d:/Work/ETL/采集合并/hwlte配置/";
		String mc_path = "d:/Work/ETL/采集合并/mc配置/";
		File file = new File(mc_path);
		File[] fl = file.listFiles();
		for(int i=0;i<fl.length;i++){
			if(fl[i].isFile() && fl[i].getName().endsWith(".xml")){
				new GnxdrXmlParser(mc_path, fl[i].getName()).process();
			}
		}
	}
}
