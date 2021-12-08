package com.cqx.common.bean.http;

import java.util.List;

/**
 * ResponseMessage
 *
 * @author chenqixu
 */
public class ResponseMessage<T> {
    private int status;
    private T body;
    private List<T> bodyList;
    private boolean isSuccess;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public List<T> getBodyList() {
        return bodyList;
    }

    public void setBodyList(List<T> bodyList) {
        this.bodyList = bodyList;
    }
}
