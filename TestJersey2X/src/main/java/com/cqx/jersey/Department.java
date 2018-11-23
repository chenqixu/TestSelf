package com.cqx.jersey;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 部门bean
 * */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class Department {
	private Long id;
	private String name;
}
