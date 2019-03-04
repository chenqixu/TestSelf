package com.cqx.jersey.service;

import com.cqx.jersey.bean.VarInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * 连接查询服务
 *
 * @author chenqixu
 */
@Path("services/env/conn/detail")
public class ConnService {

    @GET
    @Path("name")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_ATOM_XML})
    public List<VarInfo> getDetailByName(@QueryParam("connName") String connName) {
        List<VarInfo> varInfos = new ArrayList<>();
        VarInfo varInfo = new VarInfo();
        varInfos.add(varInfo);
        System.out.println("connName：" + connName);
        //redis_185
        if (connName.equals("redis_185")) {
            varInfo.setVarId("R100000349");
            varInfo.setVarParentId("redis");
            varInfo.setVarName("ip_ports");
            varInfo.setVarValue("10.1.8.75:9001,10.1.8.75:9002,10.1.8.75:9003,10.1.8.75:9004,10.1.8.75:9005,10.1.8.75:9006");
            varInfo.setVarDesc("IP端口组");
            varInfo.setLevelId("0");
            varInfo.setVarChsName("IP端口组");
        }
        return varInfos;
    }
}
