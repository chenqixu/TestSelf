package com.newland.bi.bigdata.collect;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CollectTest1 {
	public static void main(String[] args) {
		List a;
		Set b;		
		HashSet c;
		TreeSet d;
		d = new TreeSet();
		d.add("a");
		d.add("a");
		d.add("a");
		Iterator it = d.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
	}
}
