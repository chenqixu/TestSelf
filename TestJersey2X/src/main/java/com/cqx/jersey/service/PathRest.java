package com.cqx.jersey.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * 测试服务2
 */
@Path("path")
public class PathRest {

    /**
     * @param id
     * @return
     */
    @GET
    @Path("{id}")
    public String pathParam(@PathParam("id") Long id) {
        System.out.println(this);
        System.out.println(id);
        return "success";
    }
}
