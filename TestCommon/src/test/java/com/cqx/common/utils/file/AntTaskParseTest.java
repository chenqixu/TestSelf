package com.cqx.common.utils.file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AntTaskParseTest
 *
 * @author chenqixu
 */
public class AntTaskParseTest {
    private static final Logger logger = LoggerFactory.getLogger(AntTaskParseTest.class);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void ant_log() throws IOException {
        FileUtil fileUtil = new FileUtil();
        String filename = "d:\\Work\\实时\\实时中台\\监控\\ant_task.log";
        try {
            fileUtil.setReader(filename);
            fileUtil.read(new FileCount() {
                @Override
                public void run(String content) throws IOException {
                    String[] content_array = content.split("\t", -1);
                    String task_name = content_array[0];
                    String value = content_array[1];
                    StringBuilder sbValue = new StringBuilder(value);
                    // 可能有多个话题和消费组
                    while (true) {
                        String topic = findStr(sbValue, "'topic' = '");
                        if (topic.length() == 0) break;
                        String group_idx = findStr(sbValue, "'properties.group.id' = '");
//                        AntTaskBean antTaskBean = JSON.parseObject(content_array[1], AntTaskBean.class);
                        if (group_idx.length() > 0) logger.info("{}, {}, {}", task_name, topic, group_idx);
                    }
                }
            });
        } finally {
            fileUtil.closeRead();
        }
    }

    @Test
    public void jsonTest() {
        Map<String, String> m1 = new HashMap<>();
        m1.put("default", "123");
        logger.info("{}", JSON.toJSONString(m1));
        String data = "{default={\"city\":\"0592\"}}";
//        JSONObject jsonObject = JSON.parseObject(data);
//        JsonTestBean jtb = JSON.parseObject(data, JsonTestBean.class);
//        logger.info("{}", jsonObject);

        List<AntTaskBean> list = new ArrayList<>();
        AntTaskBean a1 = new AntTaskBean();
        a1.setClusterName("t");
        a1.setRunType("run");
        a1.setSqls("select 1 from dual");
        a1.setTaskDate("2023-11-24");
        a1.setUserGroupInfo("yz_newland");
        list.add(a1);
        logger.info("{}", JSON.toJSONString(list));

        String source = "http://www.baidu.com/a=123";
        logger.info("index={}", source.indexOf("http://www.baidu.com"));
    }

    private String findStr(StringBuilder source, String key) {
        int index1 = source.indexOf(key);
        if (index1 > 0) {
            int index2 = source.indexOf("'", index1 + key.length() + 1);
            String ret = source.substring(index1 + key.length(), index2);
            source.delete(0, index2);
            return ret;
        }
        return "";
    }
}
