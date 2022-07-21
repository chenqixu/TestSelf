package com.cqx.work.monitor;

import com.cqx.common.utils.system.TimeCostUtil;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MonitorCheckTest {
    private static final Logger logger = LoggerFactory.getLogger(MonitorCheckTest.class);
    private MonitorCheck monitorCheck;
    private List<String> rules;
    private List<String> values;

    @Before
    public void setUp() throws Exception {
        monitorCheck = new MonitorCheck();
        rules = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            rules.add("host" + i);
        }
        values = new ArrayList<>();
        for (int i = 0; i < 50000; i++) {
            values.add("value" + i);
        }
        values.add("www.host0.com");
    }

    @Test
    public void diff() {
        equalsMatch();
        containsMatch();
        indexOfMatch();
//        regexpMatch();
        listStreamMatch();
//        ahocorasickMatch();
    }

    @Test
    public void ipDIff() {
        IPMatch();
        IPV1Match();
    }

    @Test
    public void hashMapDiff() {
        hashMapGetMatch();
        hashMapContainsKeyMatch();
    }

    @Test
    public void equalsMatch() {
        TimeCostUtil tc = new TimeCostUtil();
        tc.start();
        for (String value : values) {
            monitorCheck.equalsMatch(value, rules);
        }
        logger.info("equals总耗时: {} ms", tc.stopAndGet());
    }

    @Test
    public void containsMatch() {
        TimeCostUtil tc = new TimeCostUtil();
        tc.start();
        for (String value : values) {
            monitorCheck.containsMatch(value, rules);
        }
        logger.info("contains总耗时: {} ms", tc.stopAndGet());
    }

    @Test
    public void indexOfMatch() {
        TimeCostUtil tc = new TimeCostUtil();
        tc.start();
        for (String value : values) {
            monitorCheck.indexOfMatch(value, rules);
        }
        logger.info("indexOf总耗时: {} ms", tc.stopAndGet());
    }

    @Test
    public void regexpMatch() {
        TimeCostUtil tc = new TimeCostUtil();
        tc.start();
        for (String value : values) {
            monitorCheck.regexpMatch(value, rules);
        }
        logger.info("正则总耗时: {} ms", tc.stopAndGet());
    }

    @Test
    public void listStreamMatch() {
        TimeCostUtil tc = new TimeCostUtil();
        tc.start();
        monitorCheck.listStreamMatch(values, rules);
        logger.info("流API总耗时: {} ms", tc.stopAndGet());
    }

    @Test
    public void ahocorasickMatch() {
        TimeCostUtil tc = new TimeCostUtil();
        tc.start();
        for (String value : values) {
            monitorCheck.ahocorasickMatch(value, rules);
        }
        logger.info("AC算法总耗时: {} ms", tc.stopAndGet());
    }

    @Test
    public void IPMatch() {
        TimeCostUtil tc = new TimeCostUtil();
        tc.start();
        Map<String, String> userLogMap = new HashMap<>();
        // server_pub_ip
        userLogMap.put("server_pub_ip", "192.168.0.1");
        // app_server_ip_ipv4_text
        userLogMap.put("app_server_ip_ipv4_text", "192.168.0.1");
        // app_server_ip_ipv6_text
        userLogMap.put("app_server_ip_ipv6_text", "192.168.0.1");
        UserRuleBean userRuleBean = new UserRuleBean();
        // 需要走ip和掩码
        userRuleBean.setDestIp("192.168.0.1");
        userRuleBean.setDestIpMask("24");
        for (int i = 0; i < 50000000; i++) {
            monitorCheck.ipMatch(userLogMap, userRuleBean);
        }
        logger.info("IP校验总耗时: {} ms", tc.stopAndGet());
    }

    @Test
    public void IPV1Match() {
        TimeCostUtil tc = new TimeCostUtil();
        tc.start();
        Map<String, String> userLogMap = new HashMap<>();
        // server_pub_ip
        userLogMap.put("server_pub_ip", "192.168.0.1");
        // app_server_ip_ipv4_text
        userLogMap.put("app_server_ip_ipv4_text", "192.168.0.1");
        // app_server_ip_ipv6_text
        userLogMap.put("app_server_ip_ipv6_text", "192.168.0.1");
        UserRuleBean userRuleBean = new UserRuleBean();
        // 需要走ip和掩码
        userRuleBean.setDestIp("192.168.0.1");
        userRuleBean.setDestIpMask("24");
        for (int i = 0; i < 50000000; i++) {
            monitorCheck.ipv1Match(userLogMap, userRuleBean);
        }
        logger.info("IPV1校验总耗时: {} ms", tc.stopAndGet());
    }

    @Test
    public void checkTimeMatch() throws ParseException {
        TimeCostUtil tc = new TimeCostUtil();
        tc.start();
        Map<String, String> userLogMap = new HashMap<>();
        // start_time
        userLogMap.put("start_time", "2022-07-21 10:11:00.171658000");
        UserRuleBean userRuleBean = new UserRuleBean();
        // 需要start_time、end_time
        userRuleBean.setStartTime("20220721101100");
        userRuleBean.setEndTime("20220721101100");
        for (int i = 0; i < 50000 * 1000; i++) {
            monitorCheck.checkTimeMatch(userLogMap, userRuleBean);
        }
        logger.info("checkTime校验总耗时: {} ms", tc.stopAndGet());
    }

    @Test
    public void checkTimeV1Match() throws ParseException {
        TimeCostUtil tc = new TimeCostUtil();
        tc.start();
        Map<String, String> userLogMap = new HashMap<>();
        // start_time
        userLogMap.put("start_time", "2022-07-21 10:11:00.171658000");
        UserRuleBean userRuleBean = new UserRuleBean();
        // 需要start_time、end_time
        userRuleBean.setStartTime("20220721101100");
        userRuleBean.setEndTime("20220721101100");
        for (int i = 0; i < 50000 * 1000; i++) {
            monitorCheck.checkTimeV1Match(userLogMap, userRuleBean);
        }
        logger.info("checkTimeV1校验总耗时: {} ms", tc.stopAndGet());
    }

    @Test
    public void hashMapGetMatch() {
        ConcurrentHashMap<String, String> RuleServiceMap = new ConcurrentHashMap<>();
        RuleServiceMap.put("user_ip", "1");
        RuleServiceMap.put("app_server_ip", "2");
        RuleServiceMap.put("url", "3");
        RuleServiceMap.put("urlKey", "4");
        RuleServiceMap.put("host", "5");
        RuleServiceMap.put("hostKey", "6");
        List<String> fields = new ArrayList<>();
        fields.add("user_ip");
        fields.add("app_server_ip");
        fields.add("url");
        fields.add("urlKey");
        fields.add("host");
        fields.add("hostKey");
        TimeCostUtil tc = new TimeCostUtil();
        tc.start();
        for (int i = 0; i < 50000 * 1000; i++) {
            for (String field : fields) {
                if (RuleServiceMap.get(field) != null) {
                    // 干活
                }
            }
        }
        logger.info("hashMapGet校验总耗时: {} ms", tc.stopAndGet());
    }

    @Test
    public void hashMapContainsKeyMatch() {
        ConcurrentHashMap<String, String> RuleServiceMap = new ConcurrentHashMap<>();
        RuleServiceMap.put("user_ip", "1");
        RuleServiceMap.put("app_server_ip", "2");
        RuleServiceMap.put("url", "3");
        RuleServiceMap.put("urlKey", "4");
        RuleServiceMap.put("host", "5");
        RuleServiceMap.put("hostKey", "6");
        List<String> fields = new ArrayList<>();
        fields.add("user_ip");
        fields.add("app_server_ip");
        fields.add("url");
        fields.add("urlKey");
        fields.add("host");
        fields.add("hostKey");
        TimeCostUtil tc = new TimeCostUtil();
        tc.start();
        for (int i = 0; i < 50000 * 1000; i++) {
            for (String field : fields) {
                if (RuleServiceMap.containsKey(field)) {
                    // 干活
                }
            }
        }
        logger.info("hashMapContainsKey校验总耗时: {} ms", tc.stopAndGet());
    }
}