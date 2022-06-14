package com.bussiness.bi.bigdata.ant;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConstantsAnt {
	public final static String rootPath = System.getProperty("user.dir");
	public final static String spltstr = File.separator;//文件路径分隔符(区分windows和linux)
	public final static String pathedc = rootPath+spltstr+"src"+spltstr+"main"
			+spltstr+"resources"+spltstr+"conf"+spltstr+"edc-bigdata.properties";
	public final static String tab = "\r\n";//回车换行
	private static List<String> programlist = null;
	private static List<String> webprogramlist = null;
	private static Map<String, String> jdkmap = null;
	private static void InstanceJdkMap(){
		if(jdkmap==null){
			jdkmap = new HashMap<String, String>();
			jdkmap.put("XmlParser", "${jdk1.5.path}");
		}
	}
	public static String getJdkByName(String name) {
		// 初始化个别程序对应的JDK版本
		InstanceJdkMap();		
		return jdkmap.get(name);
	}
	/**
	 * jar工程
	 * */
	public static List<String> getProgramelistInstance(){
		// jar工程列表
		if(programlist==null){
			programlist = new LinkedList<String>();
			//crawler
			programlist.add("searchCrawler");
			programlist.add("SNsearchCrawler");
			programlist.add("TXDMsearchCrawler");
			//dataCollect
			programlist.add("dataCollectAll");
			programlist.add("dataCollectAllRT");
			programlist.add("dataCollectMrDt");
			programlist.add("dataCollectorHWLte");
			programlist.add("dataCollectorSimultaneously");
			//dataCombine
			programlist.add("dataLoadCombine-Shell");
			//dataFileSort
			programlist.add("fileSort");
			//fileToHbase
			programlist.add("fileToHbase");
			programlist.add("fileToHbase_HW");
			programlist.add("fileToHbase_NX");
			programlist.add("giToHbase");
			//flume
			programlist.add("flumecollect");
			programlist.add("flumedistribution");
			//join
			programlist.add("detailToHbase");
			programlist.add("getHWLteDataToHbase");
			programlist.add("getNXLteDataToHbase");
			programlist.add("joinGiAndLTE");
			programlist.add("joinGiGpfs");
			programlist.add("joinGnXdr");
			//other
			programlist.add("dataFilterExtract");
			programlist.add("getKeyWord");
			programlist.add("getMovementTrackData");
			programlist.add("getMovementTrackDataDay");
			programlist.add("getMovementTrackDataHour");
			//statistic
			programlist.add("statisticGnxdrIpHttp");
			programlist.add("statisticHuaweiLte");
			programlist.add("statisticIpHttp");
			programlist.add("statisticLte");
			//xml
			programlist.add("XmlParser");
		}
		return programlist;
	}
	/**
	 * war工程
	 * */
	public static List<String> getWebProgramelistInstance(){
		// war工程列表
		if(webprogramlist==null){
			webprogramlist = new LinkedList<String>();
			//bi_bigdata_svc
			webprogramlist.add("bi_bigdata_svc");
		}
		return webprogramlist;
	}
}
