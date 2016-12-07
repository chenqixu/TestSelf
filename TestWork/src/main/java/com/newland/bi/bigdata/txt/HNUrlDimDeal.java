package com.newland.bi.bigdata.txt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.newland.bi.bigdata.bean.Cfg_web_class_name;
import com.newland.bi.bigdata.bean.Cfg_web_class_nameMap;

public class HNUrlDimDeal {
	public static void main(String[] args) {				
//		String outputfile = "D:/Work/ETL/上网查证/海南/维表/URLTOP.csv";
//		String outputfile = "D:/Work/ETL/上网查证/海南/维表/URLNULL.csv";
		String outputfile = "D:/Work/ETL/上网查证/海南/维表/URLNOTTOPANDNULL.csv";
		File file = new File("D:/Work/ETL/上网查证/海南/维表/URL配置表.csv");
//		File file = new File("D:/Work/ETL/上网查证/海南/维表/URLTEST.csv");
		BufferedReader br = null;
		HashMap<String,List<Cfg_web_class_name>> hcb = null;
		try{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
			String str = "";
			String url = "";
			hcb = new HashMap<String,List<Cfg_web_class_name>>();
			// 逐行读取文件，转成bean
			while((str=br.readLine())!=null){
				Cfg_web_class_name cb = new Cfg_web_class_name();
				cb.toBean(str);			
				url = cb.getUrl();
				String firstdomain = GetFirstDomain(url,null);
				String seconddomain = GetSecondDomain(url,null);
				cb.setDomain_level1(firstdomain);
				cb.setDomain_level1_name(cb.getWeb_name());// 默认设置为web_name
				cb.setDomain_level2(seconddomain);
				cb.setDomain_level2_name(cb.getWeb_name());
				// 提取出一级域名，作为map的key，bean list作为value
				if(hcb.get(firstdomain)==null){
					List<Cfg_web_class_name> list = new ArrayList<Cfg_web_class_name>();
					list.add(cb);
					hcb.put(firstdomain, list);
				}else{
					hcb.get(firstdomain).add(cb);
				}
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(br!=null)br.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		// 循环map，把识别的web_name处理成一级域名名称
		// 规则1：取出现最多次的相同名称作为一级域名名称
		// 规则2：如果都不相同，使用第一个web_name作为一级域名名称
		if(hcb!=null){
			Iterator<Map.Entry<String, List<Cfg_web_class_name>>> it = hcb.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String,List<Cfg_web_class_name>> entry = it.next();
				if(entry.getKey()!=null && entry.getValue().size()>1){
					new Cfg_web_class_nameMap(entry.getValue());
				}
			}
			// 保存成文件
			save(outputfile, hcb);
		}
	}
	
	/**
	 * 保存成文件
	 * */
	public static void save(String filename, HashMap<String,List<Cfg_web_class_name>> hcb){
		BufferedWriter writer = null;
		try{
			File writeFile = new File(filename);
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(writeFile), "GBK"));
			Iterator<Map.Entry<String, List<Cfg_web_class_name>>> it = hcb.entrySet().iterator();
			while (it.hasNext()) {			
				Map.Entry<String,List<Cfg_web_class_name>> entry = it.next();
				String _url = entry.getKey();
				// 剔除top列表
				if(!UrlList.isTop(_url)){
					for(Cfg_web_class_name cb : entry.getValue()){
						// web_name不为空
						if(!cb.getWeb_name().equals("null")){
							writer.write(cb.toString());
							writer.write("\r\n");
						}
					}
				}
			}
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
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
	 * 获得一级域名
	 * */
	public static String GetFirstDomain(String url, String host){
		GetFirstDomain gfd = new GetFirstDomain();
		String _url = url;
		if(url.startsWith("."))_url = "a" + url;
		String result = gfd.evaluate(_url, host);
		if(result==null || result.trim().length()==0 || result.equals("null"))result = url;		
		return result;
	}
	
	/**
	 * 获得二级域名
	 * */
	public static String GetSecondDomain(String url, String host){
		GetSecondDomain gfd = new GetSecondDomain();
		String result = gfd.evaluate(url, host);
		if(result==null || result.trim().length()==0 || result.equals("null"))result = url;		
		return result;
	}
}
