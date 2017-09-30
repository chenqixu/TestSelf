package com.gnxdr.bean;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

/**
 * Writable�ӿڴ�ҿ��ܶ�֪��������һ��ʵ�������л�Э������л�����
 * <br>��Hadoop�ж���һ���ṹ������Ҫʵ��Writable�ӿڣ�
 * <br>ʹ�øýṹ������������л�Ϊ�ֽ������ֽ���Ҳ���Է����л�Ϊ�ṹ������
 * <br>��WritableComparable�ӿ��ǿ����л����ҿɱȽϵĽӿڡ�
 * <br>MapReduce�����е�keyֵ���Ͷ�����ʵ������ӿڣ�
 * <br>��Ȼ�ǿ����л����Ǿͱ����ʵ��readFiels()��write()���������л��ͷ����л�������
 * <br>��ȻҲ�ǿɱȽϵ��Ǿͱ����ʵ��compareTo()�������ú������ǱȽϺ���������ʵ�֡�
 * <br>����MR�е�keyֵ�ͼ��ܿ����л����ǿɱȽϵġ�
 * */
public class CombineEntity implements WritableComparable<CombineEntity> {
	private Text joinKey;	//����key
	private Text flag;		//�������ͱ�ʶ
	private Text content;	//ֵ

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
