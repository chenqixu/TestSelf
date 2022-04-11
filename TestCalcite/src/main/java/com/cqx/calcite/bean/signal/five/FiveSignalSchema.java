package com.cqx.calcite.bean.signal.five;

/**
 * 5G信令Schema
 *
 * @author chenqixu
 */
public class FiveSignalSchema {
    public N1N2[] n1n2 = {
            new N1N2(1440509800000L),
            new N1N2(1440609800000L),
            new N1N2(1440609800001L),
            new N1N2(1440609999999L),
            new N1N2(1440709999999L)
    };

    public N5[] n5 = {
            new N5("CMNET1", 128),
            new N5("CMWAP2", 125),
            new N5("IMS3", 126),
            new N5("CMDTJ4", 127),
            new N5("NET5", 128)
    };

    public void setN1n2(N1N2[] n1n2) {
        this.n1n2 = n1n2;
    }
}
