package com.newland.bi.bigdata.rmgr;

import org.junit.Test;

public class BchMgtTest {

    @Test
    public void getClassName() {
        BchMgt bchMgt = new BchMgt();
        bchMgt.printlnClassName();
        KafkaMgt kafkaMgt = new KafkaMgt();
        kafkaMgt.printlnClassName();
    }
}