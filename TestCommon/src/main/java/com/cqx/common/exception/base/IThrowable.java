package com.cqx.common.exception.base;

public interface IThrowable {
    /**
     * The cause of the Throwable.
     *
     * @return Throwable throwable
     */
    public Throwable getCause();
}