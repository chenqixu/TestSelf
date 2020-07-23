package com.cqx.common.utils.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class DBUtilTest {

    private DBUtil dbUtil;

    @Before
    public void setUp() throws Exception {
        DBBean dbBean = new DBBean();
        dbBean.setDbType(DBType.ORACLE);
        dbBean.setTns("jdbc:oracle:thin:@10.1.8.204:1521:orapri");
        dbBean.setUser_name("edc_addressquery");
        dbBean.setPass_word("edc_addressquery");
        dbBean.setPool(false);
        dbUtil = new DBUtil(dbBean);
    }

    @After
    public void tearDown() throws Exception {
        if (dbUtil != null) dbUtil.release();
    }

    @Test
    public void tableFieldsCheck() throws Exception {
        dbUtil.setTable_name("sm2_dim_key");
        dbUtil.init();

        String t = "这是a";
        System.out.println(t + "，UTF-8 byte len：" + t.getBytes(StandardCharsets.UTF_8).length);
        System.out.println(t + "，GBK byte len：" + t.getBytes("GBK").length);
        System.out.println(new String(t.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
        System.out.println(new String(t.getBytes("GBK"), "GBK"));
        System.out.println(new String("你好abc123".getBytes("GBK"), "GBK"));
        System.out.println(new String("你好abc123".getBytes(), "GBK"));//前面是默认字符集，如果前后不一致就会乱码
        System.out.println(new String("你好abc123".getBytes(), "UTF-8"));
        System.out.println(new String("你好abc123".getBytes("UTF-8"), "UTF-8"));
    }
}