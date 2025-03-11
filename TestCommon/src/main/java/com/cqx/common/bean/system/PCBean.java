package com.cqx.common.bean.system;

import java.util.ArrayList;
import java.util.List;

/**
 * PCBean: parent + child
 *
 * @author chenqixu
 */
public class PCBean {
    private boolean isChild;
    private List<PCBean> childList = new ArrayList<>();
    private String name;

    public PCBean() {
    }

    public PCBean(String name, boolean isChild) {
        this.name = name;
        this.isChild = isChild;
    }

    public PCBean(String name) {
        this(name, false);
    }

    public String toString() {
        return getName();
    }

    public void addChild(String name, boolean isChild) {
        childList.add(new PCBean(name, isChild));
    }

    public PCBean addChild(String name) {
        PCBean pcb = new PCBean(name);
        childList.add(pcb);
        return pcb;
    }

    public boolean isChild() {
        return isChild;
    }

    public boolean hasChild() {
        return childList.size() > 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PCBean> getChildList() {
        return childList;
    }
}
