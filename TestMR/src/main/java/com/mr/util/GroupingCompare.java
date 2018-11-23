package com.mr.util;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class GroupingCompare extends WritableComparator{
	/**
	 * @description: 本类的构造器，必须调用父类的构造器
	 */
	protected GroupingCompare() {
		//注册comparator
		super(TempKey.class,true);
	}
	
	/**
	 * @description: 定义分组策略，比较firstKey，通过firstKey来分组
	 */
	@SuppressWarnings("unchecked")
	public int compare(WritableComparable a, WritableComparable b) {
		TempKey tempA = (TempKey)a;
		TempKey tempB  = (TempKey)b;
		return tempA.getFirstKey().compareTo(tempB.getFirstKey());
	}

}
