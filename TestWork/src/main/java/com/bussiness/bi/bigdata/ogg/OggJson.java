package com.bussiness.bi.bigdata.ogg;

import com.alibaba.fastjson.JSON;
import com.cqx.common.utils.file.FileCount;
import com.cqx.common.utils.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * OggJson
 *
 * @author chenqixu
 */
public class OggJson {
    private static final Logger logger = LoggerFactory.getLogger(OggJson.class);

    public void check() throws IOException {
        FileUtil fileUtil = new FileUtil();
        try {
            fileUtil.setReader("d:\\Work\\实时\\实时中台\\data\\mukafka.log");
            fileUtil.read(new FileCount() {
                @Override
                public void run(String content) throws IOException {
                    if (content.contains("MuKafkaConsumerTool")) {
                        String key2 = "[topic-partition]=";
                        String key3 = "[offset]=";
                        String key4 = "[key]=";
                        String key5 = "[value]=";
                        int index2 = content.indexOf(key2);
                        int index3 = content.indexOf(key3);
                        int index4 = content.indexOf(key4);
                        int index5 = content.indexOf(key5);
                        String _c2 = content.substring(index2 + key2.length(), index3);
                        String _c3 = content.substring(index3 + key3.length(), index4);
                        String _c4 = content.substring(index4 + key4.length(), index5);
                        String _c5 = content.substring(index5 + key5.length());
                        logger.info("{}{}", key4, _c4);
                        // 获取after里所有字段
                        OggJsonBean oggJsonBean = JSON.parseObject(_c5, OggJsonBean.class);
                        Map<String, Object> after = oggJsonBean.getAfter();
                        for (Map.Entry<String, Object> entry : after.entrySet()) {
                            if (entry.getValue() == null) {
                                entry.setValue(new Object());
                            }
                        }
                        // 读取.schema.json
                        Map<String, Object> schemaJson = readSchemaJson(_c4.trim());
                        // schema和after做比较，正常是schema多
                        for (String key : after.keySet()) {
                            if (schemaJson.get(key) == null) {
                                logger.info("[schemaJson] {} is not in schemaJson", key);
                            }
                        }
                        // after和schema做比较，正常会少一点
                        for (String key : schemaJson.keySet()) {
                            if (after.get(key) == null) {
                                logger.info("[after] {} is not in after", key);
                            }
                        }
                    }
                }
            });
        } finally {
            fileUtil.closeRead();
        }
    }

    public Map<String, Object> readSchemaJson(String fileName) throws IOException {
        FileUtil fileUtil = new FileUtil();
        final StringBuilder sb = new StringBuilder();
        try {
            fileUtil.setReader("d:\\Work\\实时\\实时中台\\ogg入db\\ogg信息\\" + fileName + ".schema.json");
            fileUtil.read(new FileCount() {
                @Override
                public void run(String content) throws IOException {
                    sb.append(content);
                }
            });
        } finally {
            fileUtil.closeRead();
        }
        OggJsonSchema oggJsonSchema = JSON.parseObject(sb.toString(), OggJsonSchema.class);
        return oggJsonSchema.getDefinitions().getRow().getProperties();
    }
}
