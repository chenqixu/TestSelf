
package com.newland.bi.bigdata.datacollector.filter;

import org.apache.commons.net.ftp.FTPFile;

import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.log4j.Logger;

import com.newland.bi.bigdata.datacollector.config.CollectorConfInfo;

public class DataColectorFTPFileFilter implements FTPFileFilter{
	//文件名包含的内容
	private String fileNameInclude;
	public DataColectorFTPFileFilter(String fileNameInclude){
		this.fileNameInclude = fileNameInclude;
	}
	//日志记录器
	private static Logger logger = Logger.getLogger(DataColectorFTPFileFilter.class);
	//文件过滤器，只返回后缀为ctl的文件
	@Override
	public boolean accept(FTPFile ftpfile) {
		boolean returnFlag = false;
		try {
			//ftp服务器上的文件名
			String fileName = ftpfile.getName();
			//是否排除指定文件名的文件
			if(CollectorConfInfo.ifExcludeSpecificFileName){
				//排除指定文件名的文件
				if(fileName.indexOf(CollectorConfInfo.excludeSpecificFileName) > -1) return false;
			}
			//是否过滤出文件名包含特定字符串
			if(CollectorConfInfo.ifFilterSpecificFileName){
				//过滤出文件名包含特定字符串
				if(fileName.indexOf(CollectorConfInfo.filterSpecificFileName) < 0) return false;
			}
			//如果需要过滤特定文件名
			if(CollectorConfInfo.ifFilterSpecificTimeFileName
					//过滤特定文件名不为空
					&& fileNameInclude != null && !"".equals(fileNameInclude)
					//不包含特定文件名的去除
					&& fileName.indexOf(fileNameInclude) < 0){
				return false;
			}
			
			//源文件是否有控制文件
			if(CollectorConfInfo.ifHasCtlSourceFile ){
				if(CollectorConfInfo.ifDownloadCtlFile){
					//如果需要先下载控制文件
					if(CollectorConfInfo.ifFirstDownloadCtlFile){
						//滤出控制文件后缀名为CTL的文件
						if(fileName.endsWith(CollectorConfInfo.ctlSourceFileSuffixName)) {
							if(CollectorConfInfo.ifCollectByPartition){
								//对源文件名以"_"分隔
								String[] fileNameArray = fileName.split(CollectorConfInfo.splitFileNameForDateTime);
								//取出用于划分的字段
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
						//滤出数据文件后缀名为配置文件配置的值的文件
						if(fileName.endsWith(CollectorConfInfo.dataSourceFileSuffixName)) {
							if(CollectorConfInfo.ifCollectByPartition){
								//对源文件名以"_"分隔
								String[] fileNameArray = fileName.split(CollectorConfInfo.splitFileNameForDateTime);
								//取出用于划分的字段
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
					//滤出控制文件后缀名为CTL的文件
					if(fileName.endsWith(CollectorConfInfo.ctlSourceFileSuffixName)) {
						if(CollectorConfInfo.ifCollectByPartition){
							//对源文件名以"_"分隔
							String[] fileNameArray = fileName.split(CollectorConfInfo.splitFileNameForDateTime);
							//取出用于划分的字段
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
				//滤出数据文件后缀名为配置文件配置的值的文件
				if(fileName.endsWith(CollectorConfInfo.dataSourceFileSuffixName)) {
					if(CollectorConfInfo.ifCollectByPartition){
						//对源文件名以"_"分隔
						String[] fileNameArray = fileName.split(CollectorConfInfo.splitFileNameForDateTime);
						//取出用于划分的字段
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
			logger.info("%%%%%采集程序过滤本地文件出错！！！",e);
		}
		return returnFlag;
	}

}
