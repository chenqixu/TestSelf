package com.cqx.common.utils.file;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
                    String topic = findStr(value, "'topic' = '");
                    String group_idx = findStr(value, "'properties.group.id' = '");
//                    AntTaskBean antTaskBean = JSON.parseObject(content_array[1], AntTaskBean.class);
                    logger.info("{}, {}, {}", task_name, topic, group_idx);
                }
            });
        } finally {
            fileUtil.closeRead();
        }
    }

    private String findStr(String source, String key) {
        int index1 = source.indexOf(key);
        if (index1 > 0) {
            int index2 = source.indexOf("'", index1 + key.length() + 1);
            return source.substring(index1 + key.length(), index2);
        }
        return "";
    }
}
