package com.bussiness.bi.bigdata.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cqx.process.DistributedLogInfoFactory;
import com.newland.bd.utils.log.IDistributedLogger;
import com.bussiness.bi.bigdata.utils.CollectionUtils;
import com.newland.edc.config.client.GlobalEnvConfig;
import com.newland.storm.common.pub.bean.ComponentDevParam;
import com.newland.storm.common.pub.bean.ComponentInstanceInfo;
import com.newland.storm.common.pub.bean.StepInfo;
import com.newland.storm.component.etl.ftp.spout.improve.FileTimeParser;

/**
 * 采集测试主线程
 * */
public class CollectionMain {
	
	private static Map<String, CollectionRunnable> threadlist = new HashMap<String, CollectionRunnable>();	
	
	public static Map<String, CollectionRunnable> getThreadlist() {
		return threadlist;
	}

	public static void add(List<String> list, Class<? extends CollectionRunnable> cs, ExecutorService es) {
		for(String str : list){
			CollectionRunnable my = CollectionUtils.generator(str, cs); 
			es.execute(my);
			threadlist.put(cs.getSimpleName(), my);
		}
	}
	
	static class Attribute {
		int dateFileMergeInterval;

		public int getDateFileMergeInterval() {
			return dateFileMergeInterval;
		}		
	}
	
	public static void main(String[] args) {
//		int dateFileMergeInterval = new Attribute().getDateFileMergeInterval();
//		System.out.println(dateFileMergeInterval+"");
//		System.exit(0);
		GlobalEnvConfig.setEnvUrl("http://10.1.4.186:18060/services/env/");
		//初始化工具类
		ComponentDevParam param = new ComponentDevParam();
		Map<String, List<Map<String, String>>> componentGroupsParamMap = new HashMap<String, List<Map<String, String>>>();
		List<Map<String, String>> list1 = new ArrayList<Map<String, String>>();
		Map<String, String> list1map = new HashMap<String, String>();
		list1map.put("etl_source", "ftp_10.46.61.159_aig");// 源服务器配置名列表
		list1map.put("datafilepath", "/test/cqx/data");// 数据文件路径
		list1map.put("checkfilepath", "/test/cqx/check");// 校验文件路径
		list1map.put("sourcefilebakpath", "d:/tmp/data/sourcebak");// 源文件备份目录
		list1map.put("checkfilebakpath", "d:/tmp/data/checkbak");// 校验文件备份目录
		list1map.put("errorfilebakpath", "false");// 异常文件是备份或则删除
		list1.add(list1map);
		componentGroupsParamMap.put("group_scan", list1);
		param.setGroupsParamMap(componentGroupsParamMap);		
		Map<String, String> componentInitParam = new HashMap<String, String>();
		componentInitParam.put("has_check_file", "false");
		componentInitParam.put("redis_cluster", "test_redis");
		componentInitParam.put("reader-type", "text");
		componentInitParam.put("charset", "utf-8");
		componentInitParam.put("line_split", "\n");
		param.setComponentInitParam(componentInitParam);
		IDistributedLogger distributeLogger = new DistributedLogInfoFactory();
		FileTimeParser parser = new FileTimeParser();
		ComponentInstanceInfo instanceInfo = new ComponentInstanceInfo();
		StepInfo info = new StepInfo();
		info.setTaskId("100833124645@2018010303000002");
		info.setParallelism(1);
		instanceInfo.setInfo(info);
		instanceInfo.setComponentIndex(1);
		instanceInfo.setComponentId("112233");
		//初始化FileListManager
		CollectionUtils.getInstance().createFileListManager("ftp", distributeLogger, param, parser, instanceInfo);
		//初始化LockServer
		CollectionUtils.getInstance().createLockServer(distributeLogger, instanceInfo, param);
		//初始化参数
		CollectionUtils.getInstance().setParam(param);
		//启动采集线程（传入源端服务器）
		//启动获取扫描文件线程（传入源端服务器）
		//一台源端服务器可能有二个或二个以上的线程，所以采集和获取扫描还是分开
		List<String> ftpsourcelist = new ArrayList<String>();
		ftpsourcelist.add("10.1.8.81");
		ExecutorService executor = Executors.newFixedThreadPool(5);
		add(ftpsourcelist, CollectionQueueThread.class, executor);
		add(ftpsourcelist, CollectionThread.class, executor);
		add(ftpsourcelist, CollectionReadThread.class, executor);
//		OtherUtils.sleep(10000);
//		executor.shutdown();
	}
}
