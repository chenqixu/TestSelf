package com.mr.orc;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.orc.mapred.OrcStruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Orc读取mapper
 *
 * @author chenqixu
 */
public class OrcReadMapper extends Mapper<NullWritable, OrcStruct, NullWritable, NullWritable> {

    private static final Logger logger = LoggerFactory.getLogger(OrcReadMapper.class);
    private final NullWritable nada = NullWritable.get();

    // Assume the ORC file has type: struct<name:string,age:string>
    public void map(NullWritable key, OrcStruct value,
                    Context output) throws IOException, InterruptedException {
        // take the first field as the key and the second field as the value
        logger.info("name：{}，age：{}", value.getFieldValue(0), value.getFieldValue(1));
        output.write(nada, nada);
    }
}
