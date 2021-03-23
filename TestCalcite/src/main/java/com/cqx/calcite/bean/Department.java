package com.cqx.calcite.bean;

/**
 * Department
 *
 * @author chenqixu
 */
public class Department {
    public String deptno;

    public Department(String deptno) {
        this.deptno = deptno;
    }

    public static Department of(String deptno) {
        return new Department(deptno);
    }
}
