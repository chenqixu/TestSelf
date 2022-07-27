package com.bussiness.bi.bigdata.db;

import com.cqx.bean.CmdBean;
import com.bussiness.bi.mobilebox.bean.EpgParserBeanInfo;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(JDBCUtilTest.class);
    private JDBCUtil jdbcUtil;

    @Before
    public void setUp() throws Exception {
        jdbcUtil = new JDBCUtil(CmdBean.newbuilder()
                .setType("redis").setDns("jdbc:redis://192.168.230.128:6379").setUsername("redis").setPassword("redis"));
    }

    @Test
    public void executeQuery() throws SQLException {
        String sql = "select * from hash###06006005";
        ResultSet rs = jdbcUtil.executeQuery(sql);
        int rsColoumnCount = rs.getMetaData().getColumnCount();// 字段个数
        logger.info("字段个数：{}", rsColoumnCount);
        Map<String, String> map = new HashMap<>();
        while (rs.next()) {
//            for (int i = 0; i < rsColoumnCount; i++) {
            try {
                List<EpgParserBeanInfo> epgParserBeanInfos = EpgParserBeanInfo.jsonToList(reIfNull(rs.getString(3)));
                // 去重
                for (EpgParserBeanInfo epgParserBeanInfo : epgParserBeanInfos) {
                    if (epgParserBeanInfo.getCatgId().equals("2222859")
                            && epgParserBeanInfo.getfId().equals("1")) {
                        logger.info(epgParserBeanInfo.toValue("|"));
                        logger.info("匹配到的：key：{}，value：{}", rs.getString(2), rs.getString(3));
                        System.exit(0);
                    }
                    map.put(epgParserBeanInfo.getCatgId() + "|" + epgParserBeanInfo.getfId(), epgParserBeanInfo.getCatgName());
                }
            } catch (Exception e) {
                logger.error("解析错误，数据：{}", rs.getString(3));
//                    logger.error(e.getMessage(), e);
            }
//            }
        }
    }

    public String reIfNull(String _str) {
        if (_str == null) {
            return "";
        }
        return _str;
    }
}