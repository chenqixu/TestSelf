package com.newland.bi.bigdata.datacollector.common;


import java.util.Comparator;


import org.apache.log4j.Logger;

import com.newland.bi.bigdata.datacollector.config.CollectorConfInfo;

public class TimeOfFileComparator implements Comparator<String>{
	//日志记录器
	private static Logger logger = Logger.getLogger(TimeOfFileComparator.class);
	/**
	 * 
	 * @description:重写比较器,以文件名的时间排序，
	 * 文件名实例：GnC64_http_dnssession_60_20131218_105700_20131218_105759.csv
	 * 						GnC64_http_netsession_60_20131218_105700_20131218_105759.ctl
	 * 						取出时间串 20131218_105700 作为排序
	 * @author:xixg
	 * @date:2013-11-23
	 * @param inFileNameMap map中存放的文件名
	 * @return 
	 */
	@Override
	public int compare(String fileNameA, String fileNameB) {
		int compareResult = 0;
		StringBuffer sbA = new StringBuffer();
		StringBuffer sbB = new StringBuffer();
		try {
			//对源文件名以"_"分隔，取出时间串
			String[] fileNameArrayA = fileNameA.split(CollectorConfInfo.splitFileNameForDateTime);
			//取文件名日期，如：20131218
			String fileNameDateA = fileNameArrayA[CollectorConfInfo.dateLocationAtFileName];
			//截取需要的时间字符串
			if(fileNameDateA != null && fileNameDateA.length()>= CollectorConfInfo.dateSubStringEnd)
				fileNameDateA = fileNameDateA.substring(CollectorConfInfo.dateSubStringBegin, CollectorConfInfo.dateSubStringEnd);
			//取文件名小时与分钟，如：105600
			String fileNameHourTimeA = fileNameArrayA[CollectorConfInfo.hourLocationAtFileName];
			//截取需要的小时字符串
			if(fileNameHourTimeA != null && fileNameHourTimeA.length()>= CollectorConfInfo.hourSubStringEnd)
				fileNameHourTimeA = fileNameHourTimeA.substring(CollectorConfInfo.hourSubStringBegin, CollectorConfInfo.hourSubStringEnd);
			sbA.append(fileNameDateA);
			sbA.append(CollectorConfInfo.splitFileNameForDateTime);
			sbA.append(fileNameHourTimeA);
			String fileNameATime = sbA.toString();
			
			//对源文件名以"_"分隔，取出时间串
			String[] fileNameArrayB = fileNameB.split(CollectorConfInfo.splitFileNameForDateTime);
			//取文件名日期，如：20131218
			String fileNameDateB = fileNameArrayB[CollectorConfInfo.dateLocationAtFileName];
			//截取需要的时间字符串
			if(fileNameDateB != null && fileNameDateB.length()>= CollectorConfInfo.dateSubStringEnd)
				fileNameDateB = fileNameDateB.substring(CollectorConfInfo.dateSubStringBegin, CollectorConfInfo.dateSubStringEnd);
			//取文件名小时与分钟，如：105600
			String fileNameHourTimeB = fileNameArrayB[CollectorConfInfo.hourLocationAtFileName];
			//截取需要的小时字符串
			if(fileNameHourTimeB != null && fileNameHourTimeB.length()>= CollectorConfInfo.hourSubStringEnd)
				fileNameHourTimeB = fileNameHourTimeB.substring(CollectorConfInfo.hourSubStringBegin, CollectorConfInfo.hourSubStringEnd);
			sbB.append(fileNameDateB);
			sbB.append(CollectorConfInfo.splitFileNameForDateTime);
			sbB.append(fileNameHourTimeB);
			String fileNameBTime = sbB.toString();
			
			compareResult = fileNameATime.compareTo(fileNameBTime);
		} catch (Exception e) {
			logger.error("%%%%%文件名时间点比较器出错！！！",e);
		}
		return compareResult;
	}
	

}
