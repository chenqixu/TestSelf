package com.bussiness.bi.bigdata.hdfs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.io.Text;

public class MRTest {
	public static void main(String[] args) {
		Text list1 = new Text("1234");
		Text list2 = new Text("abcd");
		Iterable<Text> values = null;
//		List<Text> a1 =  new ArrayList<Text>();
//		a1.add(list1);
//		a1.add(list2);
//		values = a1;
		Set<Text> s1 = new HashSet<Text>();
		s1.add(list1);
		s1.add(list2);
		values = s1;
		
		List<Text> a2 =  new ArrayList<Text>();
//		List<String> a2 =  new ArrayList<String>();
		for(Text v : values){
			a2.add(v);
//			a2.add(v.toString());
		}
		System.out.println(a2);
	}
}
