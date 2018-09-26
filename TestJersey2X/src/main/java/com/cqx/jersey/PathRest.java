package com.cqx.jersey;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * 测试服务2
 * */
@Path("path")
public class PathRest {
	
	/**
	 * 映射url中匹配的占位符
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
