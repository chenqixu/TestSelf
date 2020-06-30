package com.newland.bi.jkreport.bean;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HdfsLSResultTest {

    private static final Logger logger = LoggerFactory.getLogger(HdfsLSResultTest.class);
    private HdfsLSResult hdfsLSResult;

    @Before
    public void setUp() throws Exception {
        hdfsLSResult = new HdfsLSResult();
        hdfsLSResult.setFilterKey(".ok");
        hdfsLSResult.setExclusionKey(".complete");
    }

    @Test
    public void exclusion() {
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170000", "00"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170000", "202006170000.ok"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170000", "202006170000.complete"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170015", "00"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170015", "01"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170015", "02"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170015", "202006170015.ok"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170030", "00"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170030", "01"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170030", "202006170030.ok"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170045", "00"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170045", "01"));
        hdfsLSResult.addSource(new HdfsLSBean("notnat", "202006170000", "00"));
        hdfsLSResult.sourceToType();
        hdfsLSResult.typeToDate();
        hdfsLSResult.exclusion();
        logger.info("getTypeDateMap：{}", hdfsLSResult.getTypeDateMap());
    }
}