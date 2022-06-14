package com.bussiness.bi.bigdata.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * IXMLChildren
 *
 * @author chenqixu
 */
public abstract class IXMLChildren implements Cloneable {

    private static Logger logger = LoggerFactory.getLogger(IXMLChildren.class);
    protected String[] array;
    protected Map<String, Integer> indexMap = new LinkedHashMap<>();
    protected Map<String, String> valueMap = new LinkedHashMap<>();
    protected Map<String, IXMLDefaultValue> defaultValueMap = new HashMap<>();
    protected String name;
    protected int index = 0;

    public void save(String name, String value) {
        this.name = name;
        this.valueMap.put(name, value);
    }

    public void print() {
        logger.debug("index：{}，array：{}，name：{}", index, array[index], name);
        if (!array[index].equals(name)) {
            // 向下搜索到关键字，打印中间缺失的
            find(name);
        }
        this.index++;
    }

    public String getDefaultValue(String key) {
        IXMLDefaultValue ixmlDefaultValue = defaultValueMap.get(key);
        return ixmlDefaultValue != null ? ixmlDefaultValue.getValue() : valueMap.get(key);
    }

    public void end() {
        // 把末尾剩余的打印出来
        for (int i = index; i < array.length; i++) {
            IXMLDefaultValue ixmlDefaultValue = defaultValueMap.get(array[i]);
            logger.info("{}: {}", array[i], ixmlDefaultValue != null ? ixmlDefaultValue.getValue() : "");
        }
    }

    protected void find(String key) {
        Integer new_index = indexMap.get(key);
        logger.debug("find：{}，new_index：{}", key, new_index);
        if (new_index != null) {
            // 打印缺失
            for (int i = index; i < new_index; i++) {
                IXMLDefaultValue ixmlDefaultValue = defaultValueMap.get(array[i]);
                logger.info("{}: {}", array[i], ixmlDefaultValue != null ? ixmlDefaultValue.getValue() : "");
            }
            // index前进到new_index
            index = new_index;
        }
    }

    protected void initArray(String values) {
        array = values.split(",", -1);
    }

    protected void initLinkedMap(String values) {
        int cnt = 0;
        for (String v : values.split(",", -1)) {
            indexMap.put(v, cnt++);
        }
    }

    protected void addDefaultValueMap(String key, IXMLDefaultValue defaultValue) {
        defaultValueMap.put(key, defaultValue);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Object object = super.clone();
        return object;
    }
}
