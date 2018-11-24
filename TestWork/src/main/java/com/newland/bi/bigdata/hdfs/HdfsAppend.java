package com.newland.bi.bigdata.hdfs;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;

public class HdfsAppend {
	private Configuration hdfsConfig;
	private FileSystem fs;
	
	public HdfsAppend(String conf) {
		System.out.println("init");
		hdfsConfig = new Configuration();
		initFileSystem(conf);
        // 资源释放钩子
        Runtime.getRuntime().addShutdownHook(
                new Thread("release-shutdown-hook-HdfsAppend") {
                    @Override
                    public void run() {
                        System.out.println("☆☆☆ release-shutdown-hook-HdfsAppend准备强制释放资源。");
                        // 强制释放资源
                        try {
							close();
						} catch (Exception e) {
							e.printStackTrace();
						}
                    }
                }
        );
	}

	public void initFileSystem(String localPath) {
		hdfsConfig.addResource(new Path(localPath+"/hdfs-site.xml"));
		hdfsConfig.addResource(new Path(localPath+"/core-site.xml"));
		hdfsConfig.addResource(new Path(localPath+"/mapred-site.xml"));
		try {
			fs = FileSystem.newInstance(hdfsConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void append(String fileName) throws Exception {
		System.out.println("append " + fileName);
		fs.append(new Path(fileName));
//		Thread.sleep(1000000);
		close();
	}
	
	public void close() throws Exception {
		System.out.println("close hdfs");
		if(fs!=null) {
			fs.close();
		}
	}
	
	public static void main(String[] args) {
		System.setProperty("HADOOP_USER_NAME", "app");
		if(args.length==2) {
			String fileName = args[0];
			String conf = args[1];
			try {
				new HdfsAppend(conf).append(fileName);
//				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
