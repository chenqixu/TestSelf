package com.mr.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class TempKey implements WritableComparable<Object>{

	//��һ�����ֻ����룬�������ݷ���
	private String firstKey;
	//�ڶ�����ʱ�䣬������������
	private String secondKey;
	
	@Override
	public void readFields(DataInput dataInput) throws IOException {
		try {
			System.out.println("dataInput.toString():"+dataInput.toString());
			//��UTF�������firstKey
			firstKey = dataInput.readUTF();
			System.out.println("firstKey dataInput.readUTF():"+firstKey);
			//��UTF�������secondKey
			secondKey = dataInput.readUTF();
			System.out.println("secondKey dataInput.readUTF():"+secondKey);
		} catch (Exception e) {
			System.out.println("%%%%%CombineKey---readFields() firstKey="+firstKey);
			System.out.println("%%%%%CombineKey---readFields() secondKey="+secondKey);
			e.printStackTrace();
		}
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		try {
			//��UTF�������firstKey
			dataOutput.writeUTF(firstKey);
			//��UTF�������secondKey
			dataOutput.writeUTF(secondKey);
		} catch (Exception e) {
			System.out.println("%%%%%CombineKey---write() firstKey="+firstKey);
			System.out.println("%%%%%CombineKey---write() secondKey="+secondKey);
			e.printStackTrace();
		}
	}

	@Override
	public int compareTo(Object obj) {
		TempKey combineKye = (TempKey) obj;
		//�������������������߽���
		//this������д��ǰ�����������
		//thsi������д�ں�������ǽ���
		if(!this.getFirstKey().equals(combineKye.getFirstKey())){
			return this.getFirstKey().compareTo(combineKye.getFirstKey());
		}else{
			return this.getSecondKey().compareTo(combineKye.getSecondKey());
		}
		
	}
	
	public String getFirstKey() {
		return firstKey;
	}

	public void setFirstKey(String firstKey) {
		this.firstKey = firstKey;
	}

	public String getSecondKey() {
		return secondKey;
	}

	public void setSecondKey(String secondKey) {
		this.secondKey = secondKey;
	}
}
