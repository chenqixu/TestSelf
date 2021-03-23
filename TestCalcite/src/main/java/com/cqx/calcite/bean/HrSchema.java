package com.cqx.calcite.bean;

/**
 * HrSchema
 *
 * @author chenqixu
 */
public class HrSchema {
    public final Employee[] emps = {
            Employee.of(10000, "BI"),
            Employee.of(10001, "BI")
    };
    public final Department[] depts = {
            Department.of("BI"),
            Department.of("CRM"),
            Department.of("BOSS"),
            Department.of("SCM")
    };
}
