package com.cqx.common.utils.net.asnone.cdr;

import java.util.HashMap;
import java.util.Map;

/**
 * Bytes To Enumerated
 *
 * @author chenqixu
 */
public class EnumeratedRule extends IntegerRule {

    private Map<String, String> enumerated = new HashMap<>();

    public void addEnum(String key, String value) {
        enumerated.put(key, value);
    }

    @Override
    public String parse(byte[] bytes) {
        return enumerated.get(super.parse(bytes));
    }
}
