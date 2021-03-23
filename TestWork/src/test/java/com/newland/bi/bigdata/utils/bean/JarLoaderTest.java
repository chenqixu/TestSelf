package com.newland.bi.bigdata.utils.bean;

import org.junit.Test;

public class JarLoaderTest {

    @Test
    public void load() throws Exception {
        String filePath = "D:\\Document\\Workspaces\\Git\\FujianBI\\realtime-jstorm\\edc-bigdata-storm-realtime_location\\target\\edc-bigdata-storm-realtime_location-1.0.jar";
        // 将jar包转为byte[]
        byte[] resource = JarLoader.getDataSource(filePath);
        //通过byte[]进行类加载
        JarLoader.load(resource);
    }
}