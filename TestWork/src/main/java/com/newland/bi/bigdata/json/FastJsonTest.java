package com.newland.bi.bigdata.json;

import java.lang.reflect.Type;

import com.alibaba.fastjson.annotation.JSONType;

public class FastJsonTest {
	private String name;	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	private abstract class FastJsonTest1{}

	public static void main(String[] args) {
		Type type = FastJsonTest.class;
		Class<?> clazz = (Class<?>) type;
		//获取注解
		JSONType annotation = clazz.getAnnotation(JSONType.class);
		System.out.println(annotation);
		//类修饰符,public,private,abstract等
		System.out.println(clazz.getModifiers());
		//返回泛型变量
		System.out.println(clazz.getTypeParameters().length);
	}
}
