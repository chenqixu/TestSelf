package com.newland.bi.bigdata.xml.util;

import com.cqx.process.LogInfoFactory;
import com.cqx.process.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 组件Bean
 *
 * @author chenqixu
 */
public class Component {

    private static Logger logger = LogInfoFactory.getInstance(Component.class);
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
