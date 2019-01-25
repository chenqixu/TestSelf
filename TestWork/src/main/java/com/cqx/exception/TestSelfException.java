package com.cqx.exception;

import com.cqx.exception.base.BaseException;
import com.cqx.exception.base.ErrorCode;
import com.cqx.exception.base.ExceptionParse;

/**
 * 自定义工程异常
 *
 * @author chenqixu
 */
public class TestSelfException extends BaseException {
    public TestSelfException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public TestSelfException(ErrorCode errorCode, String message, Object... objs) {
        super(errorCode, ExceptionParse.parse(message, objs));
    }
}
