package com.newland.bi.bigdata.utils.string;

import java.util.*;

/**
 * 字符串工具类
 *
 * @author chenqixu
 */
public class StringUtils {
    /**
     * 列分隔符 tab符
     */
    public final static char COLUMN_SPLIT = (char) 0x09;  // \t

    /**
     * 号码处理，去除86开头
     *
     * @param telnumber 手机号码
     * @return
     */
    public static String telnumberProcessing(String telnumber) {
        String result = telnumber;
        if (result != null && result.length() > 2 && result.startsWith("86")) {
            result = result.substring(2);
        }
        return result;
    }

    /**
     * 打印系统环境变量
     */
    public static void printSystemProperties() {
        Properties systemProps = System.getProperties();
        Set<String> keys = systemProps.stringPropertyNames();
        for (String key : keys) {
            System.out.println(key);
        }
    }

    /**
     * 补0
     *
     * @param value 需要补0的字符串
     * @param len   总位数
     * @return
     */
    public static String fillZero(int value, int len) {
        String result = String.valueOf(value);
        if (result != null && result.length() < len) {
            int surplus = len - result.length();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < surplus; i++) {
                sb.append("0");
            }
            sb.append(result);
            return sb.toString();
        }
        return result;
    }

    /**
     * 获取一组补0的顺序数字
     *
     * @param begin
     * @param end
     * @param fillLen
     * @return
     */
    public static List<String> generateSeqList(int begin, int end, int fillLen) {
        List<String> strlist = new ArrayList<>();
        if (begin < end) {
            for (int i = begin; i <= end; i++) {
                strlist.add(fillZero(i, fillLen));
            }
        }
        return strlist;
    }

    public static Map<String, String> listToMap(List<String> list) {
        Map<String, String> map = new HashMap<>();
        for (String tmp : list) {
            map.put(tmp, tmp);
        }
        return map;
    }

    /**
     * 从一个顺序的数字列表中剔除部分内容
     * 算法1
     *
     * @param seqList
     * @param negativeList
     */
    public static void negativeAssert1(List<String> seqList, List<String> negativeList) {
        Iterator<String> it = seqList.iterator();
        int negativeLen = negativeList.size();
        int findLen = 0;
        int findCount = 0;
        while (it.hasNext()) {
            if (findLen == negativeLen) break;
            String value = it.next();
            for (String tmp : negativeList) {
                if (value.equals(tmp)) {
                    findLen++;
                    it.remove();
                    break;
                }
                findCount++;
            }
        }
        System.out.println("findCount：" + findCount);
    }

    /**
     * 从一个顺序的数字列表中剔除部分内容
     * 算法2
     *
     * @param seqList
     * @param negativeList
     */
    public static void negativeAssert2(List<String> seqList, List<String> negativeList) {
        int findCount = 0;
        Map<String, String> map1 = listToMap(seqList);
        findCount += seqList.size();
        Map<String, String> map2 = listToMap(negativeList);
        findCount += negativeList.size();
        for (Map.Entry<String, String> entry : map2.entrySet()) {
            map1.remove(entry.getKey());
            findCount++;
        }
        System.out.println("findCount：" + findCount);
    }

    /**
     * 打印list
     *
     * @param list
     * @param <E>
     */
    public static <E> void printList(List<E> list) {
        if (list != null) {
            for (E tmp : list)
                System.out.println(tmp);
        }
    }

    /**
     * 分割List
     *
     * @param list
     * @param split
     * @return
     */
    public static String splitList(List<String> list, String split) {
        if (list != null && isEmpty(split)) {
            StringBuffer sb = new StringBuffer();
            for (String tmp : list) {
                sb.append(tmp);
                sb.append(split);
            }
            if (sb.length() > 0)
                sb.deleteCharAt(sb.length() - split.length());
            return sb.toString();
        }
        return null;
    }

    /**
     * 是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str != null && str.trim().length() > 0;
    }
}
