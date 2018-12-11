package com.main;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.mr.SearchMapper;
import com.mr.comm.GetMovementConstants;

public class MRSearchMain {
	private static final Log LOG = LogFactory.getLog(MRSearchMain.class);

	private static String TABLE_NAME = "tablename";

	private static byte[] FAMILY_NAME = Bytes.toBytes("cfname");

	private static byte[][] QUALIFIER_NAME = { Bytes.toBytes("col1"),
			Bytes.toBytes("col2"), Bytes.toBytes("col3") };

	public void searchHBase(int numOfDays) throws IOException,
			InterruptedException, ClassNotFoundException {
		long startTime;
		long endTime;

		String path = "/home/hadoop/app/hadoop-2.0.0-cdh4.3.0/etc/hadoop/";	
		Configuration conf = HBaseConfiguration.create();
//		conf.set("hbase.zookeeper.quorum", "streamslab.localdomain");
//		conf.set("fs.default.name", "hdfs://streamslab.localdomain:8020");
//		conf.set("mapred.job.tracker", "hdfs://streamslab.localdomain:50300");
//		conf.set("fs.hdfs.impl",
//				org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		conf.set("fs.file.impl",
				org.apache.hadoop.fs.LocalFileSystem.class.getName());
		//先加载配置,FileSystem需要
		conf.addResource(new Path(path + "core-site.xml"));
		conf.addResource(new Path(path + "hdfs-site.xml"));
		conf.addResource(new Path(path + "mapred-site.xml"));
		/* 
		 * 传递参数给map 
		 */
		conf.set("search.license", "新C87310");
		conf.set("search.color", "10");
		conf.set("search.direction", "2");

		Job job = new Job(conf, "MRSearchHBase");
		System.out.println("search.license: " + conf.get("search.license"));
		job.setNumReduceTasks(0);
		job.setJarByClass(MRSearchMain.class);
		Scan scan = new Scan();
		scan.addFamily(FAMILY_NAME);
		byte[] startRow = Bytes.toBytes("2011010100000");
		byte[] stopRow;
		switch (numOfDays) {
		case 1:
			stopRow = Bytes.toBytes("2011010200000");
			break;
		case 10:
			stopRow = Bytes.toBytes("2011011100000");
			break;
		case 30:
			stopRow = Bytes.toBytes("2011020100000");
			break;
		case 365:
			stopRow = Bytes.toBytes("2012010100000");
			break;
		default:
			stopRow = Bytes.toBytes("2011010101000");
		}
		// 设置开始和结束key  
		scan.setStartRow(startRow);
		scan.setStopRow(stopRow);

		TableMapReduceUtil.initTableMapperJob(TABLE_NAME, scan,
				SearchMapper.class, ImmutableBytesWritable.class, Text.class,
				job);
		Path outPath = new Path("searchresult");
		LOG.info("outPath:"+outPath.toString());			
		
		//hdfs文件系统
		FileSystem file = null;
		try {
			file = FileSystem.get(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		HDFS_File file = new HDFS_File();
//		file.DelFile(conf, outPath.getName(), true); // 若已存在，则先删除
		//"hdfs://streamslab.localdomain:8020/
		if (file.exists(outPath)) {
			file.delete(outPath, true);
			LOG.info("=====delPath 删除：" + outPath.toString() + "=====");
		}
		FileOutputFormat.setOutputPath(job, outPath);// 输出结果  

		startTime = System.currentTimeMillis();
		job.waitForCompletion(true);
		endTime = System.currentTimeMillis();
		LOG.info("Time used: " + (endTime - startTime));
		LOG.info("startRow:" + Text.decode(startRow));
		LOG.info("stopRow: " + Text.decode(stopRow));
	}

	public static void main(String args[]) throws IOException,
			InterruptedException, ClassNotFoundException {
		MRSearchMain mrSearch = new MRSearchMain();
		int numOfDays = 1;
		if (args.length == 1)
			numOfDays = Integer.valueOf(args[0]);
		LOG.info("Num of days: " + numOfDays);
		mrSearch.searchHBase(numOfDays);
	}
}
