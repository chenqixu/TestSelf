package com.mr;

import java.io.IOException;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;

public class HFileMapper extends
	Mapper<LongWritable, Text, ImmutableBytesWritable, KeyValue> {
	
    public Counter errorRecordsCounter ;
	public enum CounterEnum {
		errorRecords;
	}

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
    	// rowkey kv
    	String line = value.toString();
    	String[] valuesArray = line.split("/t", -1);
    	try{
    		ImmutableBytesWritable rowkey = new ImmutableBytesWritable(Bytes.toBytes(valuesArray[0]));
    		// info:gn
    		KeyValue kv = new KeyValue(Bytes.toBytes(valuesArray[0]), //rowkey
    				Bytes.toBytes("info"), //family
    				Bytes.toBytes("gn"), //qualify
	        		System.currentTimeMillis(), //timestamp
	        		Bytes.toBytes(valuesArray[1])); //value
    		if (null != kv) {
    			context.write(rowkey, kv);
 	        }
    	}catch(Exception e){
        	System.out.println("写文件失败。。。line："+line);
    		e.printStackTrace();
    	}
    	
    }
    
    protected void setup(org.apache.hadoop.mapreduce.Mapper<LongWritable,Text,
    		ImmutableBytesWritable,KeyValue>.Context context) throws IOException ,InterruptedException {
    	errorRecordsCounter = context.getCounter(CounterEnum.errorRecords);
    }
}
