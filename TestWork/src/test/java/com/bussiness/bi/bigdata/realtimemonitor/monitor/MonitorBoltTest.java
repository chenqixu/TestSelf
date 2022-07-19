package com.bussiness.bi.bigdata.realtimemonitor.monitor;

import org.junit.Test;

public class MonitorBoltTest {

    @Test
    public void monitor() throws Exception {
        MonitorBolt monitorBolt = new MonitorBolt();
        monitorBolt.monitor("d:\\tmp\\data\\xdr\\monitor.txt");
    }
}