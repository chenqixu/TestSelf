package com.bussiness.bi.bigdata.parser;

import com.bussiness.bi.bigdata.parser.DpiParserValue;
import org.junit.Before;
import org.junit.Test;

public class DpiParserValueTest {

    private DpiParserValue dpiParserValue;
    private String source = "length,city,interface,xdr_id,rat,imsi,imei,msisdn,procedure_type,subprocedure_type,procedure_start_time,procedure_delay_time,procedure_end_time,procedure_status,cause,nas_cause,s1ap_cause1,s1ap_cause2,keyword,enb_ue_s1ap_id,mme_ue_s1ap_id,old_mme_group_id,old_mme_code,m_tmsi,mcc,mnc,lac,tmsi,user_ipv4,user_ipv6,machine_ip_add_type,mme_ip_add,enb_ip_add,mme_port,enb_port,tac,cell_id,other_tac,other_eci,mac,req_count,res_count,apn,eps_bearer_number,bearer_id1,bearer_type1,bearer_qci1,bearer_status1,bearer_enb_gtp_teid1,bearer_sgw_gtp_teid1,bearer_id2,bearer_type2,bearer_qci2,bearer_status2,bearer_enb_gtp_teid2,bearer_sgw_gtp_teid2,bearer_id3,bearer_type3,bearer_qci3,bearer_status3,bearer_enb_gtp_teid3,bearer_sgw_gtp_teid3,bearer_id4,bearer_type4,bearer_qci4,bearer_status4,bearer_enb_gtp_teid4,bearer_sgw_gtp_teid4,bearer_id5,bearer_type5,bearer_qci5,bearer_status5,bearer_enb_gtp_teid5,bearer_sgw_gtp_teid5,bearer_id6,bearer_type6,bearer_qci6,bearer_status6,bearer_enb_gtp_teid6,bearer_sgw_gtp_teid6,bearer_id7,bearer_type7,bearer_qci7,bearer_status7,bearer_enb_gtp_teid7,bearer_sgw_gtp_teid7,bearer_id8,bearer_type8,bearer_qci8,bearer_status8,bearer_enb_gtp_teid8,bearer_sgw_gtp_teid8,bearer_id9,bearer_type9,bearer_qci9,bearer_status9,bearer_enb_gtp_teid9,bearer_sgw_gtp_teid9,bearer_id10,bearer_type10,bearer_qci10,bearer_status10,bearer_enb_gtp_teid10,bearer_sgw_gtp_teid10,bearer_id11,bearer_type11,bearer_qci11,bearer_status11,bearer_enb_gtp_teid11,bearer_sgw_gtp_teid11,bearer_id12,bearer_type12,bearer_qci12,bearer_status12,bearer_enb_gtp_teid12,bearer_sgw_gtp_teid12,bearer_id13,bearer_type13,bearer_qci13,bearer_status13,bearer_enb_gtp_teid13,bearer_sgw_gtp_teid13,bearer_id14,bearer_type14,bearer_qci14,bearer_status14,bearer_enb_gtp_teid14,bearer_sgw_gtp_teid14,bearer_id15,bearer_type15,bearer_qci15,bearer_status15,bearer_enb_gtp_teid15,bearer_sgw_gtp_teid15,s_year,s_month,s_day,s_hour,s_minute,request_cause,old_mme_group_id_1,paging_type,keyword_2,keyword_3,keyword_4,old_mme_code_1,old_m_tmsi,bearer_1_request_cause,bearer_1_failure_cause,bearer_2_request_cause,bearer_2_failure_cause,bearer_3_request_cause,bearer_3_failure_cause,bearer_4_request_cause,bearer_4_failure_cause,bearer_5_request_cause,bearer_5_failure_cause,bearer_6_request_cause,bearer_6_failure_cause,bearer_7_request_cause,bearer_7_failure_cause,bearer_8_request_cause,bearer_8_failure_cause,bearer_9_request_cause,bearer_9_failure_cause,bearer_10_request_cause,bearer_10_failure_cause,bearer_11_request_cause,bearer_11_failure_cause,bearer_12_request_cause,bearer_12_failure_cause,bearer_13_request_cause,bearer_13_failure_cause,bearer_14_request_cause,bearer_14_failure_cause,bearer_15_request_cause,bearer_15_failure_cause,reserve_1,reserve_2,reserve_3,old_tac,old_eci";
    private String rtm = "city,xdr_id,imsi,imei,msisdn,procedure_type,subprocedure_type,procedure_start_time,procedure_delay_time,procedure_end_time,procedure_status,old_mme_group_id,old_mme_code,lac,tac,cell_id,other_tac,other_eci,home_code,msisdn_home_code,old_mme_group_id_1,old_mme_code_1,old_m_tmsi,old_tac,old_eci,cause,keyword,mme_ue_s1ap_id,request_cause,keyword_2,keyword_3,keyword_4";
    private String rule = "msisdn,procedure_start_time,procedure_end_time,tac,cell_id";

    @Before
    public void setUp() throws Exception {
        dpiParserValue = new DpiParserValue();
        dpiParserValue.setSourceField(source);
        dpiParserValue.setRtmField(rtm);
        dpiParserValue.init();
    }

    @Test
    public void printKafkaField() {
        dpiParserValue.printKafkaField();
    }

    @Test
    public void printRuleField() {
        dpiParserValue.setRtmField(rule);
        dpiParserValue.init();
        dpiParserValue.printRuleField();
    }
}