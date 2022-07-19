package com.bussiness.bi.bigdata.realtimemonitor.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UrlRule
 *
 * @author chenqixu
 */
public class UrlRule implements IMonitorRule {
    private static final Logger logger = LoggerFactory.getLogger(UrlRule.class);
    private String url;

    public UrlRule(String url) {
        this.url = url;
    }

    @Override
    public boolean check(String value) throws Exception {
        boolean check = value.equals(url);
        logger.info(String.format("值: %s, url规则: %s, 校验结果: %s", value, url, check));
        return check;
    }
}
