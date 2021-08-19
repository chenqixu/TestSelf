package com.newland.bi.bigdata.file;

import com.cqx.common.utils.file.BaseRandomAccessFile;
import com.cqx.common.utils.file.FileCount;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.kafka.GenericRecordUtil;
import com.cqx.common.utils.serialize.impl.KryoSerializationImpl;
import com.cqx.common.utils.serialize.impl.ProtoStuffSerializationImpl;
import com.newland.bi.bigdata.bean.javabean.ListObject;
import com.newland.bi.bigdata.bean.javabean.S1mmeBean;
import com.newland.bi.bigdata.time.TimeCostUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 压缩比测试
 * <p>
 * 分别测试avsc、kryo、protostuff所占空间
 *
 * @author chenqixu
 */
public class CompressionRatio {
    private static final Logger logger = LoggerFactory.getLogger(CompressionRatio.class);
    private String readFileName;
    private String writeFileName;
    private String schemaString = "{\n" +
            "\"namespace\": \"com.newland.bi.bigdata.bean.avro\",\n" +
            "\"type\": \"record\",\n" +
            "\"name\": \"S1MME_AVRO\",\n" +
            "\"fields\":[\n" +
            "{\"name\": \"length\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"city\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"interface_type\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"xdr_id\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"rat\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"imsi\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"imei\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"msisdn\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"procedure_type\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"subprocedure_type\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"procedure_start_time\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"procedure_delay_time\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"procedure_end_time\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"procedure_status\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"nas_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"s1ap_cause1\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"s1ap_cause2\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"keyword\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"enb_ue_s1ap_id\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"mme_ue_s1ap_id\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"old_mme_group_id\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"old_mme_code\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"m_tmsi\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"mcc\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"mnc\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"lac\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"tmsi\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"user_ipv4\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"user_ipv6\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"machine_ip_add_type\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"mme_ip_add\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"enb_ip_add\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"mme_port\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"enb_port\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"tac\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"cell_id\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"other_tac\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"other_eci\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"mac\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"req_count\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"res_count\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"apn\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"eps_bearer_number\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_id1\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_type1\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_qci1\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_status1\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_enb_gtp_teid1\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_sgw_gtp_teid1\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_id2\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_type2\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_qci2\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_status2\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_enb_gtp_teid2\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_sgw_gtp_teid2\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_id3\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_type3\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_qci3\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_status3\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_enb_gtp_teid3\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_sgw_gtp_teid3\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_id4\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_type4\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_qci4\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_status4\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_enb_gtp_teid4\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_sgw_gtp_teid4\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_id5\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_type5\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_qci5\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_status5\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_enb_gtp_teid5\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_sgw_gtp_teid5\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_id6\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_type6\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_qci6\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_status6\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_enb_gtp_teid6\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_sgw_gtp_teid6\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_id7\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_type7\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_qci7\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_status7\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_enb_gtp_teid7\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_sgw_gtp_teid7\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_id8\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_type8\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_qci8\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_status8\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_enb_gtp_teid8\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_sgw_gtp_teid8\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_id9\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_type9\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_qci9\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_status9\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_enb_gtp_teid9\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_sgw_gtp_teid9\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_id10\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_type10\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_qci10\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_status10\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_enb_gtp_teid10\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_sgw_gtp_teid10\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_id11\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_type11\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_qci11\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_status11\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_enb_gtp_teid11\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_sgw_gtp_teid11\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_id12\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_type12\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_qci12\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_status12\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_enb_gtp_teid12\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_sgw_gtp_teid12\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_id13\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_type13\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_qci13\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_status13\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_enb_gtp_teid13\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_sgw_gtp_teid13\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_id14\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_type14\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_qci14\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_status14\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_enb_gtp_teid14\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_sgw_gtp_teid14\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_id15\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_type15\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_qci15\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_status15\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_enb_gtp_teid15\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_sgw_gtp_teid15\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"s_year\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"s_month\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"s_day\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"s_hour\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"s_minute\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"request_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"old_mme_group_id_1\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"paging_type\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"keyword_2\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"keyword_3\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"keyword_4\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"old_mme_code_1\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"old_m_tmsi\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_1_request_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_1_failure_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_2_request_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_2_failure_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_3_request_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_3_failure_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_4_request_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_4_failure_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_5_request_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_5_failure_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_6_request_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_6_failure_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_7_request_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_7_failure_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_8_request_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_8_failure_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_9_request_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_9_failure_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_10_request_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_10_failure_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_11_request_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_11_failure_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_12_request_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_12_failure_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_13_request_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_13_failure_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_14_request_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_14_failure_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_15_request_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"bearer_15_failure_cause\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"reserve_1\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"reserve_2\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"reserve_3\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"old_tac\",  \"type\": [\"string\", \"null\"]},\n" +
            "{\"name\": \"old_eci\",  \"type\": [\"string\", \"null\"]}\n" +
            "]\n" +
            "}";
    private String[] content_key_array = "length,city,interface_type,xdr_id,rat,imsi,imei,msisdn,procedure_type,subprocedure_type,procedure_start_time,procedure_delay_time,procedure_end_time,procedure_status,cause,nas_cause,s1ap_cause1,s1ap_cause2,keyword,enb_ue_s1ap_id,mme_ue_s1ap_id,old_mme_group_id,old_mme_code,m_tmsi,mcc,mnc,lac,tmsi,user_ipv4,user_ipv6,machine_ip_add_type,mme_ip_add,enb_ip_add,mme_port,enb_port,tac,cell_id,other_tac,other_eci,mac,req_count,res_count,apn,eps_bearer_number,bearer_id1,bearer_type1,bearer_qci1,bearer_status1,bearer_enb_gtp_teid1,bearer_sgw_gtp_teid1,bearer_id2,bearer_type2,bearer_qci2,bearer_status2,bearer_enb_gtp_teid2,bearer_sgw_gtp_teid2,bearer_id3,bearer_type3,bearer_qci3,bearer_status3,bearer_enb_gtp_teid3,bearer_sgw_gtp_teid3,bearer_id4,bearer_type4,bearer_qci4,bearer_status4,bearer_enb_gtp_teid4,bearer_sgw_gtp_teid4,bearer_id5,bearer_type5,bearer_qci5,bearer_status5,bearer_enb_gtp_teid5,bearer_sgw_gtp_teid5,bearer_id6,bearer_type6,bearer_qci6,bearer_status6,bearer_enb_gtp_teid6,bearer_sgw_gtp_teid6,bearer_id7,bearer_type7,bearer_qci7,bearer_status7,bearer_enb_gtp_teid7,bearer_sgw_gtp_teid7,bearer_id8,bearer_type8,bearer_qci8,bearer_status8,bearer_enb_gtp_teid8,bearer_sgw_gtp_teid8,bearer_id9,bearer_type9,bearer_qci9,bearer_status9,bearer_enb_gtp_teid9,bearer_sgw_gtp_teid9,bearer_id10,bearer_type10,bearer_qci10,bearer_status10,bearer_enb_gtp_teid10,bearer_sgw_gtp_teid10,bearer_id11,bearer_type11,bearer_qci11,bearer_status11,bearer_enb_gtp_teid11,bearer_sgw_gtp_teid11,bearer_id12,bearer_type12,bearer_qci12,bearer_status12,bearer_enb_gtp_teid12,bearer_sgw_gtp_teid12,bearer_id13,bearer_type13,bearer_qci13,bearer_status13,bearer_enb_gtp_teid13,bearer_sgw_gtp_teid13,bearer_id14,bearer_type14,bearer_qci14,bearer_status14,bearer_enb_gtp_teid14,bearer_sgw_gtp_teid14,bearer_id15,bearer_type15,bearer_qci15,bearer_status15,bearer_enb_gtp_teid15,bearer_sgw_gtp_teid15,s_year,s_month,s_day,s_hour,s_minute,request_cause,old_mme_group_id_1,paging_type,keyword_2,keyword_3,keyword_4,old_mme_code_1,old_m_tmsi,bearer_1_request_cause,bearer_1_failure_cause,bearer_2_request_cause,bearer_2_failure_cause,bearer_3_request_cause,bearer_3_failure_cause,bearer_4_request_cause,bearer_4_failure_cause,bearer_5_request_cause,bearer_5_failure_cause,bearer_6_request_cause,bearer_6_failure_cause,bearer_7_request_cause,bearer_7_failure_cause,bearer_8_request_cause,bearer_8_failure_cause,bearer_9_request_cause,bearer_9_failure_cause,bearer_10_request_cause,bearer_10_failure_cause,bearer_11_request_cause,bearer_11_failure_cause,bearer_12_request_cause,bearer_12_failure_cause,bearer_13_request_cause,bearer_13_failure_cause,bearer_14_request_cause,bearer_14_failure_cause,bearer_15_request_cause,bearer_15_failure_cause,reserve_1,reserve_2,reserve_3,old_tac,old_eci".split(",");
    private String topic = "S1MME";
    private GenericRecordUtil genericRecordUtil;
    private KryoSerializationImpl<ListObject> kryoSerialization;
    private ProtoStuffSerializationImpl<ListObject> protoStuffSerialization;
    private boolean isRAF = false;
    private BaseRandomAccessFile baseRandomAccessFile = null;

