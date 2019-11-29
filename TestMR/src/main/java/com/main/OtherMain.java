package com.main;

import org.apache.hadoop.conf.Configuration;

/**
 * OtherMain
 *
 * @author chenqixu
 */
public class OtherMain {

    public static final String ENCODE = "sqoop.mapreduce.export.encode";

    public static void main(String[] args) {
        String encoding;
        Configuration conf = new Configuration();
//        conf.set(ENCODE, "GBK");
        encoding = conf.get(ENCODE);
        if (encoding != null) {
            System.out.println(encoding);
        } else {
            System.out.println("encoding is NULL");
        }
    }
}
