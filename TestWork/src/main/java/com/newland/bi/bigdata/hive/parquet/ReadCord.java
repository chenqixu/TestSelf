package com.newland.bi.bigdata.hive.parquet;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.parquet.read.DataWritableReadSupport;
import org.apache.hadoop.io.ArrayWritable;

import parquet.example.data.Group;
import parquet.example.data.simple.SimpleGroup;
import parquet.hadoop.ParquetReader;

public class ReadCord {
	public static final String HDFS_PATH = "hdfs://192.168.0.80";
	public static final String defaultFS = "10.1.8.75:8020";
	public static final String fsHdfsImpl = "org.apache.hadoop.hdfs.DistributedFileSystem";

    public static void main(String[] args) throws IOException {
        Path path = new Path(HDFS_PATH+"/user/hive/warehouse/parquet_example/part_20150716103149");
        ParquetReader<ArrayWritable> reader = ReadCord.getReader(path);
        ArrayWritable content= null;
        int i=0;
        do{
            i++;
            content = reader.read();
            System.out.println(content);
        }while(content != null);
        System.out.println("i counter:"+--i);
    }

    public static ParquetReader<ArrayWritable> getReader(Path path) throws IOException{
        ParquetReader<ArrayWritable> reader = new ParquetReader<ArrayWritable>(path,new DataWritableReadSupport());
        return reader;
    }
}
