package com.newland.bi.mobilebox.bean;

/**
 * 信息体
 *
 * @author chenqixu
 */
public class BodyInfo {
    private Object body;

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        if (body instanceof DeviceInfo)
            return ((DeviceInfo) body).toString();
        else
            return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }
}
