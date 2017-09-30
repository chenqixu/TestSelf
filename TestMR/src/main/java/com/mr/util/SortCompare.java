package com.mr.util;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class SortCompare extends WritableComparator{
	protected SortCompare() {
		super(TempKey.class,true);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int compare(WritableComparable a, WritableComparable b) {
		int compareValue = 0;
		try {
			TempKey tempKeyA = (TempKey)a;
			TempKey tempKeyB  = (TempKey)b;
			//���ȱȽ�һ��key,��ͬ��Ƚϵڶ�key
			if(!tempKeyA.getFirstKey().equals(tempKeyB.getFirstKey()))
				compareValue = tempKeyA.getFirstKey().compareTo(tempKeyB.getFirstKey());
			else
				compareValue = tempKeyA.getSecondKey().compareTo(tempKeyB.getSecondKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return compareValue;
	}

}
