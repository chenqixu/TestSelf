package com.bussiness.bi.bigdata.realtimemonitor.rule;

import java.util.ArrayList;
import java.util.List;

/**
 * RuleLinked
 *
 * @author chenqixu
 */
public class RuleLinked {
    private List<IMonitorRule> iMonitorRuleList = new ArrayList<>();
    private RuleLinked next;

    public RuleLinked addRuleChild(IMonitorRule iMonitorRule) {
        this.iMonitorRuleList.add(iMonitorRule);
        return this;
    }

    public RuleLinked addNext() {
        this.next = new RuleLinked();
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
        for (IMonitorRule iMonitorRule : iMonitorRuleList) {
            boolean match = iMonitorRule.check(value);
            if (match) return true;
        }
        return false;
    }
}
