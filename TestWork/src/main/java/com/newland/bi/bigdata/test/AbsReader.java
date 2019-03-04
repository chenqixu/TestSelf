package com.newland.bi.bigdata.test;

/**
 * AbsReader
 *
 * @author chenqixu
 */
public class AbsReader {

    public AbsReader() {
    }

    public AbsReader(String msg) {
        System.out.println(this + "，" + msg);
    }

    public AbsReader builder(String msg) {
        return new AbsReader(msg);
    }
}
