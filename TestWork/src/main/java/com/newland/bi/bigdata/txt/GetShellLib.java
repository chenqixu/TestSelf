package com.newland.bi.bigdata.txt;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.newland.bi.bigdata.changecode.ChangeCode;

public class GetShellLib extends ChangeCode {
	
	public static void main(String[] args) {
		HashMap<String, String> hm = new HashMap<String, String>();
		HashMap<String, String> parent = new HashMap<String, String>();
		String scanpath = "j:/Work/CVS/BI/SSC/BIGDATA2.0/Develop/SourceCode/Code/edc-bigdata/";
		String scan_rule = "[A-Za-z0-9\\-\\.]+.jar";
//		String scan_rule = "lib";
		GetShellLib gsl = new GetShellLib();
		gsl.setLoop(true);
		gsl.setScan_path(scanpath);
		gsl.setScan_rule(scan_rule);
		for(String filename: gsl.Scan(scanpath, scan_rule)){
			File _tmpFile = new File(filename);
			String _name = _tmpFile.getName();
			String _path = _tmpFile.getParent();
//			System.out.println(_path);
			parent.put(_path, "");
			if(hm.get(_name)!=null){
				hm.put(_name, String.valueOf(Integer.valueOf(hm.get(_name))+1));
			}else{
				hm.put(_name, "1");
			}			
		}
		System.out.println(parent.size());
//		for(Map.Entry<String, String> en : parent.entrySet()){
//			System.out.println(en.getKey());
//		}
		for(Map.Entry<String, String> en : hm.entrySet()){
			System.out.println(en);
			if(Integer.valueOf(en.getValue())==40){
				System.out.println(en.getKey());
			}
		}
	}
}
