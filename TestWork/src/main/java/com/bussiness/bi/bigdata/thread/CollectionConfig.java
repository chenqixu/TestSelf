package com.bussiness.bi.bigdata.thread;

import com.newland.storm.common.pub.bean.ComponentDevParam;

public class CollectionConfig {
	private ComponentDevParam param;
	public static final String PARAM_READER_TYPE = "reader-type";
//	public static int BATCH_SIZE = 1000;// 每次nextTuple返回多少条记录。 //如果发生跨机器这个值不宜设置过大默认1000
	public static int BATCH_SIZE = 3;// 每次nextTuple返回多少条记录。 //用于测试
	public static int DEAL_SIZE = 10;// 下游队列处理上限
	public static int DEFAULT_LOCK_TIMEOUT = 5*60;// 单个文件处理超时时间，如果某个task 5分钟也没有反馈一个文件处理的心跳，可以先用其他接管
	
	public ComponentDevParam getParam() {
		return param;
	}
	public void setParam(ComponentDevParam param) {
		this.param = param;
	}
	
}
