package com.bussiness.bi.bigdata.utils.string;

import com.bussiness.bi.bigdata.metric.JvmMetrics;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import com.cqx.common.utils.string.StringUtil;

public class StringUtilsTest {

    private static final String baseStr = "1234567890abcdefghijklmnopqrstuvwxyz~!@#$%^&*()_+";
    private static final char[] arr = baseStr.toCharArray();
    private JvmMetrics jvmMetrics;
    private Random random = new Random();

    @Before
    public void setUp() throws Exception {
        jvmMetrics = JvmMetrics.getVmInfo();
        printMemInfo();
    }

    @After
    public void down() throws Exception {
//        printMemInfo();
    }

    private void printMemInfo() {
//        System.out.println(jvmMetrics.toString());
        jvmMetrics.getDelta();
    }

    @Test
    public void telnumberProcessing() {
        System.out.println(StringUtil.telnumberProcessing("8613509323824"));
    }

    @Test
    public void printSystemProperties() {
        StringUtil.printSystemProperties();
    }

    @Test
    public void columnSplit() {
        StringBuffer _tmp = new StringBuffer("");
        _tmp.append("1").append(StringUtil.COLUMN_SPLIT);
        _tmp.append("2").append(StringUtil.COLUMN_SPLIT);
        _tmp.append("3").append(StringUtil.COLUMN_SPLIT);
        System.out.println(_tmp.toString());
        _tmp.deleteCharAt(_tmp.length() - 1);
        System.out.println(_tmp.toString());
        System.out.println(_tmp.toString().split(String.valueOf(StringUtil.COLUMN_SPLIT)).length);
    }

    @Test
    public void replaceTest() {
        String rule = "a?b";
        rule = rule.replace('?', '#');
        System.out.println(rule);
    }

    @Test
    public void replaceTest1() {
        String rule = "[{\"catgId\":217532,\"parentCatgId\":1,\"catgName\":\"动漫\",\"parentCatgId\":1122";
        System.out.println("##before ##" + rule);
        rule = rule.replace("\"parentCatgId\"", "\"fId\"");
        System.out.println("##replace##" + rule);

        String test1 = "a('b')";
        String[] test1arr = test1.split("'\\)");
        System.out.println(test1arr.length);
    }

    @Test
    public void negativeAssert1() {
        // 创建0到23
        List<String> seqList = StringUtil.generateSeqList(0, 23, 2);
        // 创建9,10,18,20
        List<String> negativeList = Arrays.asList(new String[]{"09", "10", "18", "20"});
        // 剔除，算法1
        StringUtil.negativeAssert1(seqList, negativeList);
        // 打印结果
//        StringUtils.printList(seqList);
        System.out.println(StringUtil.splitList(seqList, "|"));
    }

    @Test
    public void negativeAssert2() {
        // 创建0到23
        List<String> seqList = StringUtil.generateSeqList(0, 23, 2);
        // 创建9,10,18,20
        List<String> negativeList = Arrays.asList(new String[]{"09", "10", "18", "20"});
        // 剔除，算法2
        StringUtil.negativeAssert2(seqList, negativeList);
        // 打印结果
//        StringUtils.printList(seqList);
        System.out.println(StringUtil.splitList(seqList, "|"));
    }

    @Test
    public void switchTest() {
        int cnt = 4;
        switch (cnt) {
            case 0:
            case 3:
                System.out.println("|" + cnt);
                break;
            case 1:
                System.out.println(cnt);
                break;
            case 2:
                System.out.println(cnt);
                break;
            default:
                break;
        }
    }

    @Test
    public void listSizeTest() {
        List<String> list = new ArrayList<>();
        System.out.println(list.size());
        list.add("abc");
        System.out.println(list.get(0));
    }

    @Test
    public void replaseTest() {
        String separator = "\t";// 落地文件分隔符
        separator = separator.replace("\\", "");// 去掉转义符，因为落地时候不用转义
        System.out.println("a" + separator + "b");
    }

