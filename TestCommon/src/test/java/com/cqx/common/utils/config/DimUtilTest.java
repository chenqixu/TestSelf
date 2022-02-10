package com.cqx.common.utils.config;

import com.cqx.common.test.TestBase;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DimUtilTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(DimUtilTest.class);
    private Map yaml;

    @Before
    public void setUp() throws Exception {
        yaml = getParam("dim.yaml");
    }

    @Test
    public void yamlToBeanList() throws IOException {
        List<DimBean> dimBeanList = DimBean.yamlToBeanList(yaml);
        for (DimBean dimBean : dimBeanList) {
            logger.info("name：{}", dimBean.getName());
            for (DimBean.KVS kvs : dimBean.getKvsList()) {
                logger.info("  key：{}，value：{}", kvs.getKey(), kvs.getValue());
            }
            logger.info("get 1：{}", dimBean.getValueByKey("1"));
        }
    }

    @Test
    public void yamlToBeanMap() {
        Map<String, DimBean> dimBeanMap = DimBean.yamlToBeanMap(yaml);
        logger.info("{}", dimBeanMap.get("dun_type").getValueByKey("20"));
    }
}