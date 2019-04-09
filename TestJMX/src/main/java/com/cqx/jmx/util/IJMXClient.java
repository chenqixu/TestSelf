package com.cqx.jmx.util;

/**
 * IJMXClient
 *
 * @author chenqixu
 */
public interface IJMXClient {
    <T> T getAttributeByName(String attribute);
    <T> void setAttributeByName(String attribute, String type, T t);
    <T> T invoke(String operationName, Object[] params, String[] signature);
}
