package com.cqx.jersey.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * kafka schema服务
 *
 * @author chenqixu
 */
@Path("SchemaService")
public class SchemaService {

    @GET
    @Path("getSchema")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_ATOM_XML})
    public String getDetailByName(@QueryParam("t") String topic) {
        System.out.println("topic：" + topic);
        if (topic.equals("nmc_tb_lte_http")) {
            return "{\n" +
                    "\"namespace\": \"com.newland\",\n" +
                    "\"type\": \"record\",\n" +
                    "\"name\": \"lte_http\",\n" +
                    "\"fields\":[\n" +
                    "{\"name\": \"city_1\", \"type\": [\"string\"]},\n" +
                    "{\"name\": \"imsi\", \"type\": [\"string\"]},\n" +
                    "{\"name\": \"imei\", \"type\": [\"string\"] },\n" +
                    "{\"name\": \"msisdn\", \"type\": [\"string\"]},\n" +
                    "{\"name\": \"tac\", \"type\": [\"string\"]},\n" +
                    "{\"name\": \"eci\", \"type\": [\"string\"]},\n" +
                    "{\"name\": \"rat\", \"type\": [\"string\"]},\n" +
                    "{\"name\": \"procedure_start_time\", \"type\": [\"string\"]},\n" +
                    "{\"name\": \"app_class\", \"type\": [\"string\"]},\n" +
                    "{\"name\": \"host\", \"type\": [\"string\"]},\n" +
                    "{\"name\": \"uri\", \"type\": [\"string\"]},\n" +
                    "{\"name\": \"apply_classify\", \"type\": [\"string\"]},\n" +
                    "{\"name\": \"apply_name\", \"type\": [\"string\"]},\n" +
                    "{\"name\": \"web_classify\", \"type\": [\"string\"]},\n" +
                    "{\"name\": \"web_name\", \"type\": [\"string\"] },\n" +
                    "{\"name\": \"search_keyword\", \"type\": [\"string\"]},\n" +
                    "{\"name\": \"procedure_end_time\", \"type\": [\"string\"]},\n" +
                    "{\"name\": \"upbytes\", \"type\": [\"string\"]},\n" +
                    "{\"name\": \"downbytes\", \"type\": [\"string\"]}\n" +
                    "]\n" +
                    "}";
        }
        return "";
    }
}
