package com.newland.bi.bigdata.net;

import com.newland.bi.bigdata.time.TimeCostUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;

public class DpiSocketClientSOUTTest {

    private String ip = "10.1.8.203";
    private int port = 6795;
    private DpiSocketClientSOUT dpiSocketClient;

    @Before
    public void setUp() throws Exception {
        dpiSocketClient = new DpiSocketClientSOUT(ip, port);
        dpiSocketClient.connect();
    }

    @After
    public void tearDown() throws Exception {
        dpiSocketClient.disconnect();
    }

    @Test
    public void sendMsg() throws IOException {
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        timeCostUtil.start();
        String fileName = "d:\\tmp\\data\\dpi\\dpi_ltedata\\LTE_S1UHTTP_010531112002_20190507000000.txt";
        String allName = "length|city_1|interface|xdr_id|imsi|imei|msisdn|m_tmsi|ip_add_type|user_ip|sgw_ip_add|enodeb_ip_add|sgw_port|enodeb_port|tac|eci|other_tac|other_eci|rat|apn|sid|app_type_code|procedure_id|procedure_start_time|delay_time|procedure_end_time|app_class_top|app_class|ownclass|l4_protocol|busi_bear_type|source_port|server_ip|destination_port|mcc|mnc|upbytes|downbytes|dura|dura_1|upflow|downflow|updura|downdura|up_packet|down_packet|up_packet_flow|down_packet_flow|busi_behavior_identify|busi_complete_identify|busi_dura|ul_tcp_disordered_packets|dl_tcp_disordered_packets|ul_tcp_retransmission_packets|dl_tcp_retransmission_packets|ul_ip_frag_packets|dl_ip_frag_packets|tcp_built_delay|tcp_confirm_delay|first_tcp_success_delay|first_answer_delay|window_size|mss_size|tcp_attempts_cnt|tcp_connection_status|session_end_flag|host|uri|x_online_host|user_agent|http_content_type|refer_uri|cookie|content_length|target_action|wtp_disruption_type|wtp_disruption_causes|title|keyword|get|post|success|e100|e300|e401|area|city|areaclass|s_year|s_month|s_day|s_hour|s_minute|telnumber|imei_prefix8|terminaltype|mobilevendor|mobiletype|mobileos|sys_reported_time|p_id|page_id|object_type|object_status|http_versions|first_http_answer_delay|last_http_answer_delay|last_ack_answer_delay|browsing_tool|portal_app_collections|mmeues1apid|enbues1apid|location|first_request|enb_sgsn_gtp_teid|sgw_ggsn_gtp_teid|protocol_type|app_content|app_status|user_ipv6|app_server_ipv6|reserve_1|reserve_2|reserve_3|demand_1|demand_2";
        String orderName = "msisdn|app_class_top|app_class|unknow|eci|imei|tac|procedure_start_time|procedure_end_time|server_ip|destination_port|user_agent|uri|host|http_content_type|upbytes|downbytes|city_1|imsi|delay_time|ownclass|busi_bear_type|mcc|refer_uri|app_content|unknow|unknow|target_action|upflow|downflow";
        String split = "\\|";
        String concat = "|";
        List<String> listContent = DpiSocketClientSOUT.readFile(fileName, "UTF-8", 0);
        for (String content : listContent) {
            String _content = DpiSocketClientSOUT.parserContent(content, allName, orderName, split, concat);
            try {
                String reviceMsg = dpiSocketClient.sendMsg(_content);
            } catch (SocketException e) {
                System.out.println("catch SocketException");
            }
        }
        timeCostUtil.stop();
        System.out.println(String.format("file size：%s，all cost：%s，sendMsg cost：%s",
                listContent.size(), timeCostUtil.getCost(), dpiSocketClient.getIncrementCost()));
    }
}