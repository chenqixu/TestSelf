package com.cqx.common.utils.string;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * JsonTest
 *
 * @author chenqixu
 */
public class JsonTest {

    @Test
    public void duo() {
        JsonBean jsonBean = new JsonBean();
        jsonBean.setId("123");
        jsonBean.setName("test1");
        String str = JSON.toJSONString(jsonBean);
        str = str.replace("}", ",\"key\":\"k1\"}");
        System.out.println(str);
        JsonBean js = JSON.parseObject(str, JsonBean.class);
        System.out.println(js.getId());
        System.out.println(js.getName());
    }

    @Test
    public void shao() {
        JsonBean jsonBean = new JsonBean();
        jsonBean.setId("123");
        jsonBean.setName("test1");
        String str = JSON.toJSONString(jsonBean);
        str = str.replace(",\"name\":\"test1\"}", "}");
        System.out.println(str);
        JsonBean js = JSON.parseObject(str, JsonBean.class);
        System.out.println(js.getId());
        System.out.println(js.getName());
    }

    @Test
    public void MapToJson() {
        Map<String, String> map = new HashMap<>();
        map.put("teamid", "123");
        System.out.println(JSON.toJSONString(map));// {"teamid":"123"}
        String jsonStr = "{\"teamid\":\"123\"}";
        Map<String, String> jsonMap = JSON.parseObject(jsonStr, Map.class);
        System.out.println(jsonMap);
    }
}
