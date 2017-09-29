package com.gnxdr.bean;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

/**
 * Writable接口大家可能都知道，它是一个实现了序列化协议的序列化对象。
 * <br>在Hadoop中定义一个结构化对象都要实现Writable接口，
 * <br>使得该结构化对象可以序列化为字节流，字节流也可以反序列化为结构化对象。
 * <br>那WritableComparable接口是可序列化并且可比较的接口。
 * <br>MapReduce中所有的key值类型都必须实现这个接口，
 * <br>既然是可序列化的那就必须得实现readFiels()和write()这两个序列化和反序列化函数，
 * <br>既然也是可比较的那就必须得实现compareTo()函数，该函数即是比较和排序规则的实现。
 * <br>这样MR中的key值就既能可序列化又是可比较的。
 * */
public class CombineEntity implements WritableComparable<CombineEntity> {
	private Text joinKey;	//关联key
	private Text flag;		//数据类型标识
	private Text content;	//值

	public CombineEntity() {
		this.joinKey = new Text();
		this.flag = new Text();
		this.content = new Text();
	}
	
	public Text getJoinKey() {
		return joinKey;
	}
	public void setJoinKey(Text joinKey) {
		this.joinKey = joinKey;
	}
	public Text getFlag() {
		return flag;
	}
	public void setFlag(Text flag) {
		this.flag = flag;
	}
	public Text getContent() {
		return content;
	}
	public void setContent(Text content) {
		this.content = content;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.joinKey.readFields(in);
		this.flag.readFields(in);
		this.content.readFields(in);
		
	}

	@Override
	public void write(DataOutput out) throws IOException {
		this.joinKey.write(out);
		this.flag.write(out);
		this.content.write(out);
	}

	@Override
	public int compareTo(CombineEntity o) {
		return this.joinKey.compareTo(o.joinKey);
	}
}
