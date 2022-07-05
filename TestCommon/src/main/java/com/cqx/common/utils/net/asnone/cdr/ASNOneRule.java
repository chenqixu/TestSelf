package com.cqx.common.utils.net.asnone.cdr;

/**
 * ASNOneRule
 *
 * @author chenqixu
 */
public interface ASNOneRule {

    String parse(byte[] bytes) throws Exception;
}
