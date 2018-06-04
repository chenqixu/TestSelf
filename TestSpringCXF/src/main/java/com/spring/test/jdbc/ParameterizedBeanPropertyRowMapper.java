package com.spring.test.jdbc;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class ParameterizedBeanPropertyRowMapper<T> extends BeanPropertyRowMapper<T>
	implements ParameterizedRowMapper<T> {
	public static <T> ParameterizedBeanPropertyRowMapper<T> newInstance(Class<T> mappedClass) {
		ParameterizedBeanPropertyRowMapper<T> newInstance = new ParameterizedBeanPropertyRowMapper<T>();
		newInstance.setMappedClass(mappedClass);
		return newInstance;
	}
}
