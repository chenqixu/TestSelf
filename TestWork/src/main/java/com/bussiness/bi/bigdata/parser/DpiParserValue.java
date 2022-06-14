package com.bussiness.bi.bigdata.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * DpiParserValue
 *
 * @author chenqixu
 */
public class DpiParserValue {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String TAB = "\t";
    private String sourceField;
    private String rtmField;
    private String[] sourceFieldArr;
    private String[] rtmFieldArr;
    private Map<String, Integer> rtmFieldMap = new HashMap<>();

    public void init() {
        if (sourceField != null) sourceFieldArr = sourceField.split(",", -1);
        if (rtmField != null) {
            rtmFieldArr = rtmField.split(",", -1);
            rtmFieldMap.clear();
            for (int i = 0; i < rtmFieldArr.length; i++) {
                rtmFieldMap.put(rtmFieldArr[i], i + 1);
            }
        }
    }

    /**
     * 输入源字段、入kafka字段，输出入kafka字段在源字段中的顺序
     */
    public void printKafkaField() {
        for (String str : sourceFieldArr) {
            Integer index = rtmFieldMap.get(str);
            String result =
                    str + TAB + (index == null ? "否" : "是") + TAB + (index == null ? "" : index + "");
            System.out.println(result);
        }
    }

    public void printRuleField() {
        StringBuffer sb = new StringBuffer();
        for (String str : sourceFieldArr) {
            Integer index = rtmFieldMap.get(str);
            String result =
                    str + TAB + (index == null ? "" : str);
            sb.append(index == null ? "" : str);
            sb.append(",");
            System.out.println(result);
        }
        sb.deleteCharAt(sb.length() - 1);
//        System.out.println(sb.toString());
    }

    public void setSourceField(String sourceField) {
        this.sourceField = sourceField;
    }

    public void setRtmField(String rtmField) {
        this.rtmField = rtmField;
    }
}