    @Test
    public void distinct() {
        // http
        StringUtil.distinct("length,city_1,interface,xdr_id,imsi,imei,msisdn,m_tmsi,ip_add_type,user_ip,sgw_ip_add,enodeb_ip_add,sgw_port,enodeb_port,tac,eci,other_tac,other_eci,rat,apn,sid,app_type_code,procedure_id,procedure_start_time,delay_time,procedure_end_time,app_class_top,app_class,ownclass,l4_protocol,busi_bear_type,source_port,server_ip,destination_port,mcc,mnc,upbytes,downbytes,dura,dura_1,upflow,downflow,updura,downdura,up_packet,down_packet,up_packet_flow,down_packet_flow,busi_behavior_identify,busi_complete_identify,busi_dura,ul_tcp_disordered_packets,dl_tcp_disordered_packets,ul_tcp_retransmission_packets,dl_tcp_retransmission_packets,ul_ip_frag_packets,dl_ip_frag_packets,tcp_built_delay,tcp_confirm_delay,first_tcp_success_delay,first_answer_delay,window_size,mss_size,tcp_attempts_cnt,tcp_connection_status,session_end_flag,host,uri,x_online_host,user_agent,http_content_type,refer_uri,cookie,content_length,target_action,wtp_disruption_type,wtp_disruption_causes,title,keyword,get,post,success,e100,e300,e401,area,city,areaclass,s_year,s_month,s_day,s_hour,s_minute,telnumber,imei_prefix8,terminaltype,mobilevendor,mobiletype,mobileos,sys_reported_time,p_id,page_id,object_type,object_status,http_versions,first_http_answer_delay,last_http_answer_delay,last_ack_answer_delay,browsing_tool,portal_app_collections,mmeues1apid,enbues1apid,location,first_request,enb_sgsn_gtp_teid,sgw_ggsn_gtp_teid,protocol_type,app_content,app_status,user_ipv6,app_server_ipv6,reserve_1,reserve_2,reserve_3,demand_1,demand_2,label,apply_classify,apply_name,web_classify,web_name,search_keyword,urlmd5,parser_tag", ",");
        // other
        StringUtil.distinct("length,city_1,interface,xdr_id,imsi,imei,msisdn,m_tmsi,ip_add_type,user_ip,sgw_ip_add,enodeb_ip_add,sgw_port,enodeb_port,tac,eci,other_tac,other_eci,rat,apn,sid,app_type_code,procedure_start_time,delay_time,procedure_end_time,app_class_top,app_class,ownclass,l4_protocol,busi_bear_type,source_port,server_ip,destination_port,mcc,mnc,upbytes,downbytes,dura,dura_1,upflow,downflow,updura,downdura,up_packet,down_packet,up_packet_flow,down_packet_flow,busi_behavior_identify,busi_complete_identify,busi_dura,ul_tcp_disordered_packets,dl_tcp_disordered_packets,ul_tcp_retransmission_packets,dl_tcp_retransmission_packets,ul_ip_frag_packets,dl_ip_frag_packets,tcp_built_delay,tcp_confirm_delay,first_tcp_success_delay,first_answer_delay,window_size,mss_size,tcp_attempts_cnt,tcp_connection_status,session_end_flag,area,city,areaclass,s_year,s_month,s_day,s_hour,s_minute,telnumber,imei_prefix8,terminaltype,mobilevendor,mobiletype,mobileos,sys_reported_time,protocol_type,app_content,app_status,user_ipv6,app_server_ip_ipv6,mmeues1apid,enbues1apid,enb_sgsn_gtp_teid,sgw_ggsn_gtp_teid,reserve_1,reserve_2,reserve_3,apply_classify,apply_name,label,web_classify,web_name,search_keyword,urlmd5,parser_tag", ",");
        // rtsp
        StringUtil.distinct("length,city_1,interface,xdr_id,imsi,imei,msisdn,m_tmsi,ip_add_type,user_ip,sgw_ip_add,enodeb_ip_add,sgw_port,enodeb_port,tac,eci,other_tac,other_eci,rat,apn,sid,app_type_code,procedure_id,procedure_start_time,delay_time,procedure_end_time,app_class_top,app_class,ownclass,l4_protocol,busi_bear_type,source_port,server_ip,destination_port,mcc,mnc,upbytes,downbytes,dura,dura_1,upflow,downflow,updura,downdura,up_packet,down_packet,up_packet_flow,down_packet_flow,busi_behavior_identify,busi_complete_identify,busi_dura,ul_tcp_disordered_packets,dl_tcp_disordered_packets,ul_tcp_retransmission_packets,dl_tcp_retransmission_packets,ul_ip_frag_packets,dl_ip_frag_packets,tcp_built_delay,tcp_confirm_delay,first_tcp_success_delay,first_answer_delay,window_size,mss_size,tcp_attempts_cnt,tcp_connection_status,session_end_flag,url,user_agent,rtp_server_ip,client_start_prot,client_end_prot,server_start_prot,server_end_prot,video_flux,audio_flux,answer_dely,area,city,areaclass,s_year,s_month,s_day,s_hour,s_minute,telnumber,imei_prefix8,terminaltype,mobilevendor,mobiletype,mobileos,sys_reported_time,p_id,mmeues1apid,enbues1apid,enb_sgsn_gtp_teid,sgw_ggsn_gtp_teid,protocol_type,app_content,app_status,user_ipv6,app_server_ipv6,reserve_1,reserve_2,reserve_3,label,apply_classify,apply_name,web_classify,web_name,search_keyword,urlmd5,parser_tag", ",");
    }

    @Test
    public void arrayTest() {
        int[] array = {1};
        for (int i = 0; i < array.length; i++)
            System.out.println(array[i]);
    }

    private String getRandomStr(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(arr[random.nextInt(arr.length - 1)]);
        }
        return sb.toString();
    }

    @Test
    public void memTest() {
//        String str = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
        List<String> message = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
//            message.add(getRandomStr(100) + "\\r\\n");
            message.add(new StringBuilder().append(getRandomStr(100)).append("\\r\\n").toString());
            if (i % 1000 == 0) {
                printMemInfo();
            }
        }
        System.out.println(message.size());
        System.out.println(message.get(0));
    }
}