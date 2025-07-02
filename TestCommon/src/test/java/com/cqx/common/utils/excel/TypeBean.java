package com.cqx.common.utils.excel;

/**
 * TODO
 *
 * @author chenqixu
 */
public class TypeBean {
    int i;
    String type;
    String name;

    public TypeBean(int i, String type, String name) {
        this.i = i;
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("[i]%s, [type]%s, [name]%s", i, type, name);
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
