package com.bussiness.bi.bigdata.hive.parquet;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.parquet.write.DataWritableWriteSupport;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;

import parquet.hadoop.ParquetWriter;
import parquet.hadoop.metadata.CompressionCodecName;
import parquet.schema.MessageType;
import parquet.schema.MessageTypeParser;

public class BasketWriter {
	public static void main(String[] args) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("YYYYMMddHHmmss");
		new BasketWriter().generateBasketData("part_"+dateFormat.format(new Date()));
	}

	private void generateBasketData(String outFilePath) throws IOException {
		final MessageType schema = MessageTypeParser.parseMessageType("message basket { required int64 basketid; required int64 productid; required int32 quantity; required float price; required float totalbasketvalue; }");
		Configuration config = new Configuration();
		DataWritableWriteSupport.setSchema(schema, config);
		Path outDirPath = new Path(ReadCord.HDFS_PATH+"/user/hive/warehouse/parquet_example/"+outFilePath);

		ParquetWriter writer = new ParquetWriter(outDirPath, new DataWritableWriteSupport () {
			@Override
			public WriteContext init(Configuration configuration) {
				if (configuration.get(DataWritableWriteSupport.PARQUET_HIVE_SCHEMA) == null) {
					configuration.set(DataWritableWriteSupport.PARQUET_HIVE_SCHEMA, schema.toString());
				}
				return super.init(configuration);
			}
		}, CompressionCodecName.SNAPPY, 256*1024*1024, 100*1024);
		int numBaskets = 1000000;
		Random numProdsRandom = new Random();
		Random quantityRandom = new Random();
		Random priceRandom = new Random();
		Random prodRandom = new Random();
		for (int i = 0; i < numBaskets; i++) {
			int numProdsInBasket = numProdsRandom.nextInt(30);
			numProdsInBasket = Math.max(7, numProdsInBasket);
			float totalPrice = priceRandom.nextFloat();
			totalPrice = (float)Math.max(0.1, totalPrice) * 100;
			for (int j = 0; j < numProdsInBasket; j++) {
				Writable[] values = new Writable[5];
				values[0] = new LongWritable(i);
				values[1] = new LongWritable(prodRandom.nextInt(200000));
				values[2] = new IntWritable(quantityRandom.nextInt(10));
				values[3] = new FloatWritable(priceRandom.nextFloat());
				values[4] = new FloatWritable(totalPrice);
				ArrayWritable value = new ArrayWritable(Writable.class, values);
				writer.write(value);
			}
		}
		writer.close();
	}
}
