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
        } else if (topic.equals("nmc_tb_mc_cdr")) {
            return "{ \"namespace\": \"com.newland.spl\", \"type\": \"record\", \"name\": \"CRM_Source_Schema\", \"fields\":[ \n" +
                    "{\"name\": \"BTIME\", \"type\": [\"string\"]}, \n" +
                    "{\"name\": \"ETIME\", \"type\": [\"string\"]}, \n" +
                    "{\"name\": \"GLOBALID\", \"type\": [\"long\"]}, \n" +
                    "{\"name\": \"PROTOCOLID\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"EVENTID\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"MSCCODE\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"LAC\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"CI\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"OLAC\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"OCI\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"DLAC\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"DCI\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"FIRSTLAC\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"FIRSTCI\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"LASTLAC\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"LASTCI\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"CALLINGNUM\", \"type\": [\"string\"]}, \n" +
                    "{\"name\": \"CALLEDNUM\", \"type\": [\"string\"]}, \n" +
                    "{\"name\": \"CALLINGIMSI\", \"type\": [\"string\"]}, \n" +
                    "{\"name\": \"CALLEDIMSI\", \"type\": [\"string\"]}, \n" +
                    "{\"name\": \"CALLINGIMEI\", \"type\": [\"string\"]}, \n" +
                    "{\"name\": \"CALLEDIMEI\", \"type\": [\"string\"]}, \n" +
                    "{\"name\": \"CALLINGTMSI\", \"type\": [\"string\"]}, \n" +
                    "{\"name\": \"CALLEDTMSI\", \"type\": [\"string\"]}, \n" +
                    "{\"name\": \"EVENTRESULT\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"ALERTOFFSET\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"CONNOFFSET\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"DISCONDIRECT\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"DISCONNOFFSET\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"ANSWERDUR\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"PAGINGRESPTYPE\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"ALERTSTATUS\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"CONSTATUS\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"DISCONNSTATUS\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"DISCONNCAUSE\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"RELCAUSE\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"HOFLAG\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"Callingnumnature\", \"type\": [\"string\"]}, \n" +
                    "{\"name\": \"Callednumnature\", \"type\": [\"string\"]}, \n" +
                    "{\"name\": \"CALLING_CITY\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"CALLING_COUNTY\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"CALLED_CITY\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"CALLED_COUNTY\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"CALL_COUNTY\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"FIRST_CALL_COUNTY\", \"type\": [\"int\"]}, \n" +
                    "{\"name\": \"LAST_CALL_COUNTY\", \"type\": [\"int\"]},\n" +
                    "{\"name\": \"CDRID\", \"type\": [\"string\"]}, \n" +
                    "{\"name\": \"SESSIONID\", \"type\": [\"string\"]}, \n" +
                    "{\"name\": \"SPCKIND\", \"type\": [\"string\"]} ] }";
        } else if (topic.equals("ogg_to_kafka")) {
            return "{\n" +
                    "  \"type\" : \"record\",\n" +
                    "  \"name\" : \"ORA_TO_KAFKA\",\n" +
                    "  \"namespace\" : \"TEST_OGG\",\n" +
                    "  \"fields\" : [ {\n" +
                    "    \"name\" : \"table\",\n" +
                    "    \"type\" : \"string\"\n" +
                    "  }, {\n" +
                    "    \"name\" : \"op_type\",\n" +
                    "    \"type\" : \"string\"\n" +
                    "  }, {\n" +
                    "    \"name\" : \"op_ts\",\n" +
                    "    \"type\" : \"string\"\n" +
                    "  }, {\n" +
                    "    \"name\" : \"current_ts\",\n" +
                    "    \"type\" : \"string\"\n" +
                    "  }, {\n" +
                    "    \"name\" : \"pos\",\n" +
                    "    \"type\" : \"string\"\n" +
                    "  }, {\n" +
                    "    \"name\" : \"primary_keys\",\n" +
                    "    \"type\" : {\n" +
                    "      \"type\" : \"array\",\n" +
                    "      \"items\" : \"string\"\n" +
                    "    }\n" +
                    "  }, {\n" +
                    "    \"name\" : \"tokens\",\n" +
                    "    \"type\" : {\n" +
                    "      \"type\" : \"map\",\n" +
                    "      \"values\" : \"string\"\n" +
                    "    },\n" +
                    "    \"default\" : { }\n" +
                    "  }, {\n" +
                    "    \"name\" : \"before\",\n" +
                    "    \"type\" : [ \"null\", {\n" +
                    "      \"type\" : \"record\",\n" +
                    "      \"name\" : \"columns\",\n" +
                    "      \"fields\" : [ {\n" +
                    "        \"name\" : \"ID\",\n" +
                    "        \"type\" : [ \"null\", \"long\" ],\n" +
                    "        \"default\" : null\n" +
                    "      }, {\n" +
                    "        \"name\" : \"ID_isMissing\",\n" +
                    "        \"type\" : [ \"null\", \"boolean\" ],\n" +
                    "        \"default\" : null\n" +
                    "      }, {\n" +
                    "        \"name\" : \"NAME\",\n" +
                    "        \"type\" : [ \"null\", \"string\" ],\n" +
                    "        \"default\" : null\n" +
                    "      }, {\n" +
                    "        \"name\" : \"NAME_isMissing\",\n" +
                    "        \"type\" : [ \"null\", \"boolean\" ],\n" +
                    "        \"default\" : null\n" +
                    "      }, {\n" +
                    "        \"name\" : \"ADDR\",\n" +
                    "        \"type\" : [ \"null\", \"string\" ],\n" +
                    "        \"default\" : null\n" +
                    "      }, {\n" +
                    "        \"name\" : \"ADDR_isMissing\",\n" +
                    "        \"type\" : [ \"null\", \"boolean\" ],\n" +
                    "        \"default\" : null\n" +
                    "      }, {\n" +
                    "        \"name\" : \"IMSI\",\n" +
                    "        \"type\" : [ \"null\", \"long\" ],\n" +
                    "        \"default\" : null\n" +
                    "      }, {\n" +
                    "        \"name\" : \"IMSI_isMissing\",\n" +
                    "        \"type\" : [ \"null\", \"boolean\" ],\n" +
                    "        \"default\" : null\n" +
                    "      }, {\n" +
                    "        \"name\" : \"MISSION\",\n" +
                    "        \"type\" : [ \"null\", \"string\" ],\n" +
                    "        \"default\" : null\n" +
                    "      }, {\n" +
                    "        \"name\" : \"MISSION_isMissing\",\n" +
                    "        \"type\" : [ \"null\", \"boolean\" ],\n" +
                    "        \"default\" : null\n" +
                    "      } ]\n" +
                    "    } ],\n" +
                    "    \"default\" : null\n" +
                    "  }, {\n" +
                    "    \"name\" : \"after\",\n" +
                    "    \"type\" : [ \"null\", \"columns\" ],\n" +
                    "    \"default\" : null\n" +
                    "  } ]\n" +
                    "}";
        }
        return "";
    }
}
