package com.mr.util;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat;
import org.apache.hadoop.hive.ql.io.orc.OrcSerde;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Orc本地文件输出
 *
 * @author chenqixu
 */
public class OrcWriter {
    public static void main(String[] args) throws Exception {
        HadoopConfUtil.setHadoopUser("edc_base");
        JobConf conf = new JobConf(HadoopConfUtil.getLocalConf());
        LocalFileSystem lfs = new LocalFileSystem();
//        FileSystem fs = FileSystem.get(conf);
        Path outputPath = new Path("d:\\tmp\\data\\xdr\\test1.orc");
//        fs.deleteOnExit(outputPath);
        StructObjectInspector inspector = (StructObjectInspector) ObjectInspectorFactory.getReflectionObjectInspector(
                MyFile.class, ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
        OrcSerde serde = new OrcSerde();
        OutputFormat outFormat = new OrcOutputFormat();
        RecordWriter writer = outFormat.getRecordWriter(lfs, conf, outputPath.toString(), Reporter.NULL);
        FileInputStream fis = new FileInputStream(new File("d:\\tmp\\data\\xdr\\VoLTE_ici_8582644071373960_1588582644067401578_8651180874284_8618965191816_20220224_094416_012022_11_1588580346752507017.cap"));
        byte[] buff = new byte[fis.available()];
        int ret = fis.read(buff);
        writer.write(NullWritable.get(), serde.serialize(new MyFile(new String(buff, StandardCharsets.ISO_8859_1)), inspector));
        fis.close();
//        writer.write(NullWritable.get(), serde.serialize(new MyRow("张三", 20), inspector));
//        writer.write(NullWritable.get(), serde.serialize(new MyRow("李四", 22), inspector));
//        writer.write(NullWritable.get(), serde.serialize(new MyRow("王五", 30), inspector));
        writer.close(Reporter.NULL);
        lfs.close();
        System.out.println("write success .");
        org.apache.orc.OrcFile a;
    }

    static class MyRow implements Writable {
        String name;
        int age;

        MyRow(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public void readFields(DataInput arg0) throws IOException {
            throw new UnsupportedOperationException("no write");
        }

        @Override
        public void write(DataOutput arg0) throws IOException {
            throw new UnsupportedOperationException("no read");
        }
    }

    static class MyFile implements Writable {
        String content;

        MyFile(String content) {
            this.content = content;
        }

        @Override
        public void readFields(DataInput arg0) throws IOException {
            throw new UnsupportedOperationException("no write");
        }

        @Override
        public void write(DataOutput arg0) throws IOException {
            throw new UnsupportedOperationException("no read");
        }
    }
}
