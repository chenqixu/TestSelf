package com.newland.bi.bigdata.datacollector.common;


import java.util.Comparator;


import org.apache.log4j.Logger;

import com.newland.bi.bigdata.datacollector.config.CollectorConfInfo;

public class TimeOfFileComparator implements Comparator<String>{
	//��־��¼��
	private static Logger logger = Logger.getLogger(TimeOfFileComparator.class);
	/**
	 * 
	 * @description:��д�Ƚ���,���ļ�����ʱ������
	 * �ļ���ʵ����GnC64_http_dnssession_60_20131218_105700_20131218_105759.csv
	 * 						GnC64_http_netsession_60_20131218_105700_20131218_105759.ctl
	 * 						ȡ��ʱ�䴮 20131218_105700 ��Ϊ����
	 * @author:xixg
	 * @date:2013-11-23
	 * @param inFileNameMap map�д�ŵ��ļ���
	 * @return 
	 */
	@Override
	public int compare(String fileNameA, String fileNameB) {
		int compareResult = 0;
		StringBuffer sbA = new StringBuffer();
		StringBuffer sbB = new StringBuffer();
		try {
			//��Դ�ļ�����"_"�ָ���ȡ��ʱ�䴮
			String[] fileNameArrayA = fileNameA.split(CollectorConfInfo.splitFileNameForDateTime);
			//ȡ�ļ������ڣ��磺20131218
			String fileNameDateA = fileNameArrayA[CollectorConfInfo.dateLocationAtFileName];
			//��ȡ��Ҫ��ʱ���ַ���
			if(fileNameDateA != null && fileNameDateA.length()>= CollectorConfInfo.dateSubStringEnd)
				fileNameDateA = fileNameDateA.substring(CollectorConfInfo.dateSubStringBegin, CollectorConfInfo.dateSubStringEnd);
			//ȡ�ļ���Сʱ����ӣ��磺105600
			String fileNameHourTimeA = fileNameArrayA[CollectorConfInfo.hourLocationAtFileName];
			//��ȡ��Ҫ��Сʱ�ַ���
			if(fileNameHourTimeA != null && fileNameHourTimeA.length()>= CollectorConfInfo.hourSubStringEnd)
				fileNameHourTimeA = fileNameHourTimeA.substring(CollectorConfInfo.hourSubStringBegin, CollectorConfInfo.hourSubStringEnd);
			sbA.append(fileNameDateA);
			sbA.append(CollectorConfInfo.splitFileNameForDateTime);
			sbA.append(fileNameHourTimeA);
			String fileNameATime = sbA.toString();
			
			//��Դ�ļ�����"_"�ָ���ȡ��ʱ�䴮
			String[] fileNameArrayB = fileNameB.split(CollectorConfInfo.splitFileNameForDateTime);
			//ȡ�ļ������ڣ��磺20131218
			String fileNameDateB = fileNameArrayB[CollectorConfInfo.dateLocationAtFileName];
			//��ȡ��Ҫ��ʱ���ַ���
			if(fileNameDateB != null && fileNameDateB.length()>= CollectorConfInfo.dateSubStringEnd)
				fileNameDateB = fileNameDateB.substring(CollectorConfInfo.dateSubStringBegin, CollectorConfInfo.dateSubStringEnd);
			//ȡ�ļ���Сʱ����ӣ��磺105600
			String fileNameHourTimeB = fileNameArrayB[CollectorConfInfo.hourLocationAtFileName];
			//��ȡ��Ҫ��Сʱ�ַ���
			if(fileNameHourTimeB != null && fileNameHourTimeB.length()>= CollectorConfInfo.hourSubStringEnd)
				fileNameHourTimeB = fileNameHourTimeB.substring(CollectorConfInfo.hourSubStringBegin, CollectorConfInfo.hourSubStringEnd);
			sbB.append(fileNameDateB);
			sbB.append(CollectorConfInfo.splitFileNameForDateTime);
			sbB.append(fileNameHourTimeB);
			String fileNameBTime = sbB.toString();
			
			compareResult = fileNameATime.compareTo(fileNameBTime);
		} catch (Exception e) {
			logger.error("%%%%%�ļ���ʱ���Ƚ�����������",e);
		}
		return compareResult;
	}
	

}
