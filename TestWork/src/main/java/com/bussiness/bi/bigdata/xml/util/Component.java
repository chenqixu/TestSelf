package com.bussiness.bi.bigdata.xml.util;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;



import java.util.HashMap;
import java.util.Map;

/**
 * 组件Bean
 *
 * @author chenqixu
 */
public class Component {

    private static MyLogger logger = MyLoggerFactory.getLogger(Component.class);
    private Map<String, String> paramMap = new HashMap<>();

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    /**
     * 打印不一致的参数
     *
     * @param obj
     */
    public void diff(Object obj) {
        if (obj instanceof Component) {
            Component t2 = (Component) obj;
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                if (!t2.getParamMap().get(entry.getKey()).equals(entry.getValue())) {
                    logger.info("key：{}，value1：{}，value2：{}", entry.getKey(), entry.getValue(), t2.getParamMap().get(entry.getKey()));
                }
            }
        }
    }

    /**
     * 打印参数
     */
    public void print() {
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            logger.info("key：{}，value：{}", entry.getKey(), entry.getValue());
        }
    }

    /**
     * 比较
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Component) {
            Component t2 = (Component) obj;
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                if (!t2.getParamMap().get(entry.getKey()).equals(entry.getValue())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
