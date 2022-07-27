package com.cqx.services.work;

/**
 * 搬砖工人
 *
 * @author chenqixu
 */
public class Bricklayer implements WorkInf {

    static {
        WorkFactory.registerWork(new Bricklayer());
    }

    @Override
    public String doWork(String tag) {
        if (tag.startsWith("work:bricklayer://"))
            return "搬砖工人";
        return null;
    }
}
