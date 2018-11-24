package com.newland.bi.bigdata.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cqx.process.DistributedLogInfoFactory;
import com.newland.bd.utils.log.IDistributedLogger;
import com.newland.bi.bigdata.utils.CollectionUtils;
import com.newland.edc.config.client.GlobalEnvConfig;
import com.newland.storm.common.pub.bean.ComponentDevParam;
import com.newland.storm.common.pub.bean.ComponentInstanceInfo;
import com.newland.storm.common.pub.bean.StepInfo;
import com.newland.storm.component.etl.common.model.impl.ExtractFileInfo;
import com.newland.storm.component.etl.ftp.spout.IFileListManager;
import com.newland.storm.component.etl.ftp.spout.improve.FileTimeParser;

public class FtpTest {
	public static void init() {
		GlobalEnvConfig.setEnvUrl("http://10.1.4.186:18060/services/env/");
		ComponentDevParam param = new ComponentDevParam();
		Map<String, List<Map<String, String>>> componentGroupsParamMap = new HashMap<String, List<Map<String, String>>>();
		List<Map<String, String>> list1 = new ArrayList<Map<String, String>>();
		Map<String, String> list1map = new HashMap<String, String>();
		list1map.put("etl_source", "ftp_10.46.61.159_aig");
		list1map.put("datafilepath", "/test/cqx/data");
		list1map.put("checkfilepath", "/test/cqx/check");
		list1map.put("sourcefilebakpath", "d:/tmp/data/sourcebak");
		list1map.put("checkfilebakpath", "d:/tmp/data/checkbak");
		list1map.put("errorfilebakpath", "false");
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
		CollectionUtils.getInstance().createFileListManager("ftp", distributeLogger, param, parser, instanceInfo);
	}
	
	public static void delTest() {
		IFileListManager fileListManager = CollectionUtils.getInstance().getFileListManager();
		String filePath = "/test/cqx/source/test.log";
		ExtractFileInfo file = ScanServer.getExtractFileInfo();
		String uri = "ftp_10.46.61.159_aig";
		try {
			fileListManager.complateFile(filePath, file, uri);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		init();
		delTest();
		System.exit(0);
	}
}
