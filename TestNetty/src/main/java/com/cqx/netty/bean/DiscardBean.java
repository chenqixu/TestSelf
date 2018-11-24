package com.cqx.netty.bean;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class DiscardBean {
	/**
	 * 包类型 1：心跳包 2：队列数据包 3：查询包
	 */
	private int packageType;
	private long tasktemplateid;
	private DataBean dataBean;

	public DiscardBean() {
	}

	public DiscardBean(int packageType, long tasktemplateid, DataBean dataBean) {
		this.packageType = packageType;
		this.tasktemplateid = tasktemplateid;
		this.dataBean = dataBean;
	}

	public DiscardBean(ByteBuf byteBuf) {
		if (byteBuf != null) {
			this.packageType = byteBuf.readInt();
			this.tasktemplateid = byteBuf.readLong();
			byte[] dataBean = new byte[byteBuf.readableBytes()];
			byteBuf.readBytes(dataBean);
			this.dataBean = DataBean.deserializableFromString(new String(
					dataBean));
		}
	}

	public static DataBean buildDataBean(String threadName, int value,
			String date) {
		return new DataBean(threadName, value, date);
	}

	public static DataBean buildNullDataBean() {
		return new DataBean("", 0, "");
	}

	public long getTasktemplateid() {
		return tasktemplateid;
	}

	public void setTasktemplateid(long tasktemplateid) {
		this.tasktemplateid = tasktemplateid;
	}

	public int getPackageType() {
		return packageType;
	}

	public void setPackageType(int packageType) {
		this.packageType = packageType;
	}

	public DataBean getDataBean() {
		return dataBean;
	}

	public void setDataBean(DataBean dataBean) {
		this.dataBean = dataBean;
	}

	public ByteBuf getMsg() {
		ByteBuf buf = Unpooled.buffer(3);
		buf.writeInt(packageType);
		buf.writeLong(tasktemplateid);
		buf.writeBytes(dataBean.serializableToString().getBytes());
		return buf;
	}

	@Override
	public String toString() {
		return "packageType：" + packageType + "，tasktemplateid："
				+ tasktemplateid + "，dataBean："
				+ dataBean.serializableToString();
	}

}
