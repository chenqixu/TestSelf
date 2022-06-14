package com.bussiness.bi.bigdata.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置类
 * 
 * @author chenqixu
 *
 */
public class BaseConfig {
	// 文件流类型
	public static final String PARAM_READER_TYPE = "newland.param_reader_type";
	// 每次nextTuple返回多少条记录。 //如果发生跨机器这个值不宜设置过大默认1000
	public final static String BATCH_SIZE = "newland.batch_size";
	// 下游队列处理上限
	public final static String DEAL_SIZE = "newland.deal_size";
	// 单个文件处理超时时间，如果某个task 5分钟也没有反馈一个文件处理的心跳，可以先用其他接管
	public final static String DEFAULT_LOCK_TIMEOUT = "newland.default_lock_timeout";
	// 文件队列大小
	public final static String COLLECTIONQUEUE_SIZE = "newland.collectionqueue_size";
	// 扫描过期文件间隔时间，默认配置2分钟扫描1次
	public final static String SCAN_EXPIREDLOCK_TIME = "newland.scan_expiredlock_time";

	private static Map<String, String> params = new HashMap<String, String>();

	static {
		params.put(PARAM_READER_TYPE, "reader-type");
		params.put(BATCH_SIZE, "1000");
		params.put(DEAL_SIZE, "10");
		params.put(DEFAULT_LOCK_TIMEOUT, 5 * 60 + "");
		params.put(COLLECTIONQUEUE_SIZE, "100");
		params.put(SCAN_EXPIREDLOCK_TIME, "120");
	}

	public static String getStrValue(String key) {
		return params.get(key);
	}

	public static int getIntValue(String key) {
		return Integer.valueOf(params.get(key));
	}

	public static void setStrValue(String key, String value) {
		params.put(key, value);
	}

	public static void setIntValue(String key, int value) {
		params.put(key, String.valueOf(value));
	}
}
