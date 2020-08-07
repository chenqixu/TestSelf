package com.cqx.utils;

import org.junit.Test;

public class SchemaUtilTest {

    @Test
    public void readUrlContent() {
        String topic = "nmc_tb_lte_http_test";
        String urlStr = "http://10.1.8.203:19090/nl-edc-cct-sys-ms-dev/SchemaService/getSchema?t=";
        SchemaUtil schemaUtil = new SchemaUtil(urlStr);
        System.out.println(schemaUtil.readUrlContent(topic));
    }

    @Test
    public void getSchemaByTopic() {
        String topic = "nmc_tb_gn_http_test";
        System.out.println(new SchemaUtil().getSchemaByTopic(topic));
    }
}