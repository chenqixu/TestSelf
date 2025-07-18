package com.bussiness.bi.bigdata.label.rule;

/**
 * 类型
 *
 * @author chenqixu
 */
public class SubTypeBean {
    private String type;
    private String name;
    private String val;
    private String java_type;

    public SubTypeBean() {
    }

    public SubTypeBean(String[] arr) {
        if (arr.length == 4) {
            setType(arr[0]);
            setName(arr[1]);
            setVal(arr[2]);
            setJava_type(arr[3]);
        } else if (arr.length == 2) {
            setType(arr[0]);
            setVal(arr[1]);
        } else if (arr.length > 4) {// 可能有list，不能单纯的用,来分隔
            setType(arr[0]);
            setVal(arr[1]);
            setJava_type(arr[arr.length - 1]);
            if (getJava_type().contains("List")) {
                int startIndex = 0;
                int endIndex = 0;
                for (int i = 0; i < arr.length; i++) {
                    if (arr[i].trim().startsWith("[")) {
                        startIndex = i;
                    } else if (arr[i].trim().endsWith("]")) {
                        endIndex = i;
                        break;
                    }
                }
                StringBuilder val = new StringBuilder();
                for (int i = startIndex; i <= endIndex; i++) {
                    val.append(arr[i]);
                }
                setVal(val.toString());
            }
        }
    }

    public SubTypeBean(String type, String name, String val, String java_type) {
        this.type = type;
        this.name = name;
        this.val = val;
        this.java_type = java_type;
    }

    public SubTypeBean(String type, String val) {
        this.type = type;
        this.val = val;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val.trim();
    }

    public String getJava_type() {
        return java_type;
    }

    public void setJava_type(String java_type) {
        this.java_type = java_type.trim();
    }

    @Override
    public String toString() {
        return String.format("%s", getVal());
    }
}
