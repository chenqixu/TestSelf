package com.cqx.common.utils.doc;

import org.junit.Test;

import java.io.IOException;

public class DocUtilTest {
    private DocUtil docUtil;

    @Test
    public void readDoc() throws IOException {
        docUtil = new DocUtil();
        docUtil.readDoc("d:\\Work\\实时\\ADB\\KafkaToAdb\\需求\\238783-关于铁通监控调度大屏二期的需求实时数据采集-产品需求说明书.doc");
    }
}