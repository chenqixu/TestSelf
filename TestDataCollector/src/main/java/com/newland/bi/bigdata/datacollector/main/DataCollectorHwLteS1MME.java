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
 * @description:文件采集主函数类
 * @author:xixg
 * @date:2014-02-19
 */
public class DataCollectorHwLteS1MME {
	//日志记录器
	private static Logger logger = Logger.getLogger(DataCollectorHwLteS1MME.class);
	
	public static void main(String[] args) {
		try {
			//初始化操作
			InitCollectorFile.initDataCollector();
			while(true){
				logger.info("#####采集程序即将开始第"+InitCollectorFile.currCycleNum+"次循环扫描源数据多个目录："+CollectorConfInfo.sourceDataPath);
				//文件采集的时候检测是否要退出
				//实例化退出检测文件
				File f = new File(CollectorConstant.EXIT_FILE_FULL_NAME);
				//检查全局变量程序退出标识是否为真，如为真，则程序退出
				if(InitCollectorFile.ifNeedExitFlag 
						//退出文件存在，则执行退出处理
						||f.exists()){
					//退出文件存在,执行退出事件
					CollectorFileCommon.exitHandle();
					if(f.exists())
						logger.info("#####采集程序进行第"+InitCollectorFile.currCycleNum+"次循环扫描目录时，检测到退出文件："+CollectorConstant.EXIT_FILE_FULL_NAME+" 存在，采集程序退出>>>>>>>>>>>>>>>>");
					if(InitCollectorFile.ifNeedExitFlag)
						logger.info("#####采集程序进行第"+InitCollectorFile.currCycleNum+"次循环扫描目录时，全局变量退出标志为真，采集程序退出>>>>>>>>>>>>>>>>");
					return;
				}
				//如果配置文件配置为SFTP采集
				if(CollectorConfInfo.ifConnectBySftp){
					SftpCollectFileCommon.sftpCollectDirsFileService();
					
				}else{//默认为FTP采集
					CollectorFileCommon.CollectorDirsFtpFileService();
				}
				//采集程序扫描目录次数增加1
				InitCollectorFile.currCycleNum++;
				//如果全局变量中还有采集线程在工作，则程序进入睡眠。
				while(InitCollectorFile.dataCollectorThreadList.size()>0){
					//清理不活动的僵死线程
					CollectorFileCommon.clearNoAliveThread(InitCollectorFile.dataCollectorThreadList);
					Thread.sleep(1000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
