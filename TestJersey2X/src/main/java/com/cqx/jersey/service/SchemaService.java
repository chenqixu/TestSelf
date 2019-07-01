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
        } else if (topic.equals("nmc_tb_lte_s1mme")) {
            return "{\n" +
                    "\"namespace\": \" com.newland\",\n" +
                    "\"type\": \"record\",\n" +
                    "\"name\": \"Lte_Signal_Output_Schema\",\n" +
                    "\"fields\":[\n" +
                    "{\"name\": \"city\",  \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"xdr_id\",  \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"imsi\",  \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"imei\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"msisdn\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"procedure_type\", \"type\": [\"int\", \"null\"]},\n" +
                    "{\"name\": \"subprocedure_type\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"procedure_start_time\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"procedure_delay_time\", \"type\": [\"int\", \"null\"]},\n" +
                    "{\"name\": \"procedure_end_time\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"procedure_status\", \"type\": [\"int\", \"null\"]},\n" +
                    "{\"name\": \"old_mme_group_id\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"old_mme_code\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"lac\", \"type\": [\"int\", \"null\"]},\n" +
                    "{\"name\": \"tac\", \"type\": [\"int\", \"null\"]},\n" +
                    "{\"name\": \"cell_id\", \"type\": [\"int\", \"null\"]},\n" +
                    "{\"name\": \"other_tac\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"other_eci\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"home_code\", \"type\": [\"int\", \"null\"]},\n" +
                    "{\"name\": \"msisdn_home_code\", \"type\": [\"int\", \"null\"]},\n" +
                    "{\"name\": \"old_mme_group_id_1\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"old_mme_code_1\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"old_m_tmsi\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"old_tac\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"old_eci\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"cause\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"keyword\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"mme_ue_s1ap_id\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"request_cause\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"keyword_2\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"keyword_3\", \"type\": [\"string\", \"null\"]},\n" +
                    "{\"name\": \"keyword_4\", \"type\": [\"string\", \"null\"]}\n" +
                    "]\n" +
                    "}";
        }
        return "";
    }
}
