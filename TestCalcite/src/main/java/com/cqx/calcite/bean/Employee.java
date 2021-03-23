package com.cqx.calcite.bean;

/**
 * Employee
 *
 * @author chenqixu
 */
public class Employee {
    public int empid;
    public String deptno;

    public Employee(int empid, String deptno) {
        this.empid = empid;
        this.deptno = deptno;
    }

    public static Employee of(int empid, String deptno) {
        return new Employee(empid, deptno);
    }
}
