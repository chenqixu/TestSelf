package com.newland.bi.bigdata.parser.stream;

import com.newland.bi.bigdata.changecode.FileUtil;
import com.newland.bi.bigdata.time.TimeCostUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StreamParserUtil
 *
 * @author chenqixu
 */
public class StreamParserUtil {
    public static final String parserRule = ",,,,parserSubstrFirst-86,,,parserTimestamp-yyyyMMddHHmmss,,parserTimestamp-yyyyMMddHHmmss,,,,,parserConvertHex-10,parserConvertHex-10,,,,,,,,,,,,,,,,";
    public static final String field = "length,city,interface,xdr_id,rat,imsi,imei,msisdn,procedure_type,subprocedure_type,procedure_start_time,procedure_delay_time,procedure_end_time,procedure_status,cause,nas_cause,s1ap_cause1,s1ap_cause2,keyword,enb_ue_s1ap_id,mme_ue_s1ap_id,old_mme_group_id,old_mme_code,m_tmsi,mcc,mnc,lac,tmsi,user_ipv4,user_ipv6,machine_ip_add_type,mme_ip_add,enb_ip_add,mme_port,enb_port,tac,cell_id,other_tac,other_eci,mac,req_count,res_count,apn,eps_bearer_number,bearer_id1,bearer_type1,bearer_qci1,bearer_status1,bearer_enb_gtp_teid1,bearer_sgw_gtp_teid1,bearer_id2,bearer_type2,bearer_qci2,bearer_status2,bearer_enb_gtp_teid2,bearer_sgw_gtp_teid2,bearer_id3,bearer_type3,bearer_qci3,bearer_status3,bearer_enb_gtp_teid3,bearer_sgw_gtp_teid3,bearer_id4,bearer_type4,bearer_qci4,bearer_status4,bearer_enb_gtp_teid4,bearer_sgw_gtp_teid4,bearer_id5,bearer_type5,bearer_qci5,bearer_status5,bearer_enb_gtp_teid5,bearer_sgw_gtp_teid5,bearer_id6,bearer_type6,bearer_qci6,bearer_status6,bearer_enb_gtp_teid6,bearer_sgw_gtp_teid6,bearer_id7,bearer_type7,bearer_qci7,bearer_status7,bearer_enb_gtp_teid7,bearer_sgw_gtp_teid7,bearer_id8,bearer_type8,bearer_qci8,bearer_status8,bearer_enb_gtp_teid8,bearer_sgw_gtp_teid8,bearer_id9,bearer_type9,bearer_qci9,bearer_status9,bearer_enb_gtp_teid9,bearer_sgw_gtp_teid9,bearer_id10,bearer_type10,bearer_qci10,bearer_status10,bearer_enb_gtp_teid10,bearer_sgw_gtp_teid10,bearer_id11,bearer_type11,bearer_qci11,bearer_status11,bearer_enb_gtp_teid11,bearer_sgw_gtp_teid11,bearer_id12,bearer_type12,bearer_qci12,bearer_status12,bearer_enb_gtp_teid12,bearer_sgw_gtp_teid12,bearer_id13,bearer_type13,bearer_qci13,bearer_status13,bearer_enb_gtp_teid13,bearer_sgw_gtp_teid13,bearer_id14,bearer_type14,bearer_qci14,bearer_status14,bearer_enb_gtp_teid14,bearer_sgw_gtp_teid14,bearer_id15,bearer_type15,bearer_qci15,bearer_status15,bearer_enb_gtp_teid15,bearer_sgw_gtp_teid15,s_year,s_month,s_day,s_hour,s_minute,request_cause,old_mme_group_id_1,paging_type,keyword_2,keyword_3,keyword_4,old_mme_code_1,old_m_tmsi,bearer_1_request_cause,bearer_1_failure_cause,bearer_2_request_cause,bearer_2_failure_cause,bearer_3_request_cause,bearer_3_failure_cause,bearer_4_request_cause,bearer_4_failure_cause,bearer_5_request_cause,bearer_5_failure_cause,bearer_6_request_cause,bearer_6_failure_cause,bearer_7_request_cause,bearer_7_failure_cause,bearer_8_request_cause,bearer_8_failure_cause,bearer_9_request_cause,bearer_9_failure_cause,bearer_10_request_cause,bearer_10_failure_cause,bearer_11_request_cause,bearer_11_failure_cause,bearer_12_request_cause,bearer_12_failure_cause,bearer_13_request_cause,bearer_13_failure_cause,bearer_14_request_cause,bearer_14_failure_cause,bearer_15_request_cause,bearer_15_failure_cause,reserve_1,reserve_2,reserve_3,old_tac,old_eci";
    public static final String sink = "city,xdr_id,imsi,imei,msisdn,procedure_type,subprocedure_type,procedure_start_time,procedure_delay_time,procedure_end_time,procedure_status,old_mme_group_id,old_mme_code,lac,tac,cell_id,other_tac,other_eci,home_code,msisdn_home_code,old_mme_group_id_1,old_mme_code_1,old_m_tmsi,old_tac,old_eci,cause,keyword,mme_ue_s1ap_id,request_cause,keyword_2,keyword_3,keyword_4";
    private static final Logger logger = LoggerFactory.getLogger(StreamParserUtil.class);
    private static Map<String, IStreamParser> parserMap = new HashMap<>();
    private static ParserConvertHex parserConvertHex = new ParserConvertHex("10");
    private static ParserSubstrFirst parserSubstrFirst = new ParserSubstrFirst("86");
    private static ParserTimestamp parserTimestamp = new ParserTimestamp("yyyyMMddHHmmss");
    private static String[] parserRulearr;
    private static String[] fieldarr;
    private static String[] sinkarr;

