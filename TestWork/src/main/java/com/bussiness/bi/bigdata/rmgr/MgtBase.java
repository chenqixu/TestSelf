package com.bussiness.bi.bigdata.rmgr;

/**
 * MgtBase
 *
 * @author chenqixu
 */
public class MgtBase {
    public void printlnClassName() {
        String className = this.getClass().getSimpleName();
        String bchMgtClassName = BchMgt.class.getSimpleName();
        int status;
        if (className.equals(bchMgtClassName)) {
            status = 3;
        } else {
            status = 1;
        }
        System.out.println("className：" + className + "，status：" + status);
    }
}
