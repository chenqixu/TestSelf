package com.cqx.jersey;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * ���Է���2
 * */
@Path("path")
public class PathRest {
	
	/**
	 * ӳ��url��ƥ���ռλ��
	 * @param id
	 * @return
	 * */
	@GET
	@Path("{id}")
	public String pathParam(@PathParam("id") Long id) {
		System.out.println(this);
		System.out.println(id);
		return "success";
	}
}
