package com.bussiness.bi.bigdata.realtimemonitor.rule;

/**
 * IMonitorRule
 *
 * @author chenqixu
 */
public interface IMonitorRule {

    boolean check(String value) throws Exception;
}
