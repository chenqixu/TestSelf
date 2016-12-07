
package com.newland.bi.bigdata.datacollector.filter;

import org.apache.commons.net.ftp.FTPFile;

import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.log4j.Logger;

import com.newland.bi.bigdata.datacollector.config.CollectorConfInfo;

public class DataColectorFTPFileFilter implements FTPFileFilter{
	//�ļ�������������
	private String fileNameInclude;
	public DataColectorFTPFileFilter(String fileNameInclude){
		this.fileNameInclude = fileNameInclude;
	}
	//��־��¼��
	private static Logger logger = Logger.getLogger(DataColectorFTPFileFilter.class);
	//�ļ���������ֻ���غ�׺Ϊctl���ļ�
	@Override
	public boolean accept(FTPFile ftpfile) {
		boolean returnFlag = false;
		try {
			//ftp�������ϵ��ļ���
			String fileName = ftpfile.getName();
			//�Ƿ��ų�ָ���ļ������ļ�
			if(CollectorConfInfo.ifExcludeSpecificFileName){
				//�ų�ָ���ļ������ļ�
				if(fileName.indexOf(CollectorConfInfo.excludeSpecificFileName) > -1) return false;
			}
			//�Ƿ���˳��ļ��������ض��ַ���
			if(CollectorConfInfo.ifFilterSpecificFileName){
				//���˳��ļ��������ض��ַ���
				if(fileName.indexOf(CollectorConfInfo.filterSpecificFileName) < 0) return false;
			}
			//�����Ҫ�����ض��ļ���
			if(CollectorConfInfo.ifFilterSpecificTimeFileName
					//�����ض��ļ�����Ϊ��
					&& fileNameInclude != null && !"".equals(fileNameInclude)
					//�������ض��ļ�����ȥ��
					&& fileName.indexOf(fileNameInclude) < 0){
				return false;
			}
			
			//Դ�ļ��Ƿ��п����ļ�
			if(CollectorConfInfo.ifHasCtlSourceFile ){
				if(CollectorConfInfo.ifDownloadCtlFile){
					//�����Ҫ�����ؿ����ļ�
					if(CollectorConfInfo.ifFirstDownloadCtlFile){
						//�˳������ļ���׺��ΪCTL���ļ�
						if(fileName.endsWith(CollectorConfInfo.ctlSourceFileSuffixName)) {
							if(CollectorConfInfo.ifCollectByPartition){
								//��Դ�ļ�����"_"�ָ�
								String[] fileNameArray = fileName.split(CollectorConfInfo.splitFileNameForDateTime);
								//ȡ�����ڻ��ֵ��ֶ�
								String fileNameNum = fileNameArray[CollectorConfInfo.locationToPartition];
								int num = Integer.parseInt(fileNameNum.substring(fileNameNum.length()-1));								
								if(num % CollectorConfInfo.partitions == CollectorConfInfo.collectPartion-1){
									returnFlag = true;
								}
							}else{
								returnFlag = true;
							}
						}
					}else{
						//�˳������ļ���׺��Ϊ�����ļ����õ�ֵ���ļ�
						if(fileName.endsWith(CollectorConfInfo.dataSourceFileSuffixName)) {
							if(CollectorConfInfo.ifCollectByPartition){
								//��Դ�ļ�����"_"�ָ�
								String[] fileNameArray = fileName.split(CollectorConfInfo.splitFileNameForDateTime);
								//ȡ�����ڻ��ֵ��ֶ�
								String fileNameNum = fileNameArray[CollectorConfInfo.locationToPartition];
								int num = Integer.parseInt(fileNameNum.substring(fileNameNum.length()-1));
								if(num % CollectorConfInfo.partitions == CollectorConfInfo.collectPartion-1){
									returnFlag = true;
								}
							}else{
								returnFlag = true;
							}
						}
					}
				}else{
					//�˳������ļ���׺��ΪCTL���ļ�
					if(fileName.endsWith(CollectorConfInfo.ctlSourceFileSuffixName)) {
						if(CollectorConfInfo.ifCollectByPartition){
							//��Դ�ļ�����"_"�ָ�
							String[] fileNameArray = fileName.split(CollectorConfInfo.splitFileNameForDateTime);
							//ȡ�����ڻ��ֵ��ֶ�
							String fileNameNum = fileNameArray[CollectorConfInfo.locationToPartition];
							int num = Integer.parseInt(fileNameNum.substring(fileNameNum.length()-1));
							if(num % CollectorConfInfo.partitions == CollectorConfInfo.collectPartion-1){
								returnFlag = true;
							}
						}else{
							returnFlag = true;
						}
					}
				}
			}else{
				//�˳������ļ���׺��Ϊ�����ļ����õ�ֵ���ļ�
				if(fileName.endsWith(CollectorConfInfo.dataSourceFileSuffixName)) {
					if(CollectorConfInfo.ifCollectByPartition){
						//��Դ�ļ�����"_"�ָ�
						String[] fileNameArray = fileName.split(CollectorConfInfo.splitFileNameForDateTime);
						//ȡ�����ڻ��ֵ��ֶ�
						String fileNameNum = fileNameArray[CollectorConfInfo.locationToPartition];
						int num = Integer.parseInt(fileNameNum.substring(fileNameNum.length()-1));
						if(num % CollectorConfInfo.partitions == CollectorConfInfo.collectPartion-1){
							returnFlag = true;
						}
					}else{
						returnFlag = true;
					}
				}
			}
		} catch (Exception e) {
			logger.info("%%%%%�ɼ�������˱����ļ���������",e);
		}
		return returnFlag;
	}

}
