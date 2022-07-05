package com.cqx.common.utils.net.asnone.cdr;

import java.util.Map;

/**
 * ASNOneBeanParser
 *
 * @author chenqixu
 */
public class ASNOneBeanParser {

    public static void parser(ASNOneBean data, ASNOneBean rule) throws Exception {
        if (rule.isLeaf()) {
            rule.setValue(data.getValue());
            rule.calcValue();
        } else if (rule.hasChild()) {
            for (Map.Entry<Integer, ASNOneBean> entry : rule.childEntrySet()) {
                ASNOneBean tmpData = data.getChild(entry.getKey());
                if (tmpData != null) parser(tmpData, entry.getValue());
            }
        }
    }
}
