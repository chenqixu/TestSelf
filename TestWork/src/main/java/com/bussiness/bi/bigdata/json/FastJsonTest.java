package com.bussiness.bi.bigdata.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;

import java.lang.reflect.Type;
import java.util.Map;

public class FastJsonTest {
    private String name;

    public static void main(String[] args) {
        Type type = FastJsonTest.class;
        Class<?> clazz = (Class<?>) type;
        //获取注解
        JSONType annotation = clazz.getAnnotation(JSONType.class);
        System.out.println(annotation);
        //类修饰符,public,private,abstract等
        System.out.println(clazz.getModifiers());
        //返回泛型变量
        System.out.println(clazz.getTypeParameters().length);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map jsonToMap(String json) {
        return JSON.parseObject(json);
    }

    private abstract class FastJsonTest1 {
    }
}
