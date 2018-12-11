package com.cqx.mr;

import java.io.IOException;
import com.cqx.util.HDFS_File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
//import org.apache.hadoop.hbase.filter.Filter;
//import org.apache.hadoop.hbase.filter.FilterList;
//import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
//import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
//import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MRSearchAuto {
	private static final Log LOG = LogFactory.getLog(MRSearchAuto.class);

	private static String TABLE_NAME = "tablename";
	private static byte[] FAMILY_NAME = Bytes.toBytes("cfname");
	private static byte[][] QUALIFIER_NAME = { Bytes.toBytes("col1"),
			Bytes.toBytes("col2"), Bytes.toBytes("col3") };

	public static class SearchMapper extends
			TableMapper<ImmutableBytesWritable, Text> {
//		private int numOfFilter = 0;

		private Text word = new Text();
		String[] strConditionStrings = new String[] { "", "", "" }/*
																 * { "新C87310",
																 * "10", "2" }
																 */;

		/*
		 * private void init(Configuration conf) throws IOException,
		 * InterruptedException { strConditionStrings[0] =
		 * conf.get("search.license").trim(); strConditionStrings[1] =
		 * conf.get("search.carColor").trim(); strConditionStrings[2] =
		 * conf.get("search.direction").trim(); LOG.info("license: " +
		 * strConditionStrings[0]); }
		 */
		protected void setup(Context context) throws IOException,
				InterruptedException {
			strConditionStrings[0] = context.getConfiguration()
					.get("search.license").trim();
			strConditionStrings[1] = context.getConfiguration()
					.get("search.color").trim();
			strConditionStrings[2] = context.getConfiguration()
					.get("search.direction").trim();
		}

		protected void map(ImmutableBytesWritable key, Result value,
				Context context) throws InterruptedException, IOException {
			String string = "";
			String tempString;

			/**/
			for (int i = 0; i < 1; i++) {
				// /在此map里进行filter的功能
				tempString = Text.decode(value.getValue(FAMILY_NAME,
						QUALIFIER_NAME[i]));
				if (tempString.equals(/* strConditionStrings[i] */"新C87310")) {
					LOG.info("新C87310. conf: " + strConditionStrings[0]);
					if (tempString.equals(strConditionStrings[i])) {
						string = string + tempString + " ";
					} else {
						return;
					}
				}

				else {
					return;
				}
			}

			word.set(string);
			context.write(null, word);
		}
	}

	public void searchHBase(int numOfDays) throws IOException,
			InterruptedException, ClassNotFoundException {
		long startTime;
		long endTime;

		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "node2,node3,node4");
		conf.set("fs.default.name", "hdfs://node1");
		conf.set("mapred.job.tracker", "node1:54311");
		/*
		 * 传递参数给map
		 */
		conf.set("search.license", "新C87310");
		conf.set("search.color", "10");
		conf.set("search.direction", "2");

		Job job = new Job(conf, "MRSearchHBase");
		System.out.println("search.license: " + conf.get("search.license"));
		job.setNumReduceTasks(0);
		job.setJarByClass(MRSearchAuto.class);
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
		HDFS_File file = new HDFS_File();
		file.DelFile(conf, outPath.getName(), true); // 若已存在，则先删除
		FileOutputFormat.setOutputPath(job, outPath);// 输出结果

		startTime = System.currentTimeMillis();
		job.waitForCompletion(true);
		endTime = System.currentTimeMillis();
		System.out.println("Time used: " + (endTime - startTime));
		System.out.println("startRow:" + Text.decode(startRow));
		System.out.println("stopRow: " + Text.decode(stopRow));
	}

	public static void main(String args[]) throws IOException,
			InterruptedException, ClassNotFoundException {
		MRSearchAuto mrSearchAuto = new MRSearchAuto();
		int numOfDays = 1;
		if (args.length == 1)
			numOfDays = Integer.valueOf(args[0]);
		System.out.println("Num of days: " + numOfDays);
		mrSearchAuto.searchHBase(numOfDays);
	}
}
