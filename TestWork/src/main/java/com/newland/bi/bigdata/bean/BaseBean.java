package com.newland.bi.bigdata.bean;

import java.util.LinkedHashMap;

/**
 * BaseBean
 *
 * @author chenqixu
 */
public class BaseBean {

    protected LinkedHashMap<String, String> beanDesc = new LinkedHashMap<>();

    public void setBeanDesc(String key, String value) {
        beanDesc.put(key, value);
    }

    public LinkedHashMap<String, String> listBeanDesc() {
        return beanDesc;
    }
}
