package com.newland.bi.bigdata.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class Cfg_web_class_nameMap {
	private List<Cfg_web_class_name> list = null;
	private String domain_level1_name;
	public String getDomain_level1_name() {
		return domain_level1_name;
	}
	public void setDomain_level1_name(String domain_level1_name) {
		this.domain_level1_name = domain_level1_name;
	}
	public Cfg_web_class_nameMap(List<Cfg_web_class_name> _list){
		this.list = _list;
		deal();
		for(Cfg_web_class_name cb : list){
			setDomain_level1(cb);
		}
	}
	private void deal(){
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		// 记数
		for(Cfg_web_class_name cb : list){
			if(hm.get(cb.getWeb_name())!=null){
				hm.put(cb.getWeb_name(), hm.get(cb.getWeb_name())+1);
			}else{
				hm.put(cb.getWeb_name(), 1);
			}
		}
		// 取最大值
		int max = 1;
		String maxWeb_name = "";
		for (Entry<String, Integer> entry : hm.entrySet()) {
			if(entry.getValue()>max){
				maxWeb_name = entry.getKey();
				max = entry.getValue();
			}
		}
		if(maxWeb_name.equals("")){// 取第一个
			for (Entry<String, Integer> entry : hm.entrySet()) {
				this.domain_level1_name = entry.getKey();
				if(this.domain_level1_name!=null) break;
			}
		}else{// 取出现次数最多
			this.domain_level1_name = maxWeb_name;
		}
//		Collection<Integer> c = hm.values();
//		Object[] obj = c.toArray();
//		Arrays.sort(obj);
//		System.out.println(obj[obj.length-1]);
	}
	private void setDomain_level1(Cfg_web_class_name cb){
		if(cb!=null){
			cb.setDomain_level1_name(this.domain_level1_name);
		}
	}
}
