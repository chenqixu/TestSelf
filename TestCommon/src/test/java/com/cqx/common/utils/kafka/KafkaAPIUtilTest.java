package com.cqx.common.utils.kafka;

import com.cqx.common.test.TestBase;
import org.junit.Before;
import org.junit.Test;

public class KafkaAPIUtilTest extends TestBase {
    private KafkaAPIUtil kafkaAPIUtil;

    @Before
    public void setUp() throws Exception {
        kafkaAPIUtil = new KafkaAPIUtil(getParam("kafka.yaml"));
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
}