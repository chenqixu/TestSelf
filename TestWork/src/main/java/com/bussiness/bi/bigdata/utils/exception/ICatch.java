package com.bussiness.bi.bigdata.utils.exception;

/**
 * ICatch
 *
 * @author chenqixu
 */
public interface ICatch {
    void catchEvent(Exception e);
    void release();
}
