package com.cqx.netty.bean;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class DiscardTest {
	
	private TaskPool taskPool = new TaskPool();;
	
	public DiscardTest() {
	}
	
	/**
	 * 按顺序写入
	 * @return
	 */
	protected ByteBuf writeByte() {
		ByteBuf buf = Unpooled.buffer(3);
		buf.writeInt(1);
		buf.writeLong(0l);
		buf.writeBytes(new byte[]{});
		return buf;
	}
	
	/**
	 * 按顺序读取
	 * @param msg
	 */
	protected void readByte(ByteBuf msg) {
//		System.out.println("msg："+msg);
//		System.out.println("msg.readableBytes()："+msg.readableBytes());		
		int first = msg.readInt();
		long second = msg.readLong();
		byte[] thrid = new byte[msg.readableBytes()];
		msg.readBytes(thrid);
		System.out.println("first："+first);
		System.out.println("second："+second);
		System.out.println("thrid："+new String(thrid));
	}
	
	public void testWriteAndRead() {
		readByte(writeByte());
	}
	
	public void putTool(String tasktemplateid, DataBean dataBean) {
		taskPool.heartBean(tasktemplateid, dataBean);
	}
	
	public void queryTool(String tasktemplateid) {
		System.out.println(taskPool.getMapHeartProgress(tasktemplateid));
	}
	
	public static void main(String[] args) {
		DiscardTest discardTest = new DiscardTest();
		discardTest.testWriteAndRead();
//		String tasktemplateid = "1234567";
//		discardTest.putTool(tasktemplateid, new DataBean("test1", 1, "20181109"));
//		discardTest.queryTool(tasktemplateid);
	}
}
