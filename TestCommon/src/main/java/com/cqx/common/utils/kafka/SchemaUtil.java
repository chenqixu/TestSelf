package com.cqx.common.utils.kafka;

import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * SchemaUtil
 *
 * @author chenqixu
 */
public class SchemaUtil {

    private static Logger logger = LoggerFactory.getLogger(SchemaUtil.class);
    private String urlStr;

    public SchemaUtil(String urlStr) {
        this.urlStr = urlStr;
        logger.info("urlStr：{}", urlStr);
    }

    /**
     * 传入话题名称，通过springboot服务来获取对应的schema
     *
     * @param topic
     * @return
     */
    public Schema getSchemaByTopic(String topic) {
        return new Schema.Parser().parse(readUrlContent(topic));
    }

    /**
     * 传入schema字符串，直接解析成schema对象
     *
     * @param str
     * @return
     */
    public Schema getSchemaByString(String str) {
        return new Schema.Parser().parse(str);
    }

    /**
     * 通过springboot服务来获取对应的schema
     *
     * @param topic
     * @return
     */
    public String readUrlContent(String topic) {
        StringBuffer contentBuffer = new StringBuffer();
        try {
            BufferedReader reader = null;
            URL url = new URL(urlStr + topic);
            logger.info("{} url：{}", topic, urlStr + topic);
            URLConnection con = url.openConnection();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String tmpStr;
            while ((tmpStr = reader.readLine()) != null) {
                contentBuffer.append(tmpStr);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("{} schema：{}", topic, contentBuffer.toString());
        return contentBuffer.toString();
    }
}
