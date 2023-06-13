package com.cqx.kafka8;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class Kafka8UtilTest {
    private Kafka8Util kafka8Util;

    @Before
    public void setUp() throws IOException {
        kafka8Util = new Kafka8Util();
        kafka8Util.init("test2", "d:\\tmp\\data\\avro\\test1.avsc");
    }

    @Test
    public void producer() throws IOException {
        kafka8Util.producer("test2");
    }

    @Test
    public void consumer() {
        kafka8Util.consumer("test2");
    }
}