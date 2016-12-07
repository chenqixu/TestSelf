package com.newland.bi.bigdata.bean;

import java.util.Comparator;

public class ComparatorUserClassbean implements Comparator<Object> {
	private int flag = 0;//0:flux 1:time
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}

	@Override
	public int compare(Object o1, Object o2) {
		Double d1 = 0.0;
		Double d2 = 0.0;
		try{
			if(flag==0){
				d1 = Double.valueOf(((UserClassInterface)o1).getFlux());
				d2 = Double.valueOf(((UserClassInterface)o2).getFlux());
			}else{
				d1 = Double.valueOf(((UserClassInterface)o1).getVisit_time());
				d2 = Double.valueOf(((UserClassInterface)o2).getVisit_time());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return d2.compareTo(d1);
	}
}
