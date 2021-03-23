package com.cqx.calcite.bean;

/**
 * Son
 *
 * @author chenqixu
 */
public class Son extends Father {
    private String fashion;

    public Son(Son son) {
        setName(son.getName());
        setAge(son.getAge());
        setFashion(son.getFashion());
    }

    public String getFashion() {
        return fashion;
    }

    public void setFashion(String fashion) {
        this.fashion = fashion;
    }
}
