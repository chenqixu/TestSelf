package com.newland.bi.bigdata.string;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * RpmUtil
 *
 * @author chenqixu
 */
public class RpmUtil {
    private static final Logger logger = LoggerFactory.getLogger(RpmUtil.class);
    private static final String nums = "0123456789.";
    private static final char[] chars = nums.toCharArray();
    private List<String> rpms = new ArrayList<>();

    public void addRpms(String rpm_names, String regex) {
        String[] rpm_name_array = rpm_names.split(regex, -1);
        rpms = Arrays.asList(rpm_name_array);
        logger.info("rpms.size : {}", rpms.size());
    }

    public void addRpm(String rpm_name) {
        rpms.add(rpm_name);
    }

    public void deal() {
        for (String rpm : rpms) {
            StringBuilder sb = new StringBuilder();
            String[] values = rpm.split("-", -1);
            for (int i = 0; i < values.length; i++) {
                if (i == 0 && isNum(values[i].charAt(0))) {
                    continue;
                } else if (i > 0 && isNum(values[i].charAt(0))) {
                    if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
                    break;
                } else {
                    sb.append(values[i]);
                    sb.append("-");
                }
            }
//            logger.info("rpm -qa|grep {}", sb.toString());
            System.out.println(String.format("echo \"===[start]%s\" : `rpm -qa|grep %s|wc -l`", sb.toString(), sb.toString()));
        }
    }

    private boolean isNum(char str) {
        for (char c : chars) {
            if (str == c) return true;
        }
        return false;
    }
}
