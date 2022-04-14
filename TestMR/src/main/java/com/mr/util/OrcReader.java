package com.mr.util;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.orc.OrcInputFormat;
import org.apache.hadoop.hive.ql.io.orc.OrcSerde;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.mapred.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * Orc本地文件读取
 *
 * @author chenqixu
 */
public class OrcReader {

    private static final Logger logger = LoggerFactory.getLogger(OrcReader.class);

    public static void main(String[] args) throws Exception {
        JobConf conf = new JobConf();
//        Path testFilePath = new Path("D:\\tmp\\data\\orcouput");
        Path testFilePath = new Path("d:\\tmp\\data\\xdr\\test1.orc");
        Properties p = new Properties();
        OrcSerde serde = new OrcSerde();
//        p.setProperty("columns", "name,age");
//        p.setProperty("columns.types", "string:string");
        p.setProperty("columns", "content");
        p.setProperty("columns.types", "string");
        serde.initialize(conf, p);
        StructObjectInspector inspector = (StructObjectInspector) serde.getObjectInspector();
        InputFormat in = new OrcInputFormat();
        FileInputFormat.setInputPaths(conf, testFilePath.toString());
        InputSplit[] splits = in.getSplits(conf, 1);
        logger.info("splits.length：{}", splits.length);
        conf.set("hive.io.file.readcolumn.ids", "1");
        RecordReader reader = in.getRecordReader(splits[0], conf, Reporter.NULL);
        Object key = reader.createKey();
        Object value = reader.createValue();
        List<? extends StructField> fields = inspector.getAllStructFieldRefs();
        long offset = reader.getPos();
        while (reader.next(key, value)) {
            Object content = inspector.getStructFieldData(value, fields.get(0));
            logger.info("content：{}", content);
        }
        reader.close();
    }
}
