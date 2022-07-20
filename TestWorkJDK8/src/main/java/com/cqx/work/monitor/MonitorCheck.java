package com.cqx.work.monitor;

import com.cqx.common.utils.Utils;
import com.cqx.common.utils.net.IpUtil;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.util.IPAddressUtil;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 监控校验
 *
 * @author chenqixu
 */
public class MonitorCheck {
    private static final Logger logger = LoggerFactory.getLogger(MonitorCheck.class);
    private IPRule ipRule = new IPRule();
    private IPRuleV1 ipRuleV1 = new IPRuleV1();

    public void equalsMatch(String value, List<String> rules) {
//        TimeCostUtil tc = new TimeCostUtil();
//        tc.start();
        boolean found = false;
        for (String rule : rules) {
            found = value.equals(rule);
            if (found) break;
        }
//        logger.info("contains耗时: {}, 结果: {}", tc.stopAndGet(), found);
    }

    public void containsMatch(String value, List<String> rules) {
//        TimeCostUtil tc = new TimeCostUtil();
//        tc.start();
        boolean found = false;
        for (String rule : rules) {
            found = value.contains(rule);
            if (found) break;
        }
//        logger.info("contains耗时: {}, 结果: {}", tc.stopAndGet(), found);
    }

    public void indexOfMatch(String value, List<String> rules) {
//        TimeCostUtil tc = new TimeCostUtil();
//        tc.start();
        boolean found = false;
        for (String rule : rules) {
            found = value.indexOf(rule) > -1;
            if (found) break;
        }
//        logger.info("indexOf耗时: {}, 结果: {}", tc.stopAndGet(), found);
    }

    // todo: 匹配不到，规则：(?=.*rule1)(?=.*rule2)^.*$
    public void regexpMatch(String value, List<String> rules) {
//        TimeCostUtil tc = new TimeCostUtil();
//        tc.start();
        StringBuilder regexp = new StringBuilder();
        for (String rule : rules) {
            regexp.append("(?=.*").append(rule).append(")");
        }
        if (regexp.length() > 0) {
            regexp.append("^.*$");
        }
        Pattern pattern = Pattern.compile(regexp.toString());
        boolean found = pattern.matcher(value).find();
//        logger.info("正则耗时: {}, 结果: {}", tc.stopAndGet(), found);
    }

    public void listStreamMatch(List<String> values, List<String> rules) {
//        TimeCostUtil tc = new TimeCostUtil();
//        tc.start();
        boolean found = rules.stream().anyMatch(values::contains);
//        logger.info("流API耗时: {}, 结果: {}", tc.stopAndGet(), found);
    }

    public void ahocorasickMatch(String value, List<String> rules) {
//        TimeCostUtil tc = new TimeCostUtil();
//        tc.start();
        Trie trie = Trie.builder().onlyWholeWords().addKeywords(rules).build();
        Collection<Emit> emits = trie.parseText(value);
//        emits.forEach(System.out::println);
        boolean found = emits.size() > 0;
//        for (String rule : rules) {
//            found = Arrays.toString(emits.toArray()).contains(rule);
//            if (found) break;
//        }
//        logger.info("ac算法耗时: {}, 结果: {}", tc.stopAndGet(), found);
    }

    public void ipMatch(Map<String, String> userLogMap, UserRuleBean userRuleBean) {
        boolean found = ipRule.ruleHandle(userLogMap, userRuleBean);
//        logger.info("IP校验结果: {}", found);
    }

    public void ipv1Match(Map<String, String> userLogMap, UserRuleBean userRuleBean) {
        boolean found = ipRuleV1.ruleHandle(userLogMap, userRuleBean);
//        logger.info("IP校验结果: {}", found);
    }

    /**
     * 根据规则时间和用户日志时间做时间校验<br>
     * 时间检验(用户访问时间在监测开始和结束时间之间 _过滤)  14位时间字符串比较
     *
     * @param userLogMap
     * @param userRuleBean
     * @return
     * @throws ParseException
     */
    public boolean checkUserLogTime(Map<String, String> userLogMap, UserRuleBean userRuleBean) throws ParseException {
        String accessTime = userLogMap.get("start_time");
        if (StringUtils.isNotBlank(accessTime) && accessTime.length() == 16) { //5G/4G用户面是微秒时间戳
            long startTime = Long.parseLong(accessTime);
            String formatUserTime = Utils.formatTime(startTime / 1000, "yyyyMMddHHmmss");
            int startTimeCheck = userRuleBean.getStartTime().compareTo(formatUserTime);
            int endTimeCheck = userRuleBean.getEndTime().compareTo(formatUserTime);
            return startTimeCheck <= 0 && endTimeCheck >= 0;

        } else if (StringUtils.isNotBlank(accessTime) && accessTime.length() == 29) {//wlan合成
            long time = Utils.formatTime(accessTime, "yyyy-MM-dd HH:mm:ss");
            String formatUserTime = Utils.formatTime(time, "yyyyMMddHHmmss");
            int startTimeCheck = userRuleBean.getStartTime().compareTo(formatUserTime);
            int endTimeCheck = userRuleBean.getEndTime().compareTo(formatUserTime);
            return startTimeCheck <= 0 && endTimeCheck >= 0;
        }
        return false;
    }

