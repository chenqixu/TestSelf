package com.bussiness.bi.bigdata.ogg;

import org.junit.Test;

import java.io.IOException;

public class OggJsonTest {
    private OggJson oggJson = new OggJson();

    @Test
    public void check() throws IOException {
        oggJson.check();
    }

    @Test
    public void readSchemaJson() throws IOException {
        oggJson.readSchemaJson("FRTBASE.TB_SER_OGG_CC_FLOW_ORDER_MONITOR");
    }
}