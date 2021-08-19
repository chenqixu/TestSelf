package com.newland.bi.bigdata.bean.javabean;

import java.util.ArrayList;
import java.util.List;

/**
 * S1mmeBean列表
 *
 * @author chenqixu
 */
public class ListObject {
    private List<S1mmeBean> list;
    private List<String> values = new ArrayList<>();

    public ListObject() {
    }

    public ListObject(List<S1mmeBean> list) {
        this.list = list;
    }

    public void addValue(String value) {
        values.add(value);
    }

    @Override
    public String toString() {
        return "[values]" + values + ",[list]" + list;
    }

    public List<S1mmeBean> getList() {
        return list;
    }

    public void setList(List<S1mmeBean> list) {
        this.list = list;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
