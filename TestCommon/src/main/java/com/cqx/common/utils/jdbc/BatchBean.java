package com.cqx.common.utils.jdbc;

import com.cqx.common.annotation.DB_StrToClob;

import java.lang.reflect.Field;
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
    private boolean strToClob = false;

    public BatchBean(String name, Method method) {
        this(name, method, null);
    }

    public BatchBean(String name, Method method, Field field) {
        this(name, null, null, method, field);
    }

    public BatchBean(String name, Class<?> srccls, Class<?> dstcls, Method method) {
        this(name, srccls, dstcls, method, null);
    }

    public BatchBean(String name, Class<?> srccls, Class<?> dstcls, Method method, Field field) {
        this.name = name;
        this.srccls = srccls;
        this.dstcls = dstcls;
        this.method = method;
        if (field != null) {
            DB_StrToClob annotation = field.getAnnotation(DB_StrToClob.class);
            if (annotation != null) {
                this.strToClob = true;
                this.name = "java.sql.Clob";
            }
        }
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

    public boolean isStrToClob() {
        return strToClob;
    }

    public void setStrToClob(boolean strToClob) {
        this.strToClob = strToClob;
    }
}
