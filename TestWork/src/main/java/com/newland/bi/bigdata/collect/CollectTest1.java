package com.newland.bi.bigdata.collect;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CollectTest1 {
	
	public static final String SPLIT_STR = ",";
	
	/**
	 * 去重
	 * @param list
	 * */
	public static void duplicateRemoval(String s){
		HashSet<String> hs = new HashSet<String>();
		String[] list = s.split(SPLIT_STR);
		for(String str: list){
			hs.add(str);
		}
		Iterator<String> it = hs.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
	}
	
	public static void main(String[] args) {
//		List a;
//		Set b;		
//		HashSet c;
//		TreeSet d;
//		d = new TreeSet();
//		d.add("a");
//		d.add("a");
//		d.add("a");
//		Iterator it = d.iterator();
//		while(it.hasNext()){
//			System.out.println(it.next());
//		}		
		
		String str = "数据导入,数据导入,数据导入,数据导入,数据导入,数据导出,数据导出,数据传送,数据传送,数据传送,数据传送,数据删除,数据删除,数据删除,调度,公共函数,预处理,采集,mr程序,mr程序,webservice服务,公共脚本,公共脚本";
		CollectTest1.duplicateRemoval(str);
	}
}
