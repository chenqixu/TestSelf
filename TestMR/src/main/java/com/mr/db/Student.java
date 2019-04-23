package com.mr.db;

import org.apache.hadoop.io.Text;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Student
 *
 * @author chenqixu
 */
public class Student implements DBQueryBean {
    //学生id字段
    private String sno;
    //学生姓名
    private String sname;

    //无参构造方法
    public Student() {
    }

    //有参构造方法
    public Student(String sno, String sname) {
        this.sno = sno;
        this.sname = sname;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    //实现DBWritable接口要实现的方法
    public void readFields(ResultSet resultSet) throws SQLException {
        this.sno = resultSet.getString(1);
        this.sname = resultSet.getString(2);
    }

    //实现DBWritable接口要实现的方法
    public void write(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, this.sno);
        preparedStatement.setString(2, this.sname);
    }

    //实现Writable接口要实现的方法
    public void readFields(DataInput dataInput) throws IOException {
        this.sno = Text.readString(dataInput);
        this.sname = Text.readString(dataInput);
    }

    //实现Writable接口要实现的方法
    public void write(DataOutput dataOutput) throws IOException {
        Text.writeString(dataOutput, this.sno);
        Text.writeString(dataOutput, this.sname);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sno == null) ? 0 : sno.hashCode());
        result = prime * result + ((sname == null) ? 0 : sname.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Student other = (Student) obj;
        if (sno == null) {
            if (other.sno != null)
                return false;
        } else if (!sno.equals(other.sno))
            return false;
        if (sname == null) {
            if (other.sname != null)
                return false;
        } else if (!sname.equals(other.sname))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Student [id=" + sno + ", sname=" + sname + "]";
    }
}
