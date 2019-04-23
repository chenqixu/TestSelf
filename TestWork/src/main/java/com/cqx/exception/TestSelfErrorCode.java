package com.cqx.exception;

import com.cqx.exception.base.ErrorCode;

/**
 * 错误编码
 *
 * @author chenqixu
 */
public enum TestSelfErrorCode implements ErrorCode {
    ZK_CLIENT_NULL("tse-001", "zookeeper客户端没有连接"),
    ZK_NOT_EXIST_PATH("tse-002", "路径不存在"),
    DATA_MISMATCH("tse-003", "数据不匹配"),
    ;

    private final String code;
    private final String desc;

    private TestSelfErrorCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public String toString() {
        return String.format("Code:[%s], Describe:[%s]", this.code, this.desc);
    }
}
