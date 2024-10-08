package com.cqx.common.utils.string;

import java.util.*;

/**
 * 字符串工具类
 *
 * @author chenqixu
 */
public class StringUtil {
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
     * 补0
     *
     * @param value 需要补0的字符串
     * @param len   总位数
     * @return
     */
    public static String fillZero(long value, int len) {
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
     * 补空格
     *
     * @param value
     * @param len
     * @return
     */
    public static String fillSpace(String value, int len) {
        if (value != null && value.length() < len) {
            int surplus = len - value.length();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < surplus; i++) {
                sb.append(" ");
            }
            sb.append(value);
            return sb.toString();
        }
        return value;
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
        if (list != null && isNotEmpty(split)) {
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
     * 是否不为空
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return str != null && str.trim().length() > 0;
    }

    /**
     * 是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return !isNotEmpty(str);
    }

    public static void println(Object obj) {
        System.out.println(obj);
    }

    public static void println(Object obj, String tag) {
        System.out.println(tag + obj);
    }

    /**
     * 给路径加上"/"
     *
     * @param path
     * @return
     */
    public static String getEndsWithPath(String path) {
        String _tmp = path;
        if (!_tmp.endsWith("/")) {
            _tmp = _tmp + "/";
        }
        return _tmp;
    }

    /**
     * 给文件列表加上路径和后缀
     *
     * @param datalist
     * @param path
     * @param suffix
     * @return
     */
    public static List<String> addPathAndSuffix(List<String> datalist, String path, String suffix) {
        List<String> results = new ArrayList<>();
        for (String str : datalist) {
            results.add(getEndsWithPath(path) + str + suffix);
        }
        return results;
    }

    /**
     * 给文件加上路径和后缀
     *
     * @param data
     * @param path
     * @param suffix
     * @return
     */
    public static String addPathAndSuffix(String data, String path, String suffix) {
        return getEndsWithPath(path) + data + suffix;
    }

    /**
     * 去重
     *
     * @param value
     * @param split
     */
    public static void distinct(String value, String split) {
        if (value != null && split != null) {
            String[] array = value.split(split, -1);
            Map<String, String> map = new HashMap<>();
            for (String str : array) {
                map.put(str, "");
            }
            System.out.println("source.size：" + array.length + "，map.size：" + map.size());
        } else {
            System.out.println("please input param.");
        }
    }

    /**
     * 把byte数组转换成List
     *
     * @param bytes
     * @return
     */
    public static List<Byte> byteArrayToList(byte[] bytes) {
        List<Byte> list = new ArrayList<>();
        for (byte b : bytes) {
            list.add(b);
        }
        return list;
    }

    public static List<String> getID(String content, String startStr) {
        // 替换回车换行为空格
        content = content.replaceAll("\r", " ");
        content = content.replaceAll("\n", " ");
        // 替换=为空格=空格
        content = content.replaceAll("=", " = ");
        // 使用空格做分割
        String[] array = content.split(" ", -1);
        // 去左右空格，判断是否关键字开头
        Set<String> _result = new HashSet<>();
        for (String _content : array) {
            String _t = _content.trim();
            // 使用逗号做分割
            String[] array1 = _t.split(",", -1);
            for (String _content1 : array1) {
                // 可能有带别名引用，比如b.S0001
                String[] bieming_array = _content1.split("\\.", -1);
                String _compare_str = _content1;
                if (bieming_array.length > 1) {
                    _compare_str = bieming_array[1];
                }
                // 判断是否关键字开头
                if (_compare_str.startsWith(startStr)) {
                    _result.add(_compare_str);
                }
            }
        }
        return new ArrayList<>(_result);
    }
}
