package com.newland.bi.bigdata.utils.bean;

import com.newland.bi.bigdata.bean.FileToRedisBean;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ParamUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(ParamUtilTest.class);
    private ParamUtil paramUtil;

    @Before
    public void setUp() {
        paramUtil = new ParamUtil();
    }

    @Test
    public void FileToRedisBean() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("scan_path", "/bi/app/A001/");
        map.put("scan_rule", "*.TXT");
        FileToRedisBean fileToRedisBean = paramUtil.setValueByMap(map, FileToRedisBean.class);
        ParamUtil.info(fileToRedisBean, logger);
    }

    public void localCommit1() {
    }

    public void localCommit2() {
    }

    public void localCommit3() {
    }
}