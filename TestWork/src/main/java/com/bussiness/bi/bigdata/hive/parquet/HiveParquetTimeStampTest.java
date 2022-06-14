package com.bussiness.bi.bigdata.hive.parquet;

/**
 * <b>hive读写parquet下的timestamp类型操作</b><br>
 * 写：写入UTC时间<br>
 * 读：按当前时区读取
 * */
public class HiveParquetTimeStampTest {
	public static String getLocalOrGMT(boolean flag){
		return flag?"Local":"GMT";
	}
	
	public static void main(String[] args) {
		System.out.println(getLocalOrGMT(false));
		// hive write with GMT
		// org.apache.hadoop.hive.ql.io.parquet.write.DataWritableWriter
		// public void write(Object value) {
		// recordConsumer.addBinary(NanoTimeUtils.getNanoTime(ts, false).toBinary());
		//
		// public static NanoTime getNanoTime(Timestamp ts, boolean skipConversion) {
		// Calendar calendar = getCalendar(skipConversion);
		//
		// private static Calendar getCalendar(boolean skipConversion) {
		// Calendar calendar = skipConversion ? getLocalCalendar() : getGMTCalendar();

		System.out.println(getLocalOrGMT(true));
		// hive read with Local
		// org.apache.hadoop.hive.ql.io.parquet.read.DataWritableReadSupport
		// org.apache.hadoop.hive.ql.io.parquet.convert.DataWritableRecordConverter
		// org.apache.hadoop.hive.ql.io.parquet.convert.HiveGroupConverter
		// org.apache.hadoop.hive.ql.io.parquet.convert.ETypeConverter
		// ETIMESTAMP_CONVERTER(TimestampWritable.class) {
		// boolean skipConversion = Boolean.valueOf(metadata.get(HiveConf.ConfVars.HIVE_PARQUET_TIMESTAMP_SKIP_CONVERSION.varname));
        // Timestamp ts = NanoTimeUtils.getTimestamp(nt, skipConversion);
		// 默认是true
		// HIVE_PARQUET_TIMESTAMP_SKIP_CONVERSION("hive.parquet.timestamp.skip.conversion", true,
	    //  "Current Hive implementation of parquet stores timestamps to UTC, this flag allows skipping of the conversion" +
	    //  "on reading parquet files from other tools"),
	}
}
