package com.cqx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class PropertyUtil {
	public final static String spltstr = File.separator;//文件路径分隔符(区分windows和linux)
	public final static String rootPath = System.getProperty("user.dir");
	public final static String commonpath = rootPath+spltstr+"config"+spltstr+"common.properties";
	//PropertyUtil.class.getResource("/config/application_360.properties").getFile();
	
	public static void printPath(){
		System.out.println("#rootPath#"+rootPath);
	}
	
	public static String getProperty(String name) {
		String result = "";
		File f = null;
		FileInputStream pInStream = null;
		Properties p = null;
		try{
			f = new File(PropertyUtil.commonpath);
			pInStream = new FileInputStream(f);
			p = new Properties();
			p.load(pInStream);
			Enumeration<?> enuVersion = p.propertyNames();
			while(enuVersion.hasMoreElements()){
				enuVersion.nextElement();
			}
			result = p.getProperty(name);
			f = null;
			pInStream.close();
			p.clear();
			p = null;
		}catch(FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(f!=null)
				f = null;
			if(pInStream!=null)
				try {
					pInStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(p!=null){
				p.clear();
				p = null;
			}
		}
		return result;
	}
}
