package com.mr.util;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class GroupingCompare extends WritableComparator{
	/**
	 * @description: ����Ĺ�������������ø���Ĺ�����
	 */
	protected GroupingCompare() {
		//ע��comparator
		super(TempKey.class,true);
	}
	
	/**
	 * @description: ���������ԣ��Ƚ�firstKey��ͨ��firstKey������
	 */
	@SuppressWarnings("unchecked")
	public int compare(WritableComparable a, WritableComparable b) {
		TempKey tempA = (TempKey)a;
		TempKey tempB  = (TempKey)b;
		return tempA.getFirstKey().compareTo(tempB.getFirstKey());
	}

}