    class IPRule {
        public boolean ruleHandle(Map<String, String> userLogMap, UserRuleBean userRuleBean) {
            String destIp = userRuleBean.getDestIp();
            String server_pub_ip = userLogMap.get("server_pub_ip");//日志目的服务器IP

            boolean isIPv4 = IPAddressUtil.isIPv4LiteralAddress(destIp);
            boolean isIPv6 = IPAddressUtil.isIPv6LiteralAddress(destIp);

            if (StringUtils.isNotBlank(server_pub_ip)) {
                boolean userIPv4 = IPAddressUtil.isIPv4LiteralAddress(server_pub_ip);
                boolean userIPv6 = IPAddressUtil.isIPv6LiteralAddress(server_pub_ip);

                if ((userIPv4 && isIPv4) || (userIPv6 && isIPv6)) {
                    return matchDestIp(userRuleBean, server_pub_ip);
                }
            } else {
                if (isIPv4) {
                    return IpV4Match(userLogMap, userRuleBean);
                } else if (isIPv6) {
                    return IpV6Match(userLogMap, userRuleBean);
                }
            }
            return false;
        }

        public boolean matchDestIp(UserRuleBean userRuleBean, String destIp) {
            try {
                if (StringUtils.isNotBlank(destIp) && StringUtils.isNotBlank(userRuleBean.getDestIpMask())) {
                    boolean inRange = IpUtil.isInRange(destIp, userRuleBean.getDestIp(), Integer.parseInt(userRuleBean.getDestIpMask()));
                    if (inRange) {
                        return true;
                    }
                } else if (StringUtils.isNotBlank(destIp)) {
                    boolean inRange = StringUtils.equals(destIp, userRuleBean.getDestIp());
                    if (inRange) {
                        return true;
                    }
                }
            } catch (Exception e) {
                logger.error("IP规则匹配异常", e);
            }
            return false;
        }

        public boolean IpV4Match(Map<String, String> userLogMap, UserRuleBean userRuleBean) {
            String App_Server_IP_IPv4_text = userLogMap.get("app_server_ip_ipv4_text");
            return matchDestIp(userRuleBean, App_Server_IP_IPv4_text);
        }

        public boolean IpV6Match(Map<String, String> userLogMap, UserRuleBean userRuleBean) {
            String App_Server_IP_IPv6_text = userLogMap.get("app_server_ip_ipv6_text");
            return matchDestIp(userRuleBean, App_Server_IP_IPv6_text);
        }
    }

    class IPRuleV1 {
        public boolean ruleHandle(Map<String, String> userLogMap, UserRuleBean userRuleBean) {
            boolean isIPv4 = userRuleBean.isDestIpIsIPv4();
            boolean isIPv6 = userRuleBean.isDestIpIsIPv6();

            // 日志目的服务器IP
            String server_pub_ip = userLogMap.get("server_pub_ip");
            // 获取不到日志目的服务器IP，就获取app_server_ip_ipv4_text或者app_server_ip_ipv6_text
            if (StringUtils.isBlank(server_pub_ip)) {
                if (isIPv4) {
                    server_pub_ip = userLogMap.get("app_server_ip_ipv4_text");
                } else if (isIPv6) {
                    server_pub_ip = userLogMap.get("app_server_ip_ipv6_text");
                }
            }

            if (StringUtils.isNotBlank(server_pub_ip)) {
                boolean userIPv4 = IPAddressUtil.isIPv4LiteralAddress(server_pub_ip);
                boolean userIPv6 = false;
                if(!userIPv4) userIPv6 = IPAddressUtil.isIPv6LiteralAddress(server_pub_ip);

                if (userIPv4 && isIPv4) {
                    return IpV4Match(userRuleBean, server_pub_ip);
                } else if (userIPv6 && isIPv6) {
                    return IpV6Match(userRuleBean, server_pub_ip);
                }
            }
            return false;
        }

        public boolean IpV4Match(UserRuleBean userRuleBean, String destIp) {
            try {
                if (StringUtils.isNotBlank(destIp) && StringUtils.isNotBlank(userRuleBean.getDestIpMask())) {
                    return IpUtil.isIPv4InRange(destIp, userRuleBean.getDestIp(), Integer.parseInt(userRuleBean.getDestIpMask()));
                } else if (StringUtils.isNotBlank(destIp)) {
                    return StringUtils.equals(destIp, userRuleBean.getDestIp());
                }
            } catch (Exception e) {
                logger.error("IP规则匹配异常", e);
            }
            return false;
        }

        public boolean IpV6Match(UserRuleBean userRuleBean, String destIp) {
            try {
                if (StringUtils.isNotBlank(destIp) && StringUtils.isNotBlank(userRuleBean.getDestIpMask())) {
                    return IpUtil.isIPv6InRange(destIp, userRuleBean.getDestIp(), Integer.parseInt(userRuleBean.getDestIpMask()));
                } else if (StringUtils.isNotBlank(destIp)) {
                    return StringUtils.equals(destIp, userRuleBean.getDestIp());
                }
            } catch (Exception e) {
                logger.error("IP规则匹配异常", e);
            }
            return false;
        }
    }
}
