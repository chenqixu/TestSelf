package com.cqx.services.work;

/**
 * 程序员
 *
 * @author chenqixu
 */
public class Programmer implements WorkInf {

    static {
        WorkFactory.registerWork(new Programmer());
    }

    @Override
    public String doWork(String tag) {
        if (tag.startsWith("work:programmer://"))
            return "程序员";
        return null;
    }
}
