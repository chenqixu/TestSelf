package com.cqx.exception.base;

import java.io.PrintStream;
import java.io.PrintWriter;

public abstract class BaseException extends Exception implements IThrowable {
    /**
     * the Throwable
     */
    private Throwable throwable;

    /**
     * 错误编码
     */
    private ErrorCode errorCode;

    /**
     * Construct a new BaseException instance.
     *
     * @param errorCode The error code
     * @param message   The detail message for this exception.
     */
    public BaseException(final ErrorCode errorCode, final String message) {
//        super(errorCode.getCode() + ": " + errorCode.getDesc() + " " + message);
      super(errorCode + " " + message);
//        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Construct a new BaseException instance.
     *
     * @param errorCode The error code
     * @param throwable The root cause of the exception
     */
    public BaseException(final ErrorCode errorCode, final Throwable throwable) {
//    	super(errorCode.getCode() + ": " + errorCode.getDesc() + " " + throwable.getMessage(), throwable);
    	super(errorCode + " " + throwable.getMessage(), throwable);
//        super(throwable.getMessage(), throwable);
        this.errorCode = errorCode;
        this.throwable = throwable;
    }

    /**
     * Construct a new BaseException instance.
     *
     * @param errorCodeerrorCode The error code
     * @param message   The detail message for this exception.
     * @param throwable The root cause of the exception
     */
    public BaseException(final ErrorCode errorCode, final String message, final Throwable throwable) {
//    	super(errorCode.getCode() + ": " + errorCode.getDesc() + " " + message, throwable);
    	super(errorCode + " " + message, throwable);
//        super(message, throwable);
        this.errorCode = errorCode;
        this.throwable = throwable;
    }

    /**
     * Retrieve root cause of the exception.
     *
     * @return the root cause
     */
    @Override
    public final Throwable getCause() {
        return this.throwable;
    }

    /**
     * Get error code
     *
     * @return the error code
     */
    public ErrorCode getCode() {
        return this.errorCode;
    }

    /**
     * Convert the Exception with the nested exceptions to a string.
     *
     * @return a string representation of the exception tree.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append(super.toString());

        if (getCause() != null) {
            s.append(": ");
            s.append(getCause().toString());
        }

        return s.toString();
    }

    /**
     * print the stack trace for this Exception and all nested Exceptions.
     */
    @Override
    public void printStackTrace() {
        super.printStackTrace();

        if (getCause() != null) {
            getCause().printStackTrace();
        }
    }

    /**
     * print the stack trace for this Exception and all nested Exceptions to the PrintStream.
     *
     * @param s a PrintStream object.
     */
    @Override
    public void printStackTrace(final PrintStream s) {
        super.printStackTrace(s);

        if (getCause() != null) {
            getCause().printStackTrace(s);
        }
    }

    /**
     * print the stack trace for this Exception and all nested Exceptions. to the PrintWriter.
     *
     * @param s a PrintWriter object.
     */
    @Override
    public void printStackTrace(final PrintWriter s) {
        super.printStackTrace(s);

        if (getCause() != null) {
            getCause().printStackTrace(s);
        }
    }
}