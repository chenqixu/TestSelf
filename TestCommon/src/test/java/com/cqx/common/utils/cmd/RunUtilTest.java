package com.cqx.common.utils.cmd;

import org.junit.Test;

import java.util.Arrays;

public class RunUtilTest {

    @Test
    public void getCommand() {
        RunUtil runUtil = new RunUtil();
        runUtil.setJavaHome("D:\\Program Files\\Java\\jdk1.8.0_201");
        runUtil.setClassPath("D:\\Document\\Workspaces\\Git\\TestSelf\\target");
        runUtil.setMainClass("com.cqx.common.utils.HelperUtil");
        System.out.println(Arrays.asList(runUtil.getCommand()));
    }
}