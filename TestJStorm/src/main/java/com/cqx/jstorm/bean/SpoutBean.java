package com.cqx.jstorm.bean;

import java.util.Map;

/**
 * spout
 *
 * @author chenqixu
 */
public class SpoutBean {
    private String name;
    private int parall;

    public static SpoutBean newbuilder() {
        return new SpoutBean();
    }

    public SpoutBean parser(Object param) {
        Map<String, ?> tmp = (Map<String, ?>) param;
        parall = (Integer) tmp.get("parall");
        name = (String) tmp.get("name");
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParall() {
        return parall;
    }

    public void setParall(int parall) {
        this.parall = parall;
    }
}
