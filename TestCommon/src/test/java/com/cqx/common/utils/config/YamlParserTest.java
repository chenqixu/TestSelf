package com.cqx.common.utils.config;

import com.cqx.common.test.TestBase;
import com.cqx.common.utils.jdbc.DBBean;
import com.cqx.common.utils.jdbc.ParamsParserUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class YamlParserTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(YamlParserTest.class);

    @Test
    public void dump() throws IOException {
        YamlParser yamlParser = YamlParser.builder();
        Map jdbcParam = getParam("jdbc.yaml");
        yamlParser.dump(jdbcParam, "d:\\tmp\\data\\yaml\\jdbc.yaml");
        Map kafkaParam = getParam("kafka.yaml");
        yamlParser.dump(kafkaParam, "d:\\tmp\\data\\yaml\\kafka.yaml");
    }

    @Test
    public void compare() throws IOException {
        YamlParser yamlParser = YamlParser.builder();
        Map jdbcParam = yamlParser.parserConfToMap("d:\\tmp\\data\\yaml\\jdbc.yaml");
        ParamsParserUtil paramsParserUtil = new ParamsParserUtil(jdbcParam);
        DBBean dbBean = paramsParserUtil.getBeanMap().get("adbBean");
        logger.info("dbBean：{}", dbBean);
    }

    @Test
    public void dumpToString() throws IOException {
        YamlParser yamlParser = YamlParser.builder();
        Map jdbcParam = yamlParser.parserConfToMap("d:\\tmp\\data\\yaml\\jdbc.yaml");
        logger.info("jdbcParam：\n{}", yamlParser.dump(jdbcParam));
    }
}