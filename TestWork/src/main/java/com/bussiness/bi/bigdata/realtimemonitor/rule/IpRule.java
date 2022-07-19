package com.bussiness.bi.bigdata.realtimemonitor.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IpRule
 *
 * @author chenqixu
 */
public class IpRule implements IMonitorRule {
    private static final Logger logger = LoggerFactory.getLogger(IpRule.class);
    private String ip;

    public IpRule(String ip) {
        this.ip = ip;
    }

    @Override
    public boolean check(String value) throws Exception {
        boolean check = value.equals(ip);
        logger.info(String.format("值: %s, ip规则: %s, 校验结果: %s", value, ip, check));
        return check;
    }
}
