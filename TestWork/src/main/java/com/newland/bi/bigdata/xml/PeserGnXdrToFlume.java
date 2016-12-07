package com.newland.bi.bigdata.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

public class PeserGnXdrToFlume {
	public static List<String> conflist;
	
	public static List<String> ftpServerIp;
	public static List<String> ftpServerPort;
	public static List<String> ftpServerUser;
	public static List<String> ftpServerPassword;
	public static List<String> sourceDataPath;
	public static List<String> dataSourceFileSuffixName;
	public static List<String> ctlSourceFileSuffixName;
	public static List<String> ifNeedDeleteSourceDataFile;
	public static List<String> ifNeedMvSourceDataFile;
	public static List<String> ifNeedDeleteSourceCtlFile;
	public static List<String> ifNeedMvSourceCtlFile;
	public static List<String> mvSourceDataFilePath;
	public static List<String> mvSourceCtlFilePath;
	public static List<String> ifDownloadCtlFile;
	
	public static Map<String, List<String>> propertyList;
	static{
		conflist = new ArrayList<String>();
		conflist.add("ftpServerIp");
		conflist.add("ftpServerPort");
		conflist.add("ftpServerUser");
		conflist.add("ftpServerPassword");
		conflist.add("sourceDataPath");
		conflist.add("dataSourceFileSuffixName");
		conflist.add("ctlSourceFileSuffixName");
		conflist.add("ifNeedDeleteSourceDataFile");
		conflist.add("ifNeedMvSourceDataFile");
		conflist.add("ifNeedDeleteSourceCtlFile");
		conflist.add("ifNeedMvSourceCtlFile");
		conflist.add("mvSourceDataFilePath");
		conflist.add("mvSourceCtlFilePath");
		conflist.add("ifDownloadCtlFile");

		ftpServerIp = new ArrayList<String>();
		ftpServerPort = new ArrayList<String>();
		ftpServerUser = new ArrayList<String>();
		ftpServerPassword = new ArrayList<String>();
		sourceDataPath = new ArrayList<String>();
		dataSourceFileSuffixName = new ArrayList<String>();
		ctlSourceFileSuffixName = new ArrayList<String>();
		ifNeedDeleteSourceDataFile = new ArrayList<String>();
		ifNeedMvSourceDataFile = new ArrayList<String>();
		ifNeedDeleteSourceCtlFile = new ArrayList<String>();
		ifNeedMvSourceCtlFile = new ArrayList<String>();
		mvSourceDataFilePath = new ArrayList<String>();
		mvSourceCtlFilePath = new ArrayList<String>();
		ifDownloadCtlFile = new ArrayList<String>();
		
		propertyList = new HashMap<String, List<String>>();
		propertyList.put("ftpServerIp",ftpServerIp);
		propertyList.put("ftpServerPort",ftpServerPort);
		propertyList.put("ftpServerUser",ftpServerUser);
		propertyList.put("ftpServerPassword",ftpServerPassword);
		propertyList.put("sourceDataPath",sourceDataPath);
		propertyList.put("dataSourceFileSuffixName",dataSourceFileSuffixName);
		propertyList.put("ctlSourceFileSuffixName",ctlSourceFileSuffixName);
		propertyList.put("ifNeedDeleteSourceDataFile",ifNeedDeleteSourceDataFile);
		propertyList.put("ifNeedMvSourceDataFile",ifNeedMvSourceDataFile);
		propertyList.put("ifNeedDeleteSourceCtlFile",ifNeedDeleteSourceCtlFile);
		propertyList.put("ifNeedMvSourceCtlFile",ifNeedMvSourceCtlFile);
		propertyList.put("mvSourceDataFilePath",mvSourceDataFilePath);
		propertyList.put("mvSourceCtlFilePath",mvSourceCtlFilePath);
		propertyList.put("ifDownloadCtlFile",ifDownloadCtlFile);
	}
	
	public List<String> recurrenceXML(String path){
		List<String> fileNameList = new ArrayList<String>();
		File file = new File(path);
		File[] fl = file.listFiles();
		for(int i=0;i<fl.length;i++){
			if(fl[i].isFile() && fl[i].getName().endsWith(".xml")){
				fileNameList.add(fl[i].getPath());
			}else if(fl[i].isDirectory()){
				fileNameList.addAll(recurrenceXML(fl[i].getPath()));
			}
		}
		return fileNameList;
	}
	
	public void getXMLConf(String xml_path){
		Configuration conf = new Configuration();
		conf.addResource(new Path(xml_path));
		String sourceDataPath = conf.get("sourceDataPath");
		String[] sourceDataPatharr = sourceDataPath.split(",");
		int loopcnt = 1;
		if(sourceDataPatharr.length>1){
			loopcnt = sourceDataPatharr.length;
//			System.out.println(xml_path+" "+loopcnt);
		}
		for(String property : conflist){
//			System.out.println(property+" "+conf.get(property));
			int tmp_loopcnt = loopcnt;
			if(property.equals("sourceDataPath") ||
					property.equals("mvSourceDataFilePath") ||
					property.equals("mvSourceCtlFilePath")){
				tmp_loopcnt = 1;
			}
			for(int i=0;i<tmp_loopcnt;i++)
				propertyList.get(property).add(conf.get(property));
		}
	}
	
	public static void main(String[] args) {
		String gn_xdr_path = "d:/Work/CVS/BI/需求上线发布文档/2016年/2016年01月/73206-BI_关于华为LTE数据采集程序采集多台源数据服务器优化的需求/配置文件/";
		PeserGnXdrToFlume pgf = new PeserGnXdrToFlume();
		List<String> fileNameList = pgf.recurrenceXML(gn_xdr_path);
		for(String str : fileNameList){
//			System.out.println(str);
			pgf.getXMLConf(str);
		}
		for(String property : pgf.conflist){
//			System.out.println(pgf.propertyList.get(property).size());
			System.out.println(property+" "+pgf.propertyList.get(property).toString());
		}
	}
}
