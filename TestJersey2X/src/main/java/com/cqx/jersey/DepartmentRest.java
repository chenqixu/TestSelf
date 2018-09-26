package com.cqx.jersey;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 部门请求服务
 * <pre>
 * 列表(list)
 * 通过id获取名称(get/{id})
 * 保存(save)
 * 通过id更新名称(update/{id})
 * 通过id删除部门(delete/{id})
 * </pre>
 * */
@Path("dept")
public class DepartmentRest {
	
	@GET
	@Path("list")
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})	
	public List<Department> list() {
		List<Department> dept = new ArrayList<>();
		dept.add(new Department(1L, "dept1"));
		dept.add(new Department(2L, "dept2"));
		return dept;
	}
	
	@GET
	@Path("get/{id}")
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_ATOM_XML})
	public Department get(@PathParam("id") Long id) {
	    return new Department(id, "dept2");
	}

	@POST
	@Path("save")
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_ATOM_XML})
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Department save(@FormParam("name") String name) {
	    Department d = new Department(1L, name);
	    return d;
	}

	@PUT
	@Path("update/{id}")
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_ATOM_XML})
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Department update(@PathParam("id") Long id, @FormParam("name") String name) {
	    Department d = new Department(id, name);
	    return d;
	}

	@DELETE
	@Path("delete/{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void delete(@PathParam("id") Long id) {
	    System.out.println("删除部门：" + id);
	}
}
