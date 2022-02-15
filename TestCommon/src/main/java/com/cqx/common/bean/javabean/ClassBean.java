package com.cqx.common.bean.javabean;

import com.cqx.common.utils.jdbc.JDBCUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 班级
 *
 * @author chenqixu
 */
public class ClassBean implements Serializable {
    private int class_no;// 编号
    private String class_name;// 班级名称
    private TeacherBean headmaster;// 班主任
    private List<StudentBean> studentBeans;// 学生

    public ClassBean() {
        headmaster = new TeacherBean();
        studentBeans = new ArrayList<>();
    }

    public ClassBean(JDBCUtil jdbcUtil) {
        headmaster = new TeacherBean(jdbcUtil);
        studentBeans = new ArrayList<>();
    }

    public void addStudent(StudentBean studentBean) {
        studentBeans.add(studentBean);
    }

    @Override
    public String toString() {
        return "class_no：" + class_no + "，class_name：" + class_name + "，headmaster：" + headmaster + "，studentBeans：" + studentBeans;
    }

    public List<StudentBean> getStudentBeans() {
        return studentBeans;
    }

    public void setStudentBeans(List<StudentBean> studentBeans) {
        this.studentBeans = studentBeans;
    }

    public TeacherBean getHeadmaster() {
        return headmaster;
    }

    public void setHeadmaster(TeacherBean headmaster) {
        this.headmaster = headmaster;
    }

    public int getClass_no() {
        return class_no;
    }

    public void setClass_no(int class_no) {
        this.class_no = class_no;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }
}
