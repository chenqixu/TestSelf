package com.cqx.common.utils.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.cqx.common.utils.jdbc.bean.FB1;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FlatUtilTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void jsonArray() {
        // 构造对象
        FB1 fb1 = new FB1();
        fb1.setTagId("123");
        fb1.setTagName("123test");

        // 构造Json数组
        JSONArray ja = new JSONArray();
        ja.add(fb1);

        // 序列化
        // [{"tagId":"123","tagName":"123test"}]
        String jaStr = JSONArray.toJSONString(ja);
        System.out.println(String.format("序列化=%s", jaStr));

        // 反序列化
        JSONArray jb = JSONArray.parseArray(jaStr);
        for (Object o : jb) {
            System.out.println(String.format("反序列化=%s", o));
        }
    }

    @Test
    public void jsonList() {
        // 构造对象
        FB1 fb1 = new FB1();
        fb1.setTagId("123");
        fb1.setTagName("123test");

        // 转成Json字符串写入List
        List<String> listFb1 = new ArrayList<>();
        listFb1.add(JSON.toJSONString(fb1));

        // List转成Json做最后序列化
        // ["{\"tagId\":\"123\",\"tagName\":\"123test\"}"]
        String listJsonStr = JSON.toJSONString(listFb1);
        System.out.println(String.format("序列化=%s", listJsonStr));

        // 反序列化
        List<String> list1 = JSON.parseObject(listJsonStr, new TypeReference<List<String>>() {
        });
        System.out.println(String.format("反序列化=%s", list1));
    }
}