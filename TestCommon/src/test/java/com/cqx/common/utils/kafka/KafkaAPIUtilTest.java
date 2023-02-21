package com.cqx.common.utils.kafka;

import com.cqx.common.test.TestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class KafkaAPIUtilTest extends TestBase {
    private KafkaAPIUtil kafkaAPIUtil;

    @Before
    public void setUp() throws Exception {
        kafkaAPIUtil = new KafkaAPIUtil((Map) getParam("kafka_scram.yaml").get("param"));
    }

    @Test
    public void consumerGroupCommand() {
    }

    @Test
    public void aclCommand() {
        kafkaAPIUtil.AclCommandByTopic(
                "edc-mqc-01:2181"
                , "USER_PRODUCT"
        );

        kafkaAPIUtil.AclCommandByGroup(
                "edc-mqc-01:2181"
                , "new_consumer_api"
        );
    }

    @Test
    public void listTopicByAPI() throws Exception {
        kafkaAPIUtil.listTopicByAPI();
    }

    @Test
    public void listTopicByCommand() throws Exception {
        kafkaAPIUtil.listTopicByCommand();
    }
}