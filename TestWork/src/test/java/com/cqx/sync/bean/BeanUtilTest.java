package com.cqx.sync.bean;

import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BeanUtilTest {

    private BeanUtil beanUtil = new BeanUtil();

    @Test
    public void generateObject() throws Exception {
        LinkedHashMap properties = new LinkedHashMap();
        properties.put("id", Class.forName("java.lang.Integer"));
        properties.put("name", Class.forName("java.lang.String"));
        properties.put("address", Class.forName("java.lang.String"));
        Object stu = beanUtil.generateObject(properties);
        beanUtil.setValue(stu, "id", 123);
        beanUtil.setValue(stu, "name", "454");
        beanUtil.setValue(stu, "address", "789");
        System.out.println("stu>> " + stu);
        System.out.println("id>> " + beanUtil.getValue(stu, "id"));
        System.out.println("name>> " + beanUtil.getValue(stu, "name"));
        System.out.println("address>> " + beanUtil.getValue(stu, "address"));
    }

    @Test
    public void getValue() throws Exception {
//        String[] fields = "id,name,address".split(",", -1);
//        Object stu = beanUtil.generateObject(fields);
//        beanUtil.setValue(stu, "id", "123");
//        beanUtil.setValue(stu, "name", "454");
//        beanUtil.setValue(stu, "address", "789");
//        System.out.println("stu>> " + stu);
//        System.out.println("id>> " + beanUtil.getValue(stu, "id"));
//        System.out.println("name>> " + beanUtil.getValue(stu, "name"));
//        System.out.println("address>> " + beanUtil.getValue(stu, "address"));
    }

    @Test
    public void setValue() {
    }
}