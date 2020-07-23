package com.cqx.utils;

import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * SchemaUtil
 *
 * @author chenqixu
 */
public class SchemaUtil {

    private static final Logger logger = LoggerFactory.getLogger(SchemaUtil.class);
    private static Map<String, String> schemaMap = new HashMap<>();

    static {
        String paramValStr = "{\n" +
                "\"namespace\": \"com.newland\",\n" +
                "\"type\": \"record\",\n" +
                "\"name\": \"lte_http\",\n" +
                "\"fields\":[\n" +
                "{\"name\": \"city_1\", \"type\": [\"string\"]},\n" +
                "{\"name\": \"imsi\", \"type\": [\"string\"]},\n" +
                "{\"name\": \"imei\", \"type\": [\"string\"] },\n" +
                "{\"name\": \"msisdn\", \"type\": [\"string\"]},\n" +
                "{\"name\": \"tac\", \"type\": [\"string\"]},\n" +
                "{\"name\": \"eci\", \"type\": [\"string\"]},\n" +
                "{\"name\": \"rat\", \"type\": [\"string\"]},\n" +
                "{\"name\": \"procedure_start_time\", \"type\": [\"string\"]},\n" +
                "{\"name\": \"app_class\", \"type\": [\"string\"]},\n" +
                "{\"name\": \"host\", \"type\": [\"string\"]},\n" +
                "{\"name\": \"uri\", \"type\": [\"string\"]},\n" +
                "{\"name\": \"apply_classify\", \"type\": [\"string\"]},\n" +
                "{\"name\": \"apply_name\", \"type\": [\"string\"]},\n" +
                "{\"name\": \"web_classify\", \"type\": [\"string\"]},\n" +
                "{\"name\": \"web_name\", \"type\": [\"string\"] },\n" +
                "{\"name\": \"search_keyword\", \"type\": [\"string\"]},\n" +
                "{\"name\": \"procedure_end_time\", \"type\": [\"string\"]},\n" +
                "{\"name\": \"upbytes\", \"type\": [\"string\"]},\n" +
                "{\"name\": \"downbytes\", \"type\": [\"string\"]}\n" +
                "]\n" +
                "}";
        schemaMap.put("nmc_tb_lte_http_test", paramValStr);
        paramValStr = "{\"namespace\": \"com.newland\",\"type\": \"record\",\"name\": \"DEL_HTTP_SESSION_OUT\",\"fields\":[{ \"name\": \"city\", \"type\": [\"string\", \"null\"] },{ \"name\": \"imsi\", \"type\": [\"string\", \"null\"] },{ \"name\": \"imei\",  \"type\": [\"string\", \"null\"] },{ \"name\": \"msisdn\", \"type\": [\"string\", \"null\"] },{ \"name\": \"lac\", \"type\": [\"string\", \"null\"] },{ \"name\": \"cid\", \"type\": [\"string\", \"null\"] },{ \"name\": \"start_time\", \"type\": [\"string\", \"null\"] },{ \"name\": \"apply_classify\",  \"type\": [\"string\", \"null\"] },{ \"name\": \"apply_name\",  \"type\": [\"string\", \"null\"] },{ \"name\": \"service_name\",  \"type\": [\"string\", \"null\"] },{ \"name\": \"uri\",  \"type\": [\"string\", \"null\"] },{ \"name\": \"web_classify\",  \"type\": [\"string\", \"null\"] },{ \"name\": \"web_name\",  \"type\": [\"string\", \"null\"] },{ \"name\": \"search_keyword\",  \"type\": [\"string\", \"null\"] },{ \"name\": \"host\",  \"type\": [\"string\", \"null\"] },{ \"name\": \"rat\",  \"type\": [\"string\", \"null\"] }]}";
        schemaMap.put("nmc_tb_gn_http_test", paramValStr);
        paramValStr = "{\n" +
                "\"namespace\": \"com.newland\",\n" +
                "\"type\": \"record\",\n" +
                "\"name\": \"ogg\",\n" +
                "\"fields\":[\n" +
                "{\"name\": \"id\", \"type\": [\"int\"]},\n" +
                "{\"name\": \"name\", \"type\": [\"string\"]},\n" +
                "{\"name\": \"sex\", \"type\": [\"boolean\"]}\n" +
                "]\n" +
                "}";
        schemaMap.put("ogg_to_kafka", paramValStr);
    }

    private String urlStr;

    public SchemaUtil() {
    }

    public SchemaUtil(String urlStr) {
        this.urlStr = urlStr;
    }

    public boolean isLocal() {
        return !(urlStr != null && urlStr.length() > 0);
    }

    public void addSchema(String topic, String schemaStr) {
        schemaMap.put(topic, schemaStr);
    }

    public String getSchemaValueByTopic(String topic) {
        return schemaMap.get(topic);
    }

    public Schema getSchemaByTopic(String topic) {
        return new Schema.Parser().parse(getSchemaValueByTopic(topic));
    }

    public Schema getSchemaByUrlTopic(String topic) {
        return new Schema.Parser().parse(readUrlContent(topic));
    }

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
