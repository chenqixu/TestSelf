package com.cqx.common.utils.exception;

/**
 * RuntimeExceptionTest
 *
 * @author chenqixu
 */
public class RuntimeExceptionTest extends RuntimeException {

    public RuntimeExceptionTest() {
        super();
    }

    public RuntimeExceptionTest(String message) {
        super(message);
    }

    public RuntimeExceptionTest(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeExceptionTest(Throwable cause) {
        super(cause);
    }
}
