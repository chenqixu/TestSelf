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

    /**
     * <H2>添加数据</H2><br>
     * <pre>
     *      ################################
     *      #oracle
     *      ################################
     *      FiledUtil insert_values = new FiledUtil();
     *      insert_values.add(0, 4, "'task_001'");
     *      insert_values.add(1, 4, "'fuzhou'");
     *      insert_values.add(2, 4, "to_date('2022-07-15 09:49:00','yyyy-MM-dd hh24:mi:ss')");
     *      insert_values.add(3, 4, "1");
     *
     *      ################################
     *      #adb
     *      ################################
     *      FiledUtil insert_values = new FiledUtil();
     *      insert_values.add(0, 4, "'task_001'");
     *      insert_values.add(1, 4, "'fuzhou'");
     *      insert_values.add(2, 4, "'2022-07-15 09:49:00'");
     *      insert_values.add(3, 4, "1");
     * </pre>
     *
     * @param current 当前index，从0开始，自增1
     * @param allSize 总大小
     * @param value   数据的值，varchar类型需要加上''，时间字段视对应的数据库而定，有的数据库支持''，有的数据库必须to_date或to_timestamp
     */
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
