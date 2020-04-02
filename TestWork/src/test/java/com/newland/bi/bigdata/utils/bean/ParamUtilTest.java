package com.newland.bi.bigdata.utils.bean;

import com.cqx.common.utils.param.ParamUtil;
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
        map.put("scan_seq", "111");
        map.put("start_cnt", "111");
        map.put("end_cnt", "111");
        map.put("cnt", "111");
        FileToRedisBean fileToRedisBean = paramUtil.setValueByMap(map, FileToRedisBean.class);
        ParamUtil.info(fileToRedisBean, logger);
    }

}