package com.cqx.exception.base;

/**
 * 分布式采集组件异常
 *
 * @author huangxw
 * @date 2018-10-29
 */
public class DistCollectException extends BaseException {
    public DistCollectException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public DistCollectException(ErrorCode errorCode, Throwable throwable) {
        super(errorCode, throwable);
    }

    public DistCollectException(ErrorCode errorCode, String message, Throwable throwable) {
        super(errorCode, message, throwable);
    }
}
