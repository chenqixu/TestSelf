package com.cqx.concurrent;

/**
 * 高并发执行接口
 *
 * @author chenqixu
 */
public abstract class IBolt implements Cloneable {
    public abstract void init();

    public abstract void exec();

    @Override
    public Object clone() throws CloneNotSupportedException {
        Object object = super.clone();
        return object;
    }
}
