package com.mr.orc;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.orc.TypeDescription;
import org.apache.orc.mapred.OrcStruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Orc输出Mapper，单mapper模式
 *
 * @author chenqixu
 */
public class OrcWriterMapper extends Mapper<LongWritable, Text, NullWritable, OrcStruct> {

    private static final Logger logger = LoggerFactory.getLogger(OrcWriterMapper.class);
    private TypeDescription schema = TypeDescription.fromString("struct<name:string,age:string>");
    private OrcStruct pair = (OrcStruct) OrcStruct.createValue(schema);
    private final NullWritable nada = NullWritable.get();

    @Override
    protected void map(LongWritable key, Text value, Context output)
            throws IOException, InterruptedException {
        Text name0 = new Text();
        Text name1 = new Text();
        String[] arrvalue = value.toString().split("\\|");
        name0.set(arrvalue[0]);
        name1.set(arrvalue[1]);
        logger.info("value：{}", value);
        logger.info("name0：{}", new String(name0.getBytes()));
        logger.info("name1：{}", new String(name1.getBytes()));
        pair.setFieldValue(0, name0);
        pair.setFieldValue(1, name1);
        output.write(nada, pair);
    }
}
