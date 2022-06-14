package com.bussiness.bi.bigdata.bean;

public class Person {
    private String name;
    private String id;
    private int age;

    public Person() {
    }

    public Person(String _name, String _id, int _age) {
        this.name = _name;
        this.id = _id;
        this.age = _age;
        System.out.println("[this]：" + this);
    }

    public void toBean(String str) {
        System.out.println("[test]" + str);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "[name]：" + name + "，[id]：" + id + "，[age]：" + age;
    }
}
