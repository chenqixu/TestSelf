package com.mr.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class TempKey implements WritableComparable<Object>{

	//第一个键手机号码，用于数据分组
	private String firstKey;
	//第二个键时间，用于数据排序
	private String secondKey;
	
	@Override
	public void readFields(DataInput dataInput) throws IOException {
		try {
			System.out.println("dataInput.toString():"+dataInput.toString());
			//以UTF编码读入firstKey
			firstKey = dataInput.readUTF();
			System.out.println("firstKey dataInput.readUTF():"+firstKey);
			//以UTF编码读入secondKey
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
			//以UTF编码输出firstKey
			dataOutput.writeUTF(firstKey);
			//以UTF编码输出secondKey
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
		//利用这个来控制升序或者降序
		//this本对象写在前面代表是升序
		//thsi本对象写在后面代表是降序
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
