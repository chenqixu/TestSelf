package com.newland.bi.bigdata.collect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashMapSer {
	
	public void likeMap(String key) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("a", "123");
		map.put("b", "234");
//		map.put("a_append", "123");
		
		for(Map.Entry<String, String> _tmp : map.entrySet()) {
			if(_tmp.getKey().contains(key)) {
				System.out.println(_tmp.getValue());
				break;
			}
		}		
	}
	
	public void listTest() {
		List<String> list = new ArrayList<String>();
		list.add("123");
		list.add("234");
		list.add("456");
		System.out.println(list);
	}
	
	public static void main(String[] args) {		
		new HashMapSer().likeMap("a");
	}
}
