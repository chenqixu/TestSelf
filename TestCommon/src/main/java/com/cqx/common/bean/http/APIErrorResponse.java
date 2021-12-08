package com.cqx.common.bean.http;

public class APIErrorResponse {
    private String errorCode; // 可选 错误码
    private String errorMessage; // 可选 错误描述， 命令执行失败时返回的错误信息

    public String getErrorCode() {return errorCode;}
    public void setErrorCode(String errorCode) {this.errorCode=errorCode;}
    public String getErrorMessage() {return errorMessage;}
    public void setErrorMessage(String errorMessage) {this.errorMessage=errorMessage;}

}