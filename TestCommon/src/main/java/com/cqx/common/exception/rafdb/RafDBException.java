package com.cqx.common.exception.rafdb;

import com.cqx.common.exception.base.IThrowable;

/**
 * RafDBException
 *
 * @author chenqixu
 */
public class RafDBException extends Exception implements IThrowable {

    public RafDBException(String msg) {
        super(msg);
    }
}
