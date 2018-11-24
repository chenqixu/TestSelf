package com.newland.bi.bigdata.thread;

import java.util.Map;

import com.newland.bd.utils.log.IDistributedLogger;
import com.newland.storm.common.pub.bean.ComponentDevParam;
import com.newland.storm.common.pub.bean.ComponentInstanceInfo;
import com.newland.storm.component.etl.ftp.spout.IFileListManager;
import com.newland.storm.component.etl.ftp.spout.improve.FileTimeParser;
import com.newland.storm.component.etl.ftp.spout.source.ftp.FtpFileListManager;
import com.newland.storm.component.etl.ftp.spout.source.hdfs.HdfsFileListManager;

public class FileListManagerFactory {
	
	public static Builder getBuilder() {
		return new Builder();
	}
	
	public static class Builder {
		private IDistributedLogger distributeLogger;
		private Map<String, String> initParam;
		private FileTimeParser parser;
		private String etlSource;
		private String componentType;
		private ComponentInstanceInfo instanceInfo;
		private ComponentDevParam param;
		private IFileListManager fileListManager;// 文件信息来源

		public Builder setParam(ComponentDevParam param) {
			this.param = param;
			return this;
		}

		public Builder setInstanceInfo(ComponentInstanceInfo instanceInfo) {
			this.instanceInfo = instanceInfo;
			return this;
		}

		public Builder setComponentType(String componentType) {
			this.componentType = componentType;
			return this;
		}

		public Builder setEtlSource(String etlSource) {
			this.etlSource = etlSource;
			return this;
		}

		public Builder setDistributeLogger(IDistributedLogger distributeLogger) {
			this.distributeLogger = distributeLogger;
			return this;
		}

		public Builder setInitParam(Map<String, String> initParam) {
			this.initParam = initParam;
			return this;
		}

		public Builder setParser(FileTimeParser parser) {
			this.parser = parser;
			return this;
		}

		/**
		 * 构造文件列表扫描程序
		 * 这里只用到了createFileReader方法
		 * */
		public IFileListManager build() {
			try {
				if (etlSource != null || "hdfs".equals(componentType)) {
						fileListManager = new HdfsFileListManager(distributeLogger, initParam, parser);
				} else {
					fileListManager = new FtpFileListManager(distributeLogger, param, parser, instanceInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return fileListManager;
		}
	}
}
