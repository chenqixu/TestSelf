package com.newland.bi.bigdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ComparatorTest {
	public static void main(String[] args) {
		List<TestBean> list = new ArrayList<TestBean>();
		ComparatorTestBean ctb = new ComparatorTestBean();
		list.add(new TestBean("333"));
		list.add(new TestBean("111"));
		list.add(new TestBean("222"));
		Collections.sort(list, ctb);
		for(TestBean tb : list){
			System.out.println(tb.getFlux_data());
		}
	}
}

class TestBean {
	private String flux_data;
	public TestBean(String _flux){
		this.flux_data = _flux;
	}
	public String getFlux_data() {
		return flux_data;
	}
	public void setFlux_data(String flux_data) {
		this.flux_data = flux_data;
	}	
}

class ComparatorTestBean implements Comparator<Object> {
	@Override
	public int compare(Object o1, Object o2) {
		TestBean n1 = (TestBean) o1;
		TestBean n2 = (TestBean) o2;
		Double d1 = 0.0;
		Double d2 = 0.0;
		try {
			d1 = Double.valueOf(n1.getFlux_data());
			d2 = Double.valueOf(n2.getFlux_data());
		} catch (Exception e) {
			System.out.println("TestBean Comparator ERR:"+e.toString());
			e.printStackTrace();
		}
		return d2.compareTo(d1);
	}
}
