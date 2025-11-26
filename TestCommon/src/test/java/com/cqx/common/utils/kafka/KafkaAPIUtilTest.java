package com.cqx.common.utils.kafka;

import com.cqx.common.test.TestBase;
import org.junit.Before;
import org.junit.Test;

public class KafkaAPIUtilTest extends TestBase {
    private KafkaAPIUtil kafkaAPIUtil;

    @Before
    public void setUp() throws Exception {
        kafkaAPIUtil = new KafkaAPIUtil(getParam("kafka_2.13-3.2.0-scram.yaml"));
    }

    @Test
    public void consumerGroupCommand() {
    }

    @Test
    public void GetOffsetShell() {
        kafkaAPIUtil.GetOffsetShell("10.1.8.200:9094"
                , "nl_sip_test_v1"
                , "-1"
                , "D:\\Document\\Workspaces\\Git\\TestSelf\\TestCommon\\src\\test\\resources\\consumer-2.13_3.2.0.properties"
        );
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
    public void topicCommand() {
        kafkaAPIUtil.listTopic("10.1.8.200:9094");
    }
}