package com.cqx.common.utils.file;

/**
 * RAFBean
 *
 * @author chenqixu
 */
public class RAFBean<T> {
    private RAFBeanEnum rafBeanEnum = RAFBeanEnum.CONTENT;
    private T t;

    public RAFBean(T t) {
        this.t = t;
    }

    public void setEnd() {
        rafBeanEnum = RAFBeanEnum.END;
    }

    public boolean isEnd() {
        return rafBeanEnum.equals(RAFBeanEnum.END);
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
