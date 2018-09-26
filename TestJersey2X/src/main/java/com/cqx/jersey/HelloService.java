package com.cqx.jersey;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * ���Է���1
 * */
@Path("hello")
public class HelloService {
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String hi(){
		return "hello jersey";
	}
}
