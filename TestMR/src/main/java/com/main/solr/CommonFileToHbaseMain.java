package com.main.solr;

import com.mr.solr.CommonFileToHbaseMapper;
import com.mr.solr.CommonUtils;
import com.mr.util.HadoopConfUtil;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat;
import org.apache.hadoop.hbase.mapreduce.KeyValueSortReducer;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;
import org.apache.hadoop.hbase.mapreduce.SimpleTotalOrderPartitioner;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;

/**
 * solr索引
 *
 * @author chenqixu
 */
public class CommonFileToHbaseMain {
    public static void main(String[] args)
            throws IOException, InterruptedException, ClassNotFoundException, ParseException {
        long starttime = System.currentTimeMillis();
        String[] _args = {"D:\\Document\\Workspaces\\Git\\TestSelf\\TestMR\\src\\main\\resources\\conf\\gjm_test.xml", "2018103100"};
        args = _args;
//        HadoopConfUtil.setHadoopUser("UpdatusUser");
        System.setProperty("user.name", "UpdatusUser");
        System.setProperty("user.home", "C:\\Users\\UpdatusUser");
        HBaseConfiguration conf = HadoopConfUtil.getLocalHbaseConf();

        String[] dfsArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

        if (dfsArgs.length < 2) {
            System.out.println("传入的参数有误....");
            System.exit(1);
        }

        conf.addResource(new Path(dfsArgs[0]));

        FileSystem fs = FileSystem.newInstance(conf);

        String statisTime = dfsArgs[1];
        conf.set("STATIS_TIME", dfsArgs[1]);

        String tbName = CommonUtils.getTableName(conf.get("HTABLE_NAME"), conf.get("HTABLE_TYPE"), statisTime);
        System.out.println(new StringBuilder().append("tbName:").append(tbName).append(" statisTime:").append(statisTime).toString());

        int leng = CommonUtils.getTaskSplitLend(conf.get("TASK_TYPE"));

        Path outputPath = new Path(new StringBuilder().append(conf.get("OUPUT_PATH")).append(statisTime.substring(0, leng)).append("/").toString());

        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
            System.out.println(new StringBuilder().append("输出路径已经存在，删除.....").append(outputPath).toString());
        }

        Path errorPath = new Path(new StringBuilder().append(conf.get("ERROR_PATH")).append(statisTime.substring(0, leng)).append("/").toString());

        if (fs.exists(errorPath)) {
            fs.delete(errorPath, true);
            System.out.println(new StringBuilder().append("错误路径已经存在，删除.....").append(errorPath).toString());
        }
        conf.set("FULL_ERROR_OUTPUT_PATH", new StringBuilder().append(conf.get("ERROR_PATH")).append(statisTime.substring(0, leng)).append("/").toString());
        System.out.println(new StringBuilder().append("FULL_ERROR_OUTPUT_PATH：").append(conf.get("FULL_ERROR_OUTPUT_PATH")).toString());

        Job job = new Job(conf, new StringBuilder().append(statisTime).append(conf.get("TASK_NAME")).toString());

        job.setJarByClass(CommonFileToHbaseMain.class);
        job.setReducerClass(KeyValueSortReducer.class);

        job.setMapOutputKeyClass(ImmutableBytesWritable.class);
        job.setMapOutputValueClass(KeyValue.class);

        job.setPartitionerClass(SimpleTotalOrderPartitioner.class);

        String inputPath = new StringBuilder().append(conf.get("INPUT_PATH")).append(statisTime.substring(0, leng)).append("/").toString();
//        String inputPath = new StringBuilder().append(conf.get("INPUT_PATH")).toString();
        //hdfs://retnverfmb/user/bdoc/9/services/hdfs/9/edc_base/yz/data/ltemerge/ltemerge/201811292345/20181129/
        System.out.println(new StringBuilder().append("inputPath：").append(inputPath).toString());
        if (!CommonUtils.ifFileExit(job, fs, inputPath)) {
            System.out.println("输入文件不存在,请检查输入源文件！");
            System.exit(1);
        }

        CommonUtils.addInputPath(job, fs, new Path(inputPath), statisTime.substring(0, leng), CommonFileToHbaseMapper.class);

        FileOutputFormat.setOutputPath(job, outputPath);
        System.out.println(new StringBuilder().append("output path : ").append(outputPath).toString());

        HFileOutputFormat.setCompressOutput(job, true);

        HFileOutputFormat.configureIncrementalLoad(job, new HTable(conf, tbName));

        HTable table = new HTable(conf, tbName);
        StringBuilder compressionConfigValue = new StringBuilder();

        HTableDescriptor tableDescriptor = table.getTableDescriptor();
        if (tableDescriptor == null) {
            return;
        }
        Collection families = tableDescriptor.getFamilies();
        int i = 0;
        HColumnDescriptor familyDescriptor;
        for (Iterator i$ = families.iterator(); i$.hasNext(); compressionConfigValue.append(URLEncoder.encode(familyDescriptor.getCompression().getName(), "UTF-8"))) {
            familyDescriptor = (HColumnDescriptor) i$.next();
            if (i++ > 0)
                compressionConfigValue.append('&');
            compressionConfigValue.append(URLEncoder.encode(familyDescriptor.getNameAsString(), "UTF-8"));

            compressionConfigValue.append('=');
        }
        System.out.println(new StringBuilder().append("|||||||||||||||||||||||||hbase.hfileoutputformat.families.compression=").append(compressionConfigValue.toString()).toString());

        conf.set("hbase.hfileoutputformat.families.compression", compressionConfigValue.toString());

        if (job.waitForCompletion(true)) {
            System.out.println(new StringBuilder().append("*****************").append(System.currentTimeMillis() - starttime).append("*********************").toString());
            try {
                System.out.println("start execute load...");
                LoadIncrementalHFiles loader = new LoadIncrementalHFiles(conf);
                System.out.println(new StringBuilder().append("$$$$tableName:").append(tbName).append(" outputPath:").append(outputPath).toString());

                loader.doBulkLoad(outputPath, new HTable(conf, tbName));
                System.out.println("load success...");
                System.out.println(new StringBuilder().append("End time*****************").append(System.currentTimeMillis() - starttime).append("*********************").toString());
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

            if ((!conf.getBoolean("IS_DEBUG", true)) &&
                    (null != inputPath)) {
                fs.delete(new Path(inputPath), true);
            }

            fs.delete(outputPath, true);
            System.out.println("加载完成删除输出文件以及输入文件.....");

            System.exit(0);
        }
        System.exit(1);
    }
}
