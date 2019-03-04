package com.cqx.hdfs.bean;

import com.cqx.common.option.OptionsTool;
import org.junit.Test;

public class HdfsToolBeanTest {
    @Test
    public void test() throws Exception {
        String[] args = {"-h", "aa", "-p", "d:/tmp"};
        HdfsToolBean hdfsToolBean = new OptionsTool().parser(args, HdfsToolBean.class);
        System.out.println(hdfsToolBean.getHadoop_user());
        System.out.println(hdfsToolBean.getPath());
    }
}