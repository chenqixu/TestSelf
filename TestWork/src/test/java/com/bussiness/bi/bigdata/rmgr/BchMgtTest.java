package com.bussiness.bi.bigdata.rmgr;

import com.bussiness.bi.bigdata.rmgr.BchMgt;
import com.bussiness.bi.bigdata.rmgr.KafkaMgt;
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