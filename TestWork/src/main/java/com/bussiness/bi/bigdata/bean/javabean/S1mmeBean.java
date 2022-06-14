package com.bussiness.bi.bigdata.bean.javabean;

import java.util.Map;

/**
 * s1mme
 *
 * @author chenqixu
 */
public class S1mmeBean {
    private String length;
    private String city;
    private String interface_type;
    private String xdr_id;
    private String rat;
    private String imsi;
    private String imei;
    private String msisdn;
    private String procedure_type;
    private String subprocedure_type;
    private String procedure_start_time;
    private String procedure_delay_time;
    private String procedure_end_time;
    private String procedure_status;
    private String cause;
    private String nas_cause;
    private String s1ap_cause1;
    private String s1ap_cause2;
    private String keyword;
    private String enb_ue_s1ap_id;
    private String mme_ue_s1ap_id;
    private String old_mme_group_id;
    private String old_mme_code;
    private String m_tmsi;
    private String mcc;
    private String mnc;
    private String lac;
    private String tmsi;
    private String user_ipv4;
    private String user_ipv6;
    private String machine_ip_add_type;
    private String mme_ip_add;
    private String enb_ip_add;
    private String mme_port;
    private String enb_port;
    private String tac;
    private String cell_id;
    private String other_tac;
    private String other_eci;
    private String mac;
    private String req_count;
    private String res_count;
    private String apn;
    private String eps_bearer_number;
    private String bearer_id1;
    private String bearer_type1;
    private String bearer_qci1;
    private String bearer_status1;
    private String bearer_enb_gtp_teid1;
    private String bearer_sgw_gtp_teid1;
    private String bearer_id2;
    private String bearer_type2;
    private String bearer_qci2;
    private String bearer_status2;
    private String bearer_enb_gtp_teid2;
    private String bearer_sgw_gtp_teid2;
    private String bearer_id3;
    private String bearer_type3;
    private String bearer_qci3;
    private String bearer_status3;
    private String bearer_enb_gtp_teid3;
    private String bearer_sgw_gtp_teid3;
    private String bearer_id4;
    private String bearer_type4;
    private String bearer_qci4;
    private String bearer_status4;
    private String bearer_enb_gtp_teid4;
    private String bearer_sgw_gtp_teid4;
    private String bearer_id5;
    private String bearer_type5;
    private String bearer_qci5;
    private String bearer_status5;
    private String bearer_enb_gtp_teid5;
    private String bearer_sgw_gtp_teid5;
    private String bearer_id6;
    private String bearer_type6;
    private String bearer_qci6;
    private String bearer_status6;
    private String bearer_enb_gtp_teid6;
    private String bearer_sgw_gtp_teid6;
    private String bearer_id7;
    private String bearer_type7;
    private String bearer_qci7;
    private String bearer_status7;
    private String bearer_enb_gtp_teid7;
    private String bearer_sgw_gtp_teid7;
    private String bearer_id8;
    private String bearer_type8;
    private String bearer_qci8;
    private String bearer_status8;
    private String bearer_enb_gtp_teid8;
    private String bearer_sgw_gtp_teid8;
    private String bearer_id9;
    private String bearer_type9;
    private String bearer_qci9;
    private String bearer_status9;
    private String bearer_enb_gtp_teid9;
    private String bearer_sgw_gtp_teid9;
    private String bearer_id10;
    private String bearer_type10;
    private String bearer_qci10;
    private String bearer_status10;
    private String bearer_enb_gtp_teid10;
    private String bearer_sgw_gtp_teid10;
    private String bearer_id11;
    private String bearer_type11;
    private String bearer_qci11;
    private String bearer_status11;
    private String bearer_enb_gtp_teid11;
    private String bearer_sgw_gtp_teid11;
    private String bearer_id12;
    private String bearer_type12;
    private String bearer_qci12;
    private String bearer_status12;
    private String bearer_enb_gtp_teid12;
    private String bearer_sgw_gtp_teid12;
    private String bearer_id13;
    private String bearer_type13;
    private String bearer_qci13;
    private String bearer_status13;
    private String bearer_enb_gtp_teid13;
    private String bearer_sgw_gtp_teid13;
    private String bearer_id14;
    private String bearer_type14;
    private String bearer_qci14;
    private String bearer_status14;
    private String bearer_enb_gtp_teid14;
    private String bearer_sgw_gtp_teid14;
    private String bearer_id15;
    private String bearer_type15;
    private String bearer_qci15;
    private String bearer_status15;
    private String bearer_enb_gtp_teid15;
    private String bearer_sgw_gtp_teid15;
    private String s_year;
    private String s_month;
    private String s_day;
    private String s_hour;
    private String s_minute;
    private String request_cause;
    private String old_mme_group_id_1;
    private String paging_type;
    private String keyword_2;
    private String keyword_3;
    private String keyword_4;
    private String old_mme_code_1;
    private String old_m_tmsi;
    private String bearer_1_request_cause;
    private String bearer_1_failure_cause;
    private String bearer_2_request_cause;
    private String bearer_2_failure_cause;
    private String bearer_3_request_cause;
    private String bearer_3_failure_cause;
    private String bearer_4_request_cause;
    private String bearer_4_failure_cause;
    private String bearer_5_request_cause;
    private String bearer_5_failure_cause;
    private String bearer_6_request_cause;
    private String bearer_6_failure_cause;
    private String bearer_7_request_cause;
    private String bearer_7_failure_cause;
    private String bearer_8_request_cause;
    private String bearer_8_failure_cause;
    private String bearer_9_request_cause;
    private String bearer_9_failure_cause;
    private String bearer_10_request_cause;
    private String bearer_10_failure_cause;
    private String bearer_11_request_cause;
    private String bearer_11_failure_cause;
    private String bearer_12_request_cause;
    private String bearer_12_failure_cause;
    private String bearer_13_request_cause;
    private String bearer_13_failure_cause;
    private String bearer_14_request_cause;
    private String bearer_14_failure_cause;
    private String bearer_15_request_cause;
    private String bearer_15_failure_cause;
    private String reserve_1;
    private String reserve_2;
    private String reserve_3;
    private String old_tac;
    private String old_eci;

    public S1mmeBean() {
    }

