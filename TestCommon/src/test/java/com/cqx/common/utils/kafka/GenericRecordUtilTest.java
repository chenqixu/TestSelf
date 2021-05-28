package com.cqx.common.utils.kafka;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GenericRecordUtilTest {
    private GenericRecordUtil genericRecordUtil;

    @Before
    public void setUp() throws Exception {
        genericRecordUtil = new GenericRecordUtil("http://10.1.8.203:19090/nl-edc-cct-sys-ms-dev/SchemaService/getSchema?t=");
    }

    @Test
    public void genericRandomRecordByAvroRecord() {
        genericRecordUtil.addTopic("USER_PRODUCT");
    }
}