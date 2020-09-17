package com.cqx.common.utils.cmd;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessBuilderFactoryTest {

    private static final Logger logger = LoggerFactory.getLogger(ProcessBuilderFactoryTest.class);

    @Test
    public void execCmd() {
        System.setProperty("file.encoding", "GBK");

        RunUtil runUtil = new RunUtil();
        runUtil.setJavaHome("D:\\Program Files\\Java\\jdk1.8.0_201");
        runUtil.setClassPath("d:\\tmp\\jar\\server\\lib");
        runUtil.setMainClass("com.cqx.common.utils.HelperUtil");
        List<String> params = new ArrayList<>();
        params.add("abc");
        params.add("123");
        params.add("456");
        runUtil.setParams(params);

        ProcessBuilderFactory processBuilderFactory = new ProcessBuilderFactory();
        String[] command = runUtil.getCommand();
        System.out.println(Arrays.asList(runUtil.getCommand()));

        int ret = processBuilderFactory.execCmdNoWait(new LogDealInf() {
            @Override
            public void logDeal(String logMsg) {
                logger.info("{}", logMsg);
            }
        }, command);
        logger.info("ret : {}", ret);
    }

}