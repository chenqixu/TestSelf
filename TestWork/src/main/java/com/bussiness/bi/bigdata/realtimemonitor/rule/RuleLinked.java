package com.bussiness.bi.bigdata.realtimemonitor.rule;

/**
 * RuleLinked
 *
 * @author chenqixu
 */
public class RuleLinked {
    private IMonitorRule iMonitorRule;
    private RuleLinked next;

    public RuleLinked(IMonitorRule iMonitorRule) {
        this.iMonitorRule = iMonitorRule;
    }

    public RuleLinked addNext(IMonitorRule iMonitorRule) {
        this.next = new RuleLinked(iMonitorRule);
        return this.next;
    }

    public boolean hasNext() {
        return next != null;
    }

    public RuleLinked next() {
        if (hasNext()) return next;
        return null;
    }

    public boolean check(String value) throws Exception {
        return iMonitorRule.check(value);
    }
}
