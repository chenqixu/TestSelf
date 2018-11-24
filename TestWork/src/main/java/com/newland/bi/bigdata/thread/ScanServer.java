package com.newland.bi.bigdata.thread;

import java.util.ArrayList;
import java.util.Collection;

import com.newland.storm.component.etl.common.model.impl.ExtractFileInfo;

public class ScanServer {
	
	public static int count = 0;
	
	public ScanServer() {
	}
	
	public static ExtractFileInfo getExtractFileInfo() {
		ExtractFileInfo e = new ExtractFileInfo();
		e.setCheckBakPath("/test/cqx/checkback");
		e.setCheckFileName("test.chk");
		e.setSourceBakPath("/test/cqx/sourceback");
		e.setSourceCheckPath("/test/cqx/check");
		e.setFileName("test.log");
		e.setFtpEnvVarName("ftp_10.46.61.159_aig");
		e.setSourcePath("/test/cqx/source");
		e.setSourceMachine("10.1.8.81");
		e.setExtractMachine("10.1.4.185");
		e.setFileSize(1000);
		e.setCreateTime("2018-09-30 17:15:00");
		e.setFileTime("2018-09-30 17:00:00");
		return e;
	}
	
	public static Collection<ExtractFileInfo> queryFtpFileListByParams(String name){
		if(count>0)return null;
		Collection<ExtractFileInfo> list;
		list = new ArrayList<ExtractFileInfo>();
		ExtractFileInfo e = new ExtractFileInfo();
		e.setCheckBakPath("/test/cqx/checkback");
		e.setCheckFileName("test.chk");
		e.setSourceBakPath("/test/cqx/sourceback");
		e.setSourceCheckPath("/test/cqx/check");
		e.setFileName("test.log");
		e.setFtpEnvVarName("ftp_10.46.61.159_aig");
		e.setSourcePath("/test/cqx/source");
		e.setSourceMachine("10.1.8.81");
		e.setExtractMachine("10.1.4.185");
		e.setFileSize(1000);
		e.setCreateTime("2018-09-30 17:15:00");
		e.setFileTime("2018-09-30 17:00:00");
		list.add(e);
		count++;
		return list;
	}	
}
