package com.bussiness.bi.bigdata.realtimemonitor.rule;

/**
 * RuleUtil
 *
 * @author chenqixu
 */
public class RuleUtil {
    private RuleLinked ruleLinked;

    public RuleUtil() {
        init();
    }

    public void init() {
        ruleLinked = new RuleLinked().addRuleChild(new IpRule("192.168.1.0"));
        ruleLinked.addNext().addRuleChild(new IpRule("192.168.1.1"))
                .addNext().addRuleChild(new UrlRule("www.baidu.com"))
                .addNext().addRuleChild(new IpRule("192.168.1.2"))
        ;
    }

    public boolean check(String value) throws Exception {
        return check(value, ruleLinked);
    }

    private boolean check(String value, RuleLinked ruleLinked) throws Exception {
        if (ruleLinked == null) {
            return false;
        } else {
            boolean check = ruleLinked.check(value);
            if (!check) {
                RuleLinked next = ruleLinked.next();
                return check(value, next);
            } else {
                return true;
            }
        }
    }
}
