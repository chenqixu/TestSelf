package com.newland.bi.bigdata.datacollector.main;

import java.io.File;

import org.apache.log4j.Logger;

import com.newland.bi.bigdata.datacollector.common.CollectorConstant;
import com.newland.bi.bigdata.datacollector.common.CollectorFileCommon;
import com.newland.bi.bigdata.datacollector.common.InitCollectorFile;
import com.newland.bi.bigdata.datacollector.common.SftpCollectFileCommon;
import com.newland.bi.bigdata.datacollector.config.CollectorConfInfo;
/**
 * 
 * @description:�ļ��ɼ���������
 * @author:xixg
 * @date:2014-02-19
 */
public class DataCollectorHWLte_Http {
	//��־��¼��
	private static Logger logger = Logger.getLogger(DataCollectorHWLte_Http.class);
	
	public static void main(String[] args) {
		try {
			//��ʼ������
			InitCollectorFile.initDataCollector();
			while(true){
				logger.info("#####�ɼ����򼴽���ʼ��"+InitCollectorFile.currCycleNum+"��ѭ��ɨ��Դ���ݶ��Ŀ¼��"+CollectorConfInfo.sourceDataPath);
				//�ļ��ɼ���ʱ�����Ƿ�Ҫ�˳�
				//ʵ�����˳�����ļ�
				File f = new File(CollectorConstant.EXIT_FILE_FULL_NAME);
				//���ȫ�ֱ��������˳���ʶ�Ƿ�Ϊ�棬��Ϊ�棬������˳�
				if(InitCollectorFile.ifNeedExitFlag 
						//�˳��ļ����ڣ���ִ���˳�����
						||f.exists()){
					//�˳��ļ�����,ִ���˳��¼�
					CollectorFileCommon.exitHandle();
					if(f.exists())
						logger.info("#####�ɼ�������е�"+InitCollectorFile.currCycleNum+"��ѭ��ɨ��Ŀ¼ʱ����⵽�˳��ļ���"+CollectorConstant.EXIT_FILE_FULL_NAME+" ���ڣ��ɼ������˳�>>>>>>>>>>>>>>>>");
					if(InitCollectorFile.ifNeedExitFlag)
						logger.info("#####�ɼ�������е�"+InitCollectorFile.currCycleNum+"��ѭ��ɨ��Ŀ¼ʱ��ȫ�ֱ����˳���־Ϊ�棬�ɼ������˳�>>>>>>>>>>>>>>>>");
					return;
				}
				//��������ļ�����ΪSFTP�ɼ�
				if(CollectorConfInfo.ifConnectBySftp){
					SftpCollectFileCommon.sftpCollectDirsFileService();
					
				}else{//Ĭ��ΪFTP�ɼ�
					CollectorFileCommon.CollectorDirsFtpFileService();
				}
				//�ɼ�����ɨ��Ŀ¼��������1
				InitCollectorFile.currCycleNum++;
				//���ȫ�ֱ����л��вɼ��߳��ڹ�������������˯�ߡ�
				while(InitCollectorFile.dataCollectorThreadList.size()>0){
					//������Ľ����߳�
					CollectorFileCommon.clearNoAliveThread(InitCollectorFile.dataCollectorThreadList);
					Thread.sleep(1000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