    public CompressionRatio() {
        genericRecordUtil = new GenericRecordUtil(null);
        genericRecordUtil.addTopicBySchemaString(topic, schemaString);
        kryoSerialization = new KryoSerializationImpl<>();
        kryoSerialization.setTClass(ListObject.class);
        protoStuffSerialization = new ProtoStuffSerializationImpl<>();
        protoStuffSerialization.setTClass(ListObject.class);
    }

    public void write(final Compression compression) throws IOException {
        // 读取文件
        final FileCount fileCount;
        final FileUtil fileUtil = new FileUtil();
        TimeCostUtil costUtil = new TimeCostUtil();
        costUtil.start();
        switch (compression) {
            case AVRO:
            case KRYO:
            case PROTOSTUFF:
                if (isRAF) {
                    baseRandomAccessFile = new BaseRandomAccessFile(writeFileName);
                    baseRandomAccessFile.setLock(true);
                } else {
                    fileUtil.createOutputStreamFile(writeFileName, false);
                }
                break;
            default:
                throw new RuntimeException(compression + "不支持的类型！");
        }
        try {
            fileCount = new FileCount() {
                List<S1mmeBean> s1mmeBeanList = new ArrayList<>();
                List<String> valuesList = new ArrayList<>();

                @Override
                public void run(String content) throws IOException {
                    count("read");
                    // 读一行，处理一行
                    byte[] bytes = null;
                    switch (compression) {
                        case AVRO:
                            // 处理成AVRO，并写入到文件
                            bytes = genericRecordUtil.genericRecord(topic, contentToMap(content));
                            break;
                        case KRYO:
                            // 处理成KRYO
                            s1mmeBeanList.add(contentToBean(content));
                            valuesList.add(content);
                            if (valuesList.size() == 230) {
//                                bytes = kryoSerialization.serialize(new ListObject(new ArrayList<>(s1mmeBeanList)));
                                ListObject listObject = new ListObject();
                                listObject.setValues(valuesList);
                                bytes = kryoSerialization.serialize(listObject);
                                ListObject de = kryoSerialization.deserialize(bytes);
                                logger.info("{} {} {}", bytes.length, "\r\n".getBytes().length, de.getList() != null ? de.getList().size() : 0);
                                s1mmeBeanList.clear();
                                valuesList.clear();
                            }
                            break;
                        case PROTOSTUFF:
                            s1mmeBeanList.add(contentToBean(content));
                            if (s1mmeBeanList.size() == 4000) {
                                bytes = protoStuffSerialization.serialize(new ListObject(new ArrayList<>(s1mmeBeanList)));
                                logger.info("{}", bytes.length);
                                s1mmeBeanList.clear();
                            }
                            break;
                    }
                    if (bytes != null) {
                        if (isRAF) {
                            baseRandomAccessFile.write(bytes);
                        } else {
                            fileUtil.os_write(bytes);
                            fileUtil.os_newline();
                        }
                    }
                }

                @Override
                public void tearDown() throws IOException {
                    if (s1mmeBeanList.size() > 0) {
                        byte[] bytes = null;
                        switch (compression) {
                            case AVRO:
                                break;
                            case KRYO:
                                // 处理成KRYO，并写入到文件
                                bytes = kryoSerialization.serialize(new ListObject(new ArrayList<>(s1mmeBeanList)));
                                break;
                            case PROTOSTUFF:
                                bytes = protoStuffSerialization.serialize(new ListObject(new ArrayList<>(s1mmeBeanList)));
                                break;
                        }
                        if (bytes != null) {
                            if (isRAF) {
                                baseRandomAccessFile.write(bytes);
                            } else {
                                fileUtil.os_write(bytes);
                            }
                        }
                    }
                }
            };
            fileUtil.setReader(readFileName);
            fileUtil.read(fileCount);
        } finally {
            fileUtil.closeRead();
            switch (compression) {
                case AVRO:
                case KRYO:
                case PROTOSTUFF:
                    if (isRAF) {
                        baseRandomAccessFile.close();
                    } else {
                        fileUtil.closeOutputStream();
                    }
                    break;
            }
        }
        logger.info("处理完成，耗时：{}毫秒，记录数：{}", costUtil.stopAndGet(), fileCount.getCount("read"));
    }