    static {
        parserMap.put("parserConvertHex-10", parserConvertHex);
        parserMap.put("parserSubstrFirst-86", parserSubstrFirst);
        parserMap.put("parserTimestamp-yyyyMMddHHmmss", parserTimestamp);
        parserRulearr = parserRule.split(",", -1);
        fieldarr = field.split(",", -1);
        sinkarr = sink.split(",", -1);
    }

    private DateFormat dateoutFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 动态处理
     *
     * @param path
     */
    public void dynamicDeal(String path) throws Exception {
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        timeCostUtil.start();
        printField(path);
        logger.info("timeCost：{}", timeCostUtil.stopAndGet());
    }

    /**
     * 静态处理
     *
     * @param path
     */
    public void staticDeal(String path) throws Exception {
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        timeCostUtil.start();
        printFieldStatic(path);
        logger.info("timeCost：{}", timeCostUtil.stopAndGet());
    }

    public void printField(String path) throws Exception {
        printField(path, -1);
    }

    public void printField(String path, int limit) throws Exception {
        FileUtil fileUtil = new FileUtil();
        List<String> list = fileUtil.read(path, "UTF-8");
        int cnt = 0;
        for (String str : list) {
            String[] content = str.split("\\|", -1);
            // 源字段对应源内容
            Map<String, String> mapContent = mapping(fieldarr, content);
            String sinkstr = sink(mapContent);
            logger.debug("len：{}，msisdn：{}，procedure_start_time：{}，procedure_end_time：{}，tac：{}，cell_id：{}，sinkstr：{}",
                    content.length, mapContent.get("msisdn"),
                    mapContent.get("procedure_start_time"), mapContent.get("procedure_end_time"),
                    mapContent.get("tac"), mapContent.get("cell_id"), sinkstr);
            cnt++;
            if (limit > 0 && cnt == limit) break;
        }
    }

    private Map<String, String> mapping(String[] field, String[] value) {
        // 解析
        Map<String, String> mapContent = new HashMap<>();
        // 源字段对应源内容
        for (int i = 0; i < value.length; i++) {
            mapContent.put(field[i], value[i]);
        }
        return mapContent;
    }

    private String sink(Map<String, String> value) throws Exception {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < sinkarr.length; i++) {
            String result = value.get(sinkarr[i]);
            IStreamParser iStreamParser = parserMap.get(parserRulearr[i]);
            if (iStreamParser != null) {
                String begin = result;
                result = iStreamParser.parser(result);
                logger.debug("parserRule：{}，start：{}，end：{}", parserRulearr[i], begin, result);
            }
            result = (result == null ? "" : result);
            logger.info("valueMap.put(\"{}\", \"{}\");", sinkarr[i], result);
            sb.append(result);
            if ((i + 1) < sinkarr.length)
                sb.append("|");
        }
        return sb.toString();
    }

    private String sinkStatic(Map<String, String> value) throws Exception {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < sinkarr.length; i++) {
            String result = value.get(sinkarr[i]);
            String begin = result;
            if (i == 4) {
                if (result != null && result.length() > 0 && result.startsWith("86")) {
                    result = result.substring(2);
                }
                logger.debug("begin：{}，result：{}", begin, result);
            } else if (i == 7 || i == 9) {
                if (result != null && result.length() > 0) {
                    result = dateoutFormat.format(new Date(Long.valueOf(result)));
                    logger.debug("begin：{}，result：{}", begin, result);
                }
            } else if (i == 14 || i == 15) {
                if (result != null && result.length() > 0) {
                    result = String.valueOf(Long.parseLong(result, 16));
                    logger.debug("begin：{}，result：{}", begin, result);
                }
            }
            sb.append(result == null ? "" : result);
            if ((i + 1) < sinkarr.length)
                sb.append("|");
        }
        return sb.toString();
    }

    public void printFieldStatic(String path, int limit) throws Exception {
        FileUtil fileUtil = new FileUtil();
        List<String> list = fileUtil.read(path, "UTF-8");
        int cnt = 0;
        for (String str : list) {
            String[] content = str.split("\\|", -1);
            // 源字段对应源内容
            Map<String, String> mapContent = mapping(fieldarr, content);
            String sinkstr = sinkStatic(mapContent);
            logger.debug("len：{}，msisdn：{}，procedure_start_time：{}，procedure_end_time：{}，tac：{}，cell_id：{}，sinkstr：{}",
                    content.length, mapContent.get("msisdn"),
                    mapContent.get("procedure_start_time"), mapContent.get("procedure_end_time"),
                    mapContent.get("tac"), mapContent.get("cell_id"), sinkstr);
            cnt++;
            if (limit > 0 && cnt == limit) break;
        }
    }

    public void printFieldStatic(String path) throws Exception {
        printFieldStatic(path, -1);
    }

}
