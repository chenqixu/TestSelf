package com.cqx.bean;

import java.io.Serializable;

public class TestBean implements Serializable {
	private static final long serialVersionUID = 9068749381688817394L;
	private String id;
	private String name;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
