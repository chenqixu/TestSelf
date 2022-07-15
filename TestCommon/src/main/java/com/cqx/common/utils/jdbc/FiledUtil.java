package com.cqx.common.utils.jdbc;

import java.util.ArrayList;
import java.util.List;

/**
 * 字段工具
 *
 * @author chenqixu
 */
public class FiledUtil {
    private List<String> vals = new ArrayList<>();
    private StringBuilder stringBuilder = new StringBuilder();

    public void add(int current, int allSize, String value) {
        vals.add(value);
        stringBuilder.append(value);
        if (current < allSize - 1) {
            stringBuilder.append(",");
        }
    }

    public List<String> getVals() {
        return vals;
    }

    public String[] getValsArray() {
        String[] array = {};
        return vals.toArray(array);
    }

    public String getStringBuilder() {
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return getStringBuilder();
    }
}
