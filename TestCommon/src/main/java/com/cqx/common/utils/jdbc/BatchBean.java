package com.cqx.common.utils.jdbc;

import java.lang.reflect.Method;

/**
 * BatchBean
 *
 * @author chenqixu
 */
public class BatchBean {
    private String name;
    private Class<?> srccls;
    private Class<?> dstcls;
    private Method method;

    public BatchBean(String name, Method method) {
        this.name = name;
        this.method = method;
    }

    public BatchBean(String name, Class<?> srccls, Class<?> dstcls, Method method) {
        this.name = name;
        this.srccls = srccls;
        this.dstcls = dstcls;
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getSrccls() {
        return srccls;
    }

    public void setSrccls(Class<?> srccls) {
        this.srccls = srccls;
    }

    public Class<?> getDstcls() {
        return dstcls;
    }

    public void setDstcls(Class<?> dstcls) {
        this.dstcls = dstcls;
    }

}
