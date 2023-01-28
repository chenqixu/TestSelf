package com.cqx.common.utils.string;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

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
}
