package com.mr.util;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class TempKeyPartitioner extends Partitioner<TempKey, Text>{

	@Override
	public int getPartition(TempKey tempKey, Text text, int numPartitioners) {
	//��firsKey���з���
	return Math.abs(tempKey.getFirstKey().hashCode())%numPartitioners;
	}
}
