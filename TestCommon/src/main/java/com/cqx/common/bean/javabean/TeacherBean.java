package com.cqx.common.bean.javabean;

import com.cqx.common.utils.jdbc.JDBCUtil;

import java.io.Serializable;

/**
 * 老师
 *
 * @author chenqixu
 */
public class TeacherBean implements Serializable {
    private int teacher_no;// 编号
    private String teacher_name;// 姓名
    private int teacher_sex;// 性别
    private String teacher_major;// 专业
    private transient JDBCUtil jdbcUtil;

    public TeacherBean() {
    }

    public TeacherBean(JDBCUtil jdbcUtil) {
        this.jdbcUtil = jdbcUtil;
    }

    @Override
    public String toString() {
        return "teacher_no：" + teacher_no + "，teacher_name：" + teacher_name + "，teacher_sex：" + teacher_sex + "，teacher_major：" + teacher_major;
    }

    public int getTeacher_no() {
        return teacher_no;
    }

    public void setTeacher_no(int teacher_no) {
        this.teacher_no = teacher_no;
    }

    public String getTeacher_name() {
        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public int getTeacher_sex() {
        return teacher_sex;
    }

    public void setTeacher_sex(int teacher_sex) {
        this.teacher_sex = teacher_sex;
    }

    public String getTeacher_major() {
        return teacher_major;
    }

    public void setTeacher_major(String teacher_major) {
        this.teacher_major = teacher_major;
    }
}