    public S1mmeBean(Map<String, String> map) {
        length = map.get("length");
        city = map.get("city");
        interface_type = map.get("interface_type");
        xdr_id = map.get("xdr_id");
        rat = map.get("rat");
        imsi = map.get("imsi");
        imei = map.get("imei");
        msisdn = map.get("msisdn");
        procedure_type = map.get("procedure_type");
        subprocedure_type = map.get("subprocedure_type");
        procedure_start_time = map.get("procedure_start_time");
        procedure_delay_time = map.get("procedure_delay_time");
        procedure_end_time = map.get("procedure_end_time");
        procedure_status = map.get("procedure_status");
        cause = map.get("cause");
        nas_cause = map.get("nas_cause");
        s1ap_cause1 = map.get("s1ap_cause1");
        s1ap_cause2 = map.get("s1ap_cause2");
        keyword = map.get("keyword");
        enb_ue_s1ap_id = map.get("enb_ue_s1ap_id");
        mme_ue_s1ap_id = map.get("mme_ue_s1ap_id");
        old_mme_group_id = map.get("old_mme_group_id");
        old_mme_code = map.get("old_mme_code");
        m_tmsi = map.get("m_tmsi");
        mcc = map.get("mcc");
        mnc = map.get("mnc");
        lac = map.get("lac");
        tmsi = map.get("tmsi");
        user_ipv4 = map.get("user_ipv4");
        user_ipv6 = map.get("user_ipv6");
        machine_ip_add_type = map.get("machine_ip_add_type");
        mme_ip_add = map.get("mme_ip_add");
        enb_ip_add = map.get("enb_ip_add");
        mme_port = map.get("mme_port");
        enb_port = map.get("enb_port");
        tac = map.get("tac");
        cell_id = map.get("cell_id");
        other_tac = map.get("other_tac");
        other_eci = map.get("other_eci");
        mac = map.get("mac");
        req_count = map.get("req_count");
        res_count = map.get("res_count");
        apn = map.get("apn");
        eps_bearer_number = map.get("eps_bearer_number");
        bearer_id1 = map.get("bearer_id1");
        bearer_type1 = map.get("bearer_type1");
        bearer_qci1 = map.get("bearer_qci1");
        bearer_status1 = map.get("bearer_status1");
        bearer_enb_gtp_teid1 = map.get("bearer_enb_gtp_teid1");
        bearer_sgw_gtp_teid1 = map.get("bearer_sgw_gtp_teid1");
        bearer_id2 = map.get("bearer_id2");
        bearer_type2 = map.get("bearer_type2");
        bearer_qci2 = map.get("bearer_qci2");
        bearer_status2 = map.get("bearer_status2");
        bearer_enb_gtp_teid2 = map.get("bearer_enb_gtp_teid2");
        bearer_sgw_gtp_teid2 = map.get("bearer_sgw_gtp_teid2");
        bearer_id3 = map.get("bearer_id3");
        bearer_type3 = map.get("bearer_type3");
        bearer_qci3 = map.get("bearer_qci3");
        bearer_status3 = map.get("bearer_status3");
        bearer_enb_gtp_teid3 = map.get("bearer_enb_gtp_teid3");
        bearer_sgw_gtp_teid3 = map.get("bearer_sgw_gtp_teid3");
        bearer_id4 = map.get("bearer_id4");
        bearer_type4 = map.get("bearer_type4");
        bearer_qci4 = map.get("bearer_qci4");
        bearer_status4 = map.get("bearer_status4");
        bearer_enb_gtp_teid4 = map.get("bearer_enb_gtp_teid4");
        bearer_sgw_gtp_teid4 = map.get("bearer_sgw_gtp_teid4");
        bearer_id5 = map.get("bearer_id5");
        bearer_type5 = map.get("bearer_type5");
        bearer_qci5 = map.get("bearer_qci5");
        bearer_status5 = map.get("bearer_status5");
        bearer_enb_gtp_teid5 = map.get("bearer_enb_gtp_teid5");
        bearer_sgw_gtp_teid5 = map.get("bearer_sgw_gtp_teid5");
        bearer_id6 = map.get("bearer_id6");
        bearer_type6 = map.get("bearer_type6");
        bearer_qci6 = map.get("bearer_qci6");
        bearer_status6 = map.get("bearer_status6");
        bearer_enb_gtp_teid6 = map.get("bearer_enb_gtp_teid6");
        bearer_sgw_gtp_teid6 = map.get("bearer_sgw_gtp_teid6");
        bearer_id7 = map.get("bearer_id7");
        bearer_type7 = map.get("bearer_type7");
        bearer_qci7 = map.get("bearer_qci7");
        bearer_status7 = map.get("bearer_status7");
        bearer_enb_gtp_teid7 = map.get("bearer_enb_gtp_teid7");
        bearer_sgw_gtp_teid7 = map.get("bearer_sgw_gtp_teid7");
        bearer_id8 = map.get("bearer_id8");
        bearer_type8 = map.get("bearer_type8");
        bearer_qci8 = map.get("bearer_qci8");
        bearer_status8 = map.get("bearer_status8");
        bearer_enb_gtp_teid8 = map.get("bearer_enb_gtp_teid8");
        bearer_sgw_gtp_teid8 = map.get("bearer_sgw_gtp_teid8");
        bearer_id9 = map.get("bearer_id9");
        bearer_type9 = map.get("bearer_type9");
        bearer_qci9 = map.get("bearer_qci9");
        bearer_status9 = map.get("bearer_status9");
        bearer_enb_gtp_teid9 = map.get("bearer_enb_gtp_teid9");
        bearer_sgw_gtp_teid9 = map.get("bearer_sgw_gtp_teid9");
        bearer_id10 = map.get("bearer_id10");
        bearer_type10 = map.get("bearer_type10");
        bearer_qci10 = map.get("bearer_qci10");
        bearer_status10 = map.get("bearer_status10");
        bearer_enb_gtp_teid10 = map.get("bearer_enb_gtp_teid10");
        bearer_sgw_gtp_teid10 = map.get("bearer_sgw_gtp_teid10");
        bearer_id11 = map.get("bearer_id11");
        bearer_type11 = map.get("bearer_type11");
        bearer_qci11 = map.get("bearer_qci11");
        bearer_status11 = map.get("bearer_status11");
        bearer_enb_gtp_teid11 = map.get("bearer_enb_gtp_teid11");
        bearer_sgw_gtp_teid11 = map.get("bearer_sgw_gtp_teid11");
        bearer_id12 = map.get("bearer_id12");
        bearer_type12 = map.get("bearer_type12");
        bearer_qci12 = map.get("bearer_qci12");
        bearer_status12 = map.get("bearer_status12");
        bearer_enb_gtp_teid12 = map.get("bearer_enb_gtp_teid12");
        bearer_sgw_gtp_teid12 = map.get("bearer_sgw_gtp_teid12");
        bearer_id13 = map.get("bearer_id13");
        bearer_type13 = map.get("bearer_type13");
        bearer_qci13 = map.get("bearer_qci13");
        bearer_status13 = map.get("bearer_status13");
        bearer_enb_gtp_teid13 = map.get("bearer_enb_gtp_teid13");
        bearer_sgw_gtp_teid13 = map.get("bearer_sgw_gtp_teid13");
        bearer_id14 = map.get("bearer_id14");
        bearer_type14 = map.get("bearer_type14");
        bearer_qci14 = map.get("bearer_qci14");
        bearer_status14 = map.get("bearer_status14");
        bearer_enb_gtp_teid14 = map.get("bearer_enb_gtp_teid14");
        bearer_sgw_gtp_teid14 = map.get("bearer_sgw_gtp_teid14");
        bearer_id15 = map.get("bearer_id15");
        bearer_type15 = map.get("bearer_type15");
        bearer_qci15 = map.get("bearer_qci15");
        bearer_status15 = map.get("bearer_status15");
        bearer_enb_gtp_teid15 = map.get("bearer_enb_gtp_teid15");
        bearer_sgw_gtp_teid15 = map.get("bearer_sgw_gtp_teid15");
        s_year = map.get("s_year");
        s_month = map.get("s_month");
        s_day = map.get("s_day");
        s_hour = map.get("s_hour");
        s_minute = map.get("s_minute");
        request_cause = map.get("request_cause");
        old_mme_group_id_1 = map.get("old_mme_group_id_1");
        paging_type = map.get("paging_type");
        keyword_2 = map.get("keyword_2");
        keyword_3 = map.get("keyword_3");
        keyword_4 = map.get("keyword_4");
        old_mme_code_1 = map.get("old_mme_code_1");
        old_m_tmsi = map.get("old_m_tmsi");
        bearer_1_request_cause = map.get("bearer_1_request_cause");
        bearer_1_failure_cause = map.get("bearer_1_failure_cause");
        bearer_2_request_cause = map.get("bearer_2_request_cause");
        bearer_2_failure_cause = map.get("bearer_2_failure_cause");
        bearer_3_request_cause = map.get("bearer_3_request_cause");
        bearer_3_failure_cause = map.get("bearer_3_failure_cause");
        bearer_4_request_cause = map.get("bearer_4_request_cause");
        bearer_4_failure_cause = map.get("bearer_4_failure_cause");
        bearer_5_request_cause = map.get("bearer_5_request_cause");
        bearer_5_failure_cause = map.get("bearer_5_failure_cause");
        bearer_6_request_cause = map.get("bearer_6_request_cause");
        bearer_6_failure_cause = map.get("bearer_6_failure_cause");
        bearer_7_request_cause = map.get("bearer_7_request_cause");
        bearer_7_failure_cause = map.get("bearer_7_failure_cause");
        bearer_8_request_cause = map.get("bearer_8_request_cause");
        bearer_8_failure_cause = map.get("bearer_8_failure_cause");
        bearer_9_request_cause = map.get("bearer_9_request_cause");
        bearer_9_failure_cause = map.get("bearer_9_failure_cause");
        bearer_10_request_cause = map.get("bearer_10_request_cause");
        bearer_10_failure_cause = map.get("bearer_10_failure_cause");
        bearer_11_request_cause = map.get("bearer_11_request_cause");
        bearer_11_failure_cause = map.get("bearer_11_failure_cause");
        bearer_12_request_cause = map.get("bearer_12_request_cause");
        bearer_12_failure_cause = map.get("bearer_12_failure_cause");
        bearer_13_request_cause = map.get("bearer_13_request_cause");
        bearer_13_failure_cause = map.get("bearer_13_failure_cause");
        bearer_14_request_cause = map.get("bearer_14_request_cause");
        bearer_14_failure_cause = map.get("bearer_14_failure_cause");
        bearer_15_request_cause = map.get("bearer_15_request_cause");
        bearer_15_failure_cause = map.get("bearer_15_failure_cause");
        reserve_1 = map.get("reserve_1");
        reserve_2 = map.get("reserve_2");
        reserve_3 = map.get("reserve_3");
        old_tac = map.get("old_tac");
        old_eci = map.get("old_eci");
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getInterface_type() {
        return interface_type;
    }

    public void setInterface_type(String interface_type) {
        this.interface_type = interface_type;
    }

    public String getXdr_id() {
        return xdr_id;
    }

    public void setXdr_id(String xdr_id) {
        this.xdr_id = xdr_id;
    }

    public String getRat() {
        return rat;
    }

    public void setRat(String rat) {
        this.rat = rat;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getProcedure_type() {
        return procedure_type;
    }

    public void setProcedure_type(String procedure_type) {
        this.procedure_type = procedure_type;
    }

    public String getSubprocedure_type() {
        return subprocedure_type;
    }

    public void setSubprocedure_type(String subprocedure_type) {
        this.subprocedure_type = subprocedure_type;
    }

    public String getProcedure_start_time() {
        return procedure_start_time;
    }

    public void setProcedure_start_time(String procedure_start_time) {
        this.procedure_start_time = procedure_start_time;
    }

    public String getProcedure_delay_time() {
        return procedure_delay_time;
    }

    public void setProcedure_delay_time(String procedure_delay_time) {
        this.procedure_delay_time = procedure_delay_time;
    }

    public String getProcedure_end_time() {
        return procedure_end_time;
    }

    public void setProcedure_end_time(String procedure_end_time) {
        this.procedure_end_time = procedure_end_time;
    }

    public String getProcedure_status() {
        return procedure_status;
    }

    public void setProcedure_status(String procedure_status) {
        this.procedure_status = procedure_status;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getNas_cause() {
        return nas_cause;
    }

    public void setNas_cause(String nas_cause) {
        this.nas_cause = nas_cause;
    }

    public String getS1ap_cause1() {
        return s1ap_cause1;
    }

    public void setS1ap_cause1(String s1ap_cause1) {
        this.s1ap_cause1 = s1ap_cause1;
    }

    public String getS1ap_cause2() {
        return s1ap_cause2;
    }

    public void setS1ap_cause2(String s1ap_cause2) {
        this.s1ap_cause2 = s1ap_cause2;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getEnb_ue_s1ap_id() {
        return enb_ue_s1ap_id;
    }

    public void setEnb_ue_s1ap_id(String enb_ue_s1ap_id) {
        this.enb_ue_s1ap_id = enb_ue_s1ap_id;
    }

    public String getMme_ue_s1ap_id() {
        return mme_ue_s1ap_id;
    }

    public void setMme_ue_s1ap_id(String mme_ue_s1ap_id) {
        this.mme_ue_s1ap_id = mme_ue_s1ap_id;
    }

    public String getOld_mme_group_id() {
        return old_mme_group_id;
    }

    public void setOld_mme_group_id(String old_mme_group_id) {
        this.old_mme_group_id = old_mme_group_id;
    }

    public String getOld_mme_code() {
        return old_mme_code;
    }

    public void setOld_mme_code(String old_mme_code) {
        this.old_mme_code = old_mme_code;
    }

    public String getM_tmsi() {
        return m_tmsi;
    }

    public void setM_tmsi(String m_tmsi) {
        this.m_tmsi = m_tmsi;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getLac() {
        return lac;
    }

    public void setLac(String lac) {
        this.lac = lac;
    }

    public String getTmsi() {
        return tmsi;
    }

    public void setTmsi(String tmsi) {
        this.tmsi = tmsi;
    }

    public String getUser_ipv4() {
        return user_ipv4;
    }

    public void setUser_ipv4(String user_ipv4) {
        this.user_ipv4 = user_ipv4;
    }

    public String getUser_ipv6() {
        return user_ipv6;
    }

    public void setUser_ipv6(String user_ipv6) {
        this.user_ipv6 = user_ipv6;
    }

    public String getMachine_ip_add_type() {
        return machine_ip_add_type;
    }

    public void setMachine_ip_add_type(String machine_ip_add_type) {
        this.machine_ip_add_type = machine_ip_add_type;
    }

    public String getMme_ip_add() {
        return mme_ip_add;
    }

    public void setMme_ip_add(String mme_ip_add) {
        this.mme_ip_add = mme_ip_add;
    }

    public String getEnb_ip_add() {
        return enb_ip_add;
    }

    public void setEnb_ip_add(String enb_ip_add) {
        this.enb_ip_add = enb_ip_add;
    }

    public String getMme_port() {
        return mme_port;
    }

    public void setMme_port(String mme_port) {
        this.mme_port = mme_port;
    }

    public String getEnb_port() {
        return enb_port;
    }

    public void setEnb_port(String enb_port) {
        this.enb_port = enb_port;
    }

    public String getTac() {
        return tac;
    }

    public void setTac(String tac) {
        this.tac = tac;
    }

    public String getCell_id() {
        return cell_id;
    }

    public void setCell_id(String cell_id) {
        this.cell_id = cell_id;
    }

    public String getOther_tac() {
        return other_tac;
    }

    public void setOther_tac(String other_tac) {
        this.other_tac = other_tac;
    }

    public String getOther_eci() {
        return other_eci;
    }

    public void setOther_eci(String other_eci) {
        this.other_eci = other_eci;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getReq_count() {
        return req_count;
    }

    public void setReq_count(String req_count) {
        this.req_count = req_count;
    }

    public String getRes_count() {
        return res_count;
    }

    public void setRes_count(String res_count) {
        this.res_count = res_count;
    }

    public String getApn() {
        return apn;
    }

    public void setApn(String apn) {
        this.apn = apn;
    }

    public String getEps_bearer_number() {
        return eps_bearer_number;
    }

    public void setEps_bearer_number(String eps_bearer_number) {
        this.eps_bearer_number = eps_bearer_number;
    }

    public String getBearer_id1() {
        return bearer_id1;
    }

    public void setBearer_id1(String bearer_id1) {
        this.bearer_id1 = bearer_id1;
    }

    public String getBearer_type1() {
        return bearer_type1;
    }

    public void setBearer_type1(String bearer_type1) {
        this.bearer_type1 = bearer_type1;
    }

    public String getBearer_qci1() {
        return bearer_qci1;
    }

    public void setBearer_qci1(String bearer_qci1) {
        this.bearer_qci1 = bearer_qci1;
    }

    public String getBearer_status1() {
        return bearer_status1;
    }

    public void setBearer_status1(String bearer_status1) {
        this.bearer_status1 = bearer_status1;
    }

    public String getBearer_enb_gtp_teid1() {
        return bearer_enb_gtp_teid1;
    }

    public void setBearer_enb_gtp_teid1(String bearer_enb_gtp_teid1) {
        this.bearer_enb_gtp_teid1 = bearer_enb_gtp_teid1;
    }

    public String getBearer_sgw_gtp_teid1() {
        return bearer_sgw_gtp_teid1;
    }

    public void setBearer_sgw_gtp_teid1(String bearer_sgw_gtp_teid1) {
        this.bearer_sgw_gtp_teid1 = bearer_sgw_gtp_teid1;
    }

    public String getBearer_id2() {
        return bearer_id2;
    }

    public void setBearer_id2(String bearer_id2) {
        this.bearer_id2 = bearer_id2;
    }

    public String getBearer_type2() {
        return bearer_type2;
    }

    public void setBearer_type2(String bearer_type2) {
        this.bearer_type2 = bearer_type2;
    }

    public String getBearer_qci2() {
        return bearer_qci2;
    }

    public void setBearer_qci2(String bearer_qci2) {
        this.bearer_qci2 = bearer_qci2;
    }

    public String getBearer_status2() {
        return bearer_status2;
    }

    public void setBearer_status2(String bearer_status2) {
        this.bearer_status2 = bearer_status2;
    }

    public String getBearer_enb_gtp_teid2() {
        return bearer_enb_gtp_teid2;
    }

    public void setBearer_enb_gtp_teid2(String bearer_enb_gtp_teid2) {
        this.bearer_enb_gtp_teid2 = bearer_enb_gtp_teid2;
    }

    public String getBearer_sgw_gtp_teid2() {
        return bearer_sgw_gtp_teid2;
    }

    public void setBearer_sgw_gtp_teid2(String bearer_sgw_gtp_teid2) {
        this.bearer_sgw_gtp_teid2 = bearer_sgw_gtp_teid2;
    }

    public String getBearer_id3() {
        return bearer_id3;
    }

    public void setBearer_id3(String bearer_id3) {
        this.bearer_id3 = bearer_id3;
    }

    public String getBearer_type3() {
        return bearer_type3;
    }

    public void setBearer_type3(String bearer_type3) {
        this.bearer_type3 = bearer_type3;
    }

    public String getBearer_qci3() {
        return bearer_qci3;
    }

    public void setBearer_qci3(String bearer_qci3) {
        this.bearer_qci3 = bearer_qci3;
    }

    public String getBearer_status3() {
        return bearer_status3;
    }

    public void setBearer_status3(String bearer_status3) {
        this.bearer_status3 = bearer_status3;
    }

    public String getBearer_enb_gtp_teid3() {
        return bearer_enb_gtp_teid3;
    }

    public void setBearer_enb_gtp_teid3(String bearer_enb_gtp_teid3) {
        this.bearer_enb_gtp_teid3 = bearer_enb_gtp_teid3;
    }

    public String getBearer_sgw_gtp_teid3() {
        return bearer_sgw_gtp_teid3;
    }

    public void setBearer_sgw_gtp_teid3(String bearer_sgw_gtp_teid3) {
        this.bearer_sgw_gtp_teid3 = bearer_sgw_gtp_teid3;
    }

    public String getBearer_id4() {
        return bearer_id4;
    }

    public void setBearer_id4(String bearer_id4) {
        this.bearer_id4 = bearer_id4;
    }

    public String getBearer_type4() {
        return bearer_type4;
    }

    public void setBearer_type4(String bearer_type4) {
        this.bearer_type4 = bearer_type4;
    }

    public String getBearer_qci4() {
        return bearer_qci4;
    }

    public void setBearer_qci4(String bearer_qci4) {
        this.bearer_qci4 = bearer_qci4;
    }

    public String getBearer_status4() {
        return bearer_status4;
    }

    public void setBearer_status4(String bearer_status4) {
        this.bearer_status4 = bearer_status4;
    }

    public String getBearer_enb_gtp_teid4() {
        return bearer_enb_gtp_teid4;
    }

    public void setBearer_enb_gtp_teid4(String bearer_enb_gtp_teid4) {
        this.bearer_enb_gtp_teid4 = bearer_enb_gtp_teid4;
    }

    public String getBearer_sgw_gtp_teid4() {
        return bearer_sgw_gtp_teid4;
    }

    public void setBearer_sgw_gtp_teid4(String bearer_sgw_gtp_teid4) {
        this.bearer_sgw_gtp_teid4 = bearer_sgw_gtp_teid4;
    }

    public String getBearer_id5() {
        return bearer_id5;
    }

    public void setBearer_id5(String bearer_id5) {
        this.bearer_id5 = bearer_id5;
    }

    public String getBearer_type5() {
        return bearer_type5;
    }

    public void setBearer_type5(String bearer_type5) {
        this.bearer_type5 = bearer_type5;
    }

    public String getBearer_qci5() {
        return bearer_qci5;
    }

    public void setBearer_qci5(String bearer_qci5) {
        this.bearer_qci5 = bearer_qci5;
    }

    public String getBearer_status5() {
        return bearer_status5;
    }

    public void setBearer_status5(String bearer_status5) {
        this.bearer_status5 = bearer_status5;
    }

    public String getBearer_enb_gtp_teid5() {
        return bearer_enb_gtp_teid5;
    }

    public void setBearer_enb_gtp_teid5(String bearer_enb_gtp_teid5) {
        this.bearer_enb_gtp_teid5 = bearer_enb_gtp_teid5;
    }

    public String getBearer_sgw_gtp_teid5() {
        return bearer_sgw_gtp_teid5;
    }

    public void setBearer_sgw_gtp_teid5(String bearer_sgw_gtp_teid5) {
        this.bearer_sgw_gtp_teid5 = bearer_sgw_gtp_teid5;
    }

    public String getBearer_id6() {
        return bearer_id6;
    }

    public void setBearer_id6(String bearer_id6) {
        this.bearer_id6 = bearer_id6;
    }

    public String getBearer_type6() {
        return bearer_type6;
    }

    public void setBearer_type6(String bearer_type6) {
        this.bearer_type6 = bearer_type6;
    }

    public String getBearer_qci6() {
        return bearer_qci6;
    }

    public void setBearer_qci6(String bearer_qci6) {
        this.bearer_qci6 = bearer_qci6;
    }

    public String getBearer_status6() {
        return bearer_status6;
    }

    public void setBearer_status6(String bearer_status6) {
        this.bearer_status6 = bearer_status6;
    }

    public String getBearer_enb_gtp_teid6() {
        return bearer_enb_gtp_teid6;
    }

    public void setBearer_enb_gtp_teid6(String bearer_enb_gtp_teid6) {
        this.bearer_enb_gtp_teid6 = bearer_enb_gtp_teid6;
    }

    public String getBearer_sgw_gtp_teid6() {
        return bearer_sgw_gtp_teid6;
    }

    public void setBearer_sgw_gtp_teid6(String bearer_sgw_gtp_teid6) {
        this.bearer_sgw_gtp_teid6 = bearer_sgw_gtp_teid6;
    }

    public String getBearer_id7() {
        return bearer_id7;
    }

    public void setBearer_id7(String bearer_id7) {
        this.bearer_id7 = bearer_id7;
    }

    public String getBearer_type7() {
        return bearer_type7;
    }

    public void setBearer_type7(String bearer_type7) {
        this.bearer_type7 = bearer_type7;
    }

    public String getBearer_qci7() {
        return bearer_qci7;
    }

    public void setBearer_qci7(String bearer_qci7) {
        this.bearer_qci7 = bearer_qci7;
    }

    public String getBearer_status7() {
        return bearer_status7;
    }

    public void setBearer_status7(String bearer_status7) {
        this.bearer_status7 = bearer_status7;
    }

    public String getBearer_enb_gtp_teid7() {
        return bearer_enb_gtp_teid7;
    }

    public void setBearer_enb_gtp_teid7(String bearer_enb_gtp_teid7) {
        this.bearer_enb_gtp_teid7 = bearer_enb_gtp_teid7;
    }

    public String getBearer_sgw_gtp_teid7() {
        return bearer_sgw_gtp_teid7;
    }

    public void setBearer_sgw_gtp_teid7(String bearer_sgw_gtp_teid7) {
        this.bearer_sgw_gtp_teid7 = bearer_sgw_gtp_teid7;
    }

    public String getBearer_id8() {
        return bearer_id8;
    }

    public void setBearer_id8(String bearer_id8) {
        this.bearer_id8 = bearer_id8;
    }

    public String getBearer_type8() {
        return bearer_type8;
    }

    public void setBearer_type8(String bearer_type8) {
        this.bearer_type8 = bearer_type8;
    }

    public String getBearer_qci8() {
        return bearer_qci8;
    }

    public void setBearer_qci8(String bearer_qci8) {
        this.bearer_qci8 = bearer_qci8;
    }

    public String getBearer_status8() {
        return bearer_status8;
    }

    public void setBearer_status8(String bearer_status8) {
        this.bearer_status8 = bearer_status8;
    }

    public String getBearer_enb_gtp_teid8() {
        return bearer_enb_gtp_teid8;
    }

    public void setBearer_enb_gtp_teid8(String bearer_enb_gtp_teid8) {
        this.bearer_enb_gtp_teid8 = bearer_enb_gtp_teid8;
    }

    public String getBearer_sgw_gtp_teid8() {
        return bearer_sgw_gtp_teid8;
    }

    public void setBearer_sgw_gtp_teid8(String bearer_sgw_gtp_teid8) {
        this.bearer_sgw_gtp_teid8 = bearer_sgw_gtp_teid8;
    }

    public String getBearer_id9() {
        return bearer_id9;
    }

    public void setBearer_id9(String bearer_id9) {
        this.bearer_id9 = bearer_id9;
    }

    public String getBearer_type9() {
        return bearer_type9;
    }

    public void setBearer_type9(String bearer_type9) {
        this.bearer_type9 = bearer_type9;
    }

    public String getBearer_qci9() {
        return bearer_qci9;
    }

    public void setBearer_qci9(String bearer_qci9) {
        this.bearer_qci9 = bearer_qci9;
    }

    public String getBearer_status9() {
        return bearer_status9;
    }

    public void setBearer_status9(String bearer_status9) {
        this.bearer_status9 = bearer_status9;
    }

    public String getBearer_enb_gtp_teid9() {
        return bearer_enb_gtp_teid9;
    }

    public void setBearer_enb_gtp_teid9(String bearer_enb_gtp_teid9) {
        this.bearer_enb_gtp_teid9 = bearer_enb_gtp_teid9;
    }

    public String getBearer_sgw_gtp_teid9() {
        return bearer_sgw_gtp_teid9;
    }

    public void setBearer_sgw_gtp_teid9(String bearer_sgw_gtp_teid9) {
        this.bearer_sgw_gtp_teid9 = bearer_sgw_gtp_teid9;
    }

    public String getBearer_id10() {
        return bearer_id10;
    }

    public void setBearer_id10(String bearer_id10) {
        this.bearer_id10 = bearer_id10;
    }

    public String getBearer_type10() {
        return bearer_type10;
    }

    public void setBearer_type10(String bearer_type10) {
        this.bearer_type10 = bearer_type10;
    }

    public String getBearer_qci10() {
        return bearer_qci10;
    }

    public void setBearer_qci10(String bearer_qci10) {
        this.bearer_qci10 = bearer_qci10;
    }

    public String getBearer_status10() {
        return bearer_status10;
    }

    public void setBearer_status10(String bearer_status10) {
        this.bearer_status10 = bearer_status10;
    }

    public String getBearer_enb_gtp_teid10() {
        return bearer_enb_gtp_teid10;
    }

    public void setBearer_enb_gtp_teid10(String bearer_enb_gtp_teid10) {
        this.bearer_enb_gtp_teid10 = bearer_enb_gtp_teid10;
    }

    public String getBearer_sgw_gtp_teid10() {
        return bearer_sgw_gtp_teid10;
    }

    public void setBearer_sgw_gtp_teid10(String bearer_sgw_gtp_teid10) {
        this.bearer_sgw_gtp_teid10 = bearer_sgw_gtp_teid10;
    }

    public String getBearer_id11() {
        return bearer_id11;
    }

    public void setBearer_id11(String bearer_id11) {
        this.bearer_id11 = bearer_id11;
    }

    public String getBearer_type11() {
        return bearer_type11;
    }

    public void setBearer_type11(String bearer_type11) {
        this.bearer_type11 = bearer_type11;
    }

    public String getBearer_qci11() {
        return bearer_qci11;
    }

    public void setBearer_qci11(String bearer_qci11) {
        this.bearer_qci11 = bearer_qci11;
    }

    public String getBearer_status11() {
        return bearer_status11;
    }

    public void setBearer_status11(String bearer_status11) {
        this.bearer_status11 = bearer_status11;
    }

    public String getBearer_enb_gtp_teid11() {
        return bearer_enb_gtp_teid11;
    }

    public void setBearer_enb_gtp_teid11(String bearer_enb_gtp_teid11) {
        this.bearer_enb_gtp_teid11 = bearer_enb_gtp_teid11;
    }

    public String getBearer_sgw_gtp_teid11() {
        return bearer_sgw_gtp_teid11;
    }

    public void setBearer_sgw_gtp_teid11(String bearer_sgw_gtp_teid11) {
        this.bearer_sgw_gtp_teid11 = bearer_sgw_gtp_teid11;
    }

    public String getBearer_id12() {
        return bearer_id12;
    }

    public void setBearer_id12(String bearer_id12) {
        this.bearer_id12 = bearer_id12;
    }

    public String getBearer_type12() {
        return bearer_type12;
    }

    public void setBearer_type12(String bearer_type12) {
        this.bearer_type12 = bearer_type12;
    }

    public String getBearer_qci12() {
        return bearer_qci12;
    }

    public void setBearer_qci12(String bearer_qci12) {
        this.bearer_qci12 = bearer_qci12;
    }

    public String getBearer_status12() {
        return bearer_status12;
    }

    public void setBearer_status12(String bearer_status12) {
        this.bearer_status12 = bearer_status12;
    }

    public String getBearer_enb_gtp_teid12() {
        return bearer_enb_gtp_teid12;
    }

    public void setBearer_enb_gtp_teid12(String bearer_enb_gtp_teid12) {
        this.bearer_enb_gtp_teid12 = bearer_enb_gtp_teid12;
    }

    public String getBearer_sgw_gtp_teid12() {
        return bearer_sgw_gtp_teid12;
    }

    public void setBearer_sgw_gtp_teid12(String bearer_sgw_gtp_teid12) {
        this.bearer_sgw_gtp_teid12 = bearer_sgw_gtp_teid12;
    }

    public String getBearer_id13() {
        return bearer_id13;
    }

    public void setBearer_id13(String bearer_id13) {
        this.bearer_id13 = bearer_id13;
    }

    public String getBearer_type13() {
        return bearer_type13;
    }

    public void setBearer_type13(String bearer_type13) {
        this.bearer_type13 = bearer_type13;
    }

    public String getBearer_qci13() {
        return bearer_qci13;
    }

    public void setBearer_qci13(String bearer_qci13) {
        this.bearer_qci13 = bearer_qci13;
    }

    public String getBearer_status13() {
        return bearer_status13;
    }

    public void setBearer_status13(String bearer_status13) {
        this.bearer_status13 = bearer_status13;
    }

    public String getBearer_enb_gtp_teid13() {
        return bearer_enb_gtp_teid13;
    }

    public void setBearer_enb_gtp_teid13(String bearer_enb_gtp_teid13) {
        this.bearer_enb_gtp_teid13 = bearer_enb_gtp_teid13;
    }

    public String getBearer_sgw_gtp_teid13() {
        return bearer_sgw_gtp_teid13;
    }

    public void setBearer_sgw_gtp_teid13(String bearer_sgw_gtp_teid13) {
        this.bearer_sgw_gtp_teid13 = bearer_sgw_gtp_teid13;
    }

    public String getBearer_id14() {
        return bearer_id14;
    }

    public void setBearer_id14(String bearer_id14) {
        this.bearer_id14 = bearer_id14;
    }

    public String getBearer_type14() {
        return bearer_type14;
    }

    public void setBearer_type14(String bearer_type14) {
        this.bearer_type14 = bearer_type14;
    }

    public String getBearer_qci14() {
        return bearer_qci14;
    }

    public void setBearer_qci14(String bearer_qci14) {
        this.bearer_qci14 = bearer_qci14;
    }

    public String getBearer_status14() {
        return bearer_status14;
    }

    public void setBearer_status14(String bearer_status14) {
        this.bearer_status14 = bearer_status14;
    }

    public String getBearer_enb_gtp_teid14() {
        return bearer_enb_gtp_teid14;
    }

    public void setBearer_enb_gtp_teid14(String bearer_enb_gtp_teid14) {
        this.bearer_enb_gtp_teid14 = bearer_enb_gtp_teid14;
    }

    public String getBearer_sgw_gtp_teid14() {
        return bearer_sgw_gtp_teid14;
    }

    public void setBearer_sgw_gtp_teid14(String bearer_sgw_gtp_teid14) {
        this.bearer_sgw_gtp_teid14 = bearer_sgw_gtp_teid14;
    }

    public String getBearer_id15() {
        return bearer_id15;
    }

    public void setBearer_id15(String bearer_id15) {
        this.bearer_id15 = bearer_id15;
    }

    public String getBearer_type15() {
        return bearer_type15;
    }

    public void setBearer_type15(String bearer_type15) {
        this.bearer_type15 = bearer_type15;
    }

    public String getBearer_qci15() {
        return bearer_qci15;
    }

    public void setBearer_qci15(String bearer_qci15) {
        this.bearer_qci15 = bearer_qci15;
    }

    public String getBearer_status15() {
        return bearer_status15;
    }

    public void setBearer_status15(String bearer_status15) {
        this.bearer_status15 = bearer_status15;
    }

    public String getBearer_enb_gtp_teid15() {
        return bearer_enb_gtp_teid15;
    }

    public void setBearer_enb_gtp_teid15(String bearer_enb_gtp_teid15) {
        this.bearer_enb_gtp_teid15 = bearer_enb_gtp_teid15;
    }

    public String getBearer_sgw_gtp_teid15() {
        return bearer_sgw_gtp_teid15;
    }

    public void setBearer_sgw_gtp_teid15(String bearer_sgw_gtp_teid15) {
        this.bearer_sgw_gtp_teid15 = bearer_sgw_gtp_teid15;
    }

    public String getS_year() {
        return s_year;
    }

    public void setS_year(String s_year) {
        this.s_year = s_year;
    }

    public String getS_month() {
        return s_month;
    }

    public void setS_month(String s_month) {
        this.s_month = s_month;
    }

    public String getS_day() {
        return s_day;
    }

    public void setS_day(String s_day) {
        this.s_day = s_day;
    }

    public String getS_hour() {
        return s_hour;
    }

    public void setS_hour(String s_hour) {
        this.s_hour = s_hour;
    }

    public String getS_minute() {
        return s_minute;
    }

    public void setS_minute(String s_minute) {
        this.s_minute = s_minute;
    }

    public String getRequest_cause() {
        return request_cause;
    }

    public void setRequest_cause(String request_cause) {
        this.request_cause = request_cause;
    }

    public String getOld_mme_group_id_1() {
        return old_mme_group_id_1;
    }

    public void setOld_mme_group_id_1(String old_mme_group_id_1) {
        this.old_mme_group_id_1 = old_mme_group_id_1;
    }

    public String getPaging_type() {
        return paging_type;
    }

    public void setPaging_type(String paging_type) {
        this.paging_type = paging_type;
    }

    public String getKeyword_2() {
        return keyword_2;
    }

    public void setKeyword_2(String keyword_2) {
        this.keyword_2 = keyword_2;
    }

    public String getKeyword_3() {
        return keyword_3;
    }

    public void setKeyword_3(String keyword_3) {
        this.keyword_3 = keyword_3;
    }

    public String getKeyword_4() {
        return keyword_4;
    }

    public void setKeyword_4(String keyword_4) {
        this.keyword_4 = keyword_4;
    }

    public String getOld_mme_code_1() {
        return old_mme_code_1;
    }

    public void setOld_mme_code_1(String old_mme_code_1) {
        this.old_mme_code_1 = old_mme_code_1;
    }

    public String getOld_m_tmsi() {
        return old_m_tmsi;
    }

    public void setOld_m_tmsi(String old_m_tmsi) {
        this.old_m_tmsi = old_m_tmsi;
    }

    public String getBearer_1_request_cause() {
        return bearer_1_request_cause;
    }

    public void setBearer_1_request_cause(String bearer_1_request_cause) {
        this.bearer_1_request_cause = bearer_1_request_cause;
    }

    public String getBearer_1_failure_cause() {
        return bearer_1_failure_cause;
    }

    public void setBearer_1_failure_cause(String bearer_1_failure_cause) {
        this.bearer_1_failure_cause = bearer_1_failure_cause;
    }

    public String getBearer_2_request_cause() {
        return bearer_2_request_cause;
    }

    public void setBearer_2_request_cause(String bearer_2_request_cause) {
        this.bearer_2_request_cause = bearer_2_request_cause;
    }

    public String getBearer_2_failure_cause() {
        return bearer_2_failure_cause;
    }

    public void setBearer_2_failure_cause(String bearer_2_failure_cause) {
        this.bearer_2_failure_cause = bearer_2_failure_cause;
    }

    public String getBearer_3_request_cause() {
        return bearer_3_request_cause;
    }

    public void setBearer_3_request_cause(String bearer_3_request_cause) {
        this.bearer_3_request_cause = bearer_3_request_cause;
    }

    public String getBearer_3_failure_cause() {
        return bearer_3_failure_cause;
    }

    public void setBearer_3_failure_cause(String bearer_3_failure_cause) {
        this.bearer_3_failure_cause = bearer_3_failure_cause;
    }

    public String getBearer_4_request_cause() {
        return bearer_4_request_cause;
    }

    public void setBearer_4_request_cause(String bearer_4_request_cause) {
        this.bearer_4_request_cause = bearer_4_request_cause;
    }

    public String getBearer_4_failure_cause() {
        return bearer_4_failure_cause;
    }

    public void setBearer_4_failure_cause(String bearer_4_failure_cause) {
        this.bearer_4_failure_cause = bearer_4_failure_cause;
    }

    public String getBearer_5_request_cause() {
        return bearer_5_request_cause;
    }

    public void setBearer_5_request_cause(String bearer_5_request_cause) {
        this.bearer_5_request_cause = bearer_5_request_cause;
    }

    public String getBearer_5_failure_cause() {
        return bearer_5_failure_cause;
    }

    public void setBearer_5_failure_cause(String bearer_5_failure_cause) {
        this.bearer_5_failure_cause = bearer_5_failure_cause;
    }

    public String getBearer_6_request_cause() {
        return bearer_6_request_cause;
    }

    public void setBearer_6_request_cause(String bearer_6_request_cause) {
        this.bearer_6_request_cause = bearer_6_request_cause;
    }

    public String getBearer_6_failure_cause() {
        return bearer_6_failure_cause;
    }

    public void setBearer_6_failure_cause(String bearer_6_failure_cause) {
        this.bearer_6_failure_cause = bearer_6_failure_cause;
    }

    public String getBearer_7_request_cause() {
        return bearer_7_request_cause;
    }

    public void setBearer_7_request_cause(String bearer_7_request_cause) {
        this.bearer_7_request_cause = bearer_7_request_cause;
    }

    public String getBearer_7_failure_cause() {
        return bearer_7_failure_cause;
    }

    public void setBearer_7_failure_cause(String bearer_7_failure_cause) {
        this.bearer_7_failure_cause = bearer_7_failure_cause;
    }

    public String getBearer_8_request_cause() {
        return bearer_8_request_cause;
    }

    public void setBearer_8_request_cause(String bearer_8_request_cause) {
        this.bearer_8_request_cause = bearer_8_request_cause;
    }

    public String getBearer_8_failure_cause() {
        return bearer_8_failure_cause;
    }

    public void setBearer_8_failure_cause(String bearer_8_failure_cause) {
        this.bearer_8_failure_cause = bearer_8_failure_cause;
    }

    public String getBearer_9_request_cause() {
        return bearer_9_request_cause;
    }

    public void setBearer_9_request_cause(String bearer_9_request_cause) {
        this.bearer_9_request_cause = bearer_9_request_cause;
    }

    public String getBearer_9_failure_cause() {
        return bearer_9_failure_cause;
    }

    public void setBearer_9_failure_cause(String bearer_9_failure_cause) {
        this.bearer_9_failure_cause = bearer_9_failure_cause;
    }

    public String getBearer_10_request_cause() {
        return bearer_10_request_cause;
    }

    public void setBearer_10_request_cause(String bearer_10_request_cause) {
        this.bearer_10_request_cause = bearer_10_request_cause;
    }

    public String getBearer_10_failure_cause() {
        return bearer_10_failure_cause;
    }

    public void setBearer_10_failure_cause(String bearer_10_failure_cause) {
        this.bearer_10_failure_cause = bearer_10_failure_cause;
    }

    public String getBearer_11_request_cause() {
        return bearer_11_request_cause;
    }

    public void setBearer_11_request_cause(String bearer_11_request_cause) {
        this.bearer_11_request_cause = bearer_11_request_cause;
    }

    public String getBearer_11_failure_cause() {
        return bearer_11_failure_cause;
    }

    public void setBearer_11_failure_cause(String bearer_11_failure_cause) {
        this.bearer_11_failure_cause = bearer_11_failure_cause;
    }

    public String getBearer_12_request_cause() {
        return bearer_12_request_cause;
    }

    public void setBearer_12_request_cause(String bearer_12_request_cause) {
        this.bearer_12_request_cause = bearer_12_request_cause;
    }

    public String getBearer_12_failure_cause() {
        return bearer_12_failure_cause;
    }

    public void setBearer_12_failure_cause(String bearer_12_failure_cause) {
        this.bearer_12_failure_cause = bearer_12_failure_cause;
    }

    public String getBearer_13_request_cause() {
        return bearer_13_request_cause;
    }

    public void setBearer_13_request_cause(String bearer_13_request_cause) {
        this.bearer_13_request_cause = bearer_13_request_cause;
    }

    public String getBearer_13_failure_cause() {
        return bearer_13_failure_cause;
    }

    public void setBearer_13_failure_cause(String bearer_13_failure_cause) {
        this.bearer_13_failure_cause = bearer_13_failure_cause;
    }

    public String getBearer_14_request_cause() {
        return bearer_14_request_cause;
    }

    public void setBearer_14_request_cause(String bearer_14_request_cause) {
        this.bearer_14_request_cause = bearer_14_request_cause;
    }

    public String getBearer_14_failure_cause() {
        return bearer_14_failure_cause;
    }

    public void setBearer_14_failure_cause(String bearer_14_failure_cause) {
        this.bearer_14_failure_cause = bearer_14_failure_cause;
    }

    public String getBearer_15_request_cause() {
        return bearer_15_request_cause;
    }

    public void setBearer_15_request_cause(String bearer_15_request_cause) {
        this.bearer_15_request_cause = bearer_15_request_cause;
    }

    public String getBearer_15_failure_cause() {
        return bearer_15_failure_cause;
    }

    public void setBearer_15_failure_cause(String bearer_15_failure_cause) {
        this.bearer_15_failure_cause = bearer_15_failure_cause;
    }

    public String getReserve_1() {
        return reserve_1;
    }

    public void setReserve_1(String reserve_1) {
        this.reserve_1 = reserve_1;
    }

    public String getReserve_2() {
        return reserve_2;
    }

    public void setReserve_2(String reserve_2) {
        this.reserve_2 = reserve_2;
    }

    public String getReserve_3() {
        return reserve_3;
    }

    public void setReserve_3(String reserve_3) {
        this.reserve_3 = reserve_3;
    }

    public String getOld_tac() {
        return old_tac;
    }

    public void setOld_tac(String old_tac) {
        this.old_tac = old_tac;
    }

    public String getOld_eci() {
        return old_eci;
    }

    public void setOld_eci(String old_eci) {
        this.old_eci = old_eci;
    }
}
