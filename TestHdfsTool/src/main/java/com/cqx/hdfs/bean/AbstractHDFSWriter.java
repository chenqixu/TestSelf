package com.cqx.hdfs.bean;

/**
 * AbstractHDFSWriter
 *
 * @author chenqixu
 */
public class AbstractHDFSWriter {
    private AbstractHDFSWriter() {
    }

    public static AbstractHDFSWriter builder() {
        return new AbstractHDFSWriter();
    }
}