    public void read(final Compression compression) throws IOException {
        // 读取文件
//        final FileCount fileCount;
//        final FileUtil fileUtil = new FileUtil();
        try {
            baseRandomAccessFile = new BaseRandomAccessFile(writeFileName);
            baseRandomAccessFile.setLock(true);
            byte[] bytes = baseRandomAccessFile.readByte(0, 379571);
            logger.info("{}", bytes);
            ListObject listObject = kryoSerialization.deserialize(bytes);
            logger.info("{}", listObject);
        } finally {
            baseRandomAccessFile.close();
        }
//        TimeCostUtil costUtil = new TimeCostUtil();
//        costUtil.start();
//        try {
//            fileCount = new FileCount() {
//
//                @Override
//                public void run(byte[] content) throws IOException {
//                    count("read");
//                    switch (compression) {
//                        case AVRO:
//                            break;
//                        case KRYO:
//                            logger.info("{}", content.length);
//                            ListObject listObject = kryoSerialization.deserialize(content);
//                            logger.info("{}", listObject.getList().size());
//                            break;
//                        case PROTOSTUFF:
//                            break;
//                    }
//                }
//
//                @Override
//                public void run(String content) throws IOException {
//                }
//            };
//            fileUtil.setInputStreamReader(writeFileName);
//            fileUtil.readInputStream(fileCount, 0, 17853);
//        } finally {
//            fileUtil.closeRead();
//        }
//        logger.info("处理完成，耗时：{}毫秒，记录数：{}", costUtil.stopAndGet(), fileCount.getCount("read"));
    }

    public void setReadFileName(String readFileName) {
        this.readFileName = readFileName;
    }

    public void setWriteFileName(String writeFileName) {
        this.writeFileName = writeFileName;
    }

    public Map<String, String> contentToMap(String content) {
        String[] content_value_array = content.split("\\|", -1);
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < content_key_array.length; i++) {
            map.put(content_key_array[i], content_value_array[i]);
        }
        return map;
    }

    public S1mmeBean contentToBean(String content) {
        Map<String, String> map = contentToMap(content);
        return new S1mmeBean(map);
    }

    public void setRAF(boolean RAF) {
        isRAF = RAF;
    }

    public enum Compression {
        AVRO,
        KRYO,
        PROTOSTUFF,
        ;
    }
}
