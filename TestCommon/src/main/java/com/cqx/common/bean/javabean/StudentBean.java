package com.cqx.common.bean.javabean;

/**
 * 学生
 *
 * @author chenqixu
 */
public class StudentBean {
    private int student_no;// 编号
    private String student_name;// 姓名
    private int student_sex;// 性别

    @Override
    public String toString() {
        return "student_no：" + student_no + "，student_name：" + student_name + "，student_sex：" + student_sex;
    }

    public int getStudent_no() {
        return student_no;
    }

    public void setStudent_no(int student_no) {
        this.student_no = student_no;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public int getStudent_sex() {
        return student_sex;
    }

    public void setStudent_sex(int student_sex) {
        this.student_sex = student_sex;
    }
}
