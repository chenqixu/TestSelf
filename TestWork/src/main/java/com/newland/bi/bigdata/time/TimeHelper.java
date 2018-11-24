package com.newland.bi.bigdata.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 时间工具类
 * 
 * @author chenqixu
 *
 */
public class TimeHelper {

	protected static final Logger log = LoggerFactory
			.getLogger(TimeHelper.class);
	protected static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyyMMddHH");

	/**
	 * 时间比较
	 * 
	 * <pre>
	 * 	time1小于time2 -1
	 * 	time1等于time2 0
	 * 	time1大于time2 1
	 * </pre>
	 * 
	 * @param time1
	 * @param time2
	 * @return 文件周期
	 */
	public static int timeComparison(String time1, String time2) {
		try {
			long l1 = simpleDateFormat.parse(time1).getTime();
			long l2 = simpleDateFormat.parse(time2).getTime();
			return (l1 - l2 > 0) ? 1 : (l1 - l2 < 0 ? -1 : 0);
		} catch (ParseException e) {
			log.error("★★★ 时间比较异常，时间1 {}，时间2 {}", time1, time2);
			log.error("★★★ 具体错误信息：{}", e.getMessage(), e);
			// e.printStackTrace();
		}
		return 2;
	}
}
