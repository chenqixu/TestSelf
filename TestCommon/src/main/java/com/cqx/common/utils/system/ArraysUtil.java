package com.cqx.common.utils.system;

import java.util.ArrayList;
import java.util.List;

/**
 * 数组工具类
 *
 * @author chenqixu
 */
public class ArraysUtil {

    /**
     * 数组相加，a1，a2变成一个新数组
     *
     * @param a1
     * @param a2
     * @return
     */
    public static String[] arrayCopy(String[] a1, String[] a2) {
        List<String> list = new ArrayList<>();
        for (String s : a1) list.add(s);
        for (String s : a2) list.add(s);
        String[] tmp = new String[list.size()];
        for (int i = 0; i < list.size(); i++) tmp[i] = list.get(i);
        return tmp;
    }

    /**
     * 给数组内容加上前缀，返回一个新数组
     *
     * @param src
     * @param prefix
     */
    public static String[] arrayAddPrefix(String[] src, String prefix) {
        String[] new_array = new String[src.length];
        for (int i = 0; i < src.length; i++) new_array[i] = prefix + src[i];
        return new_array;
    }

    /**
     * 给数组内容加上后缀，返回一个新数组
     *
     * @param src
     * @param suffix
     */
    public static String[] arrayAddSuffix(String[] src, String suffix) {
        String[] new_array = new String[src.length];
        for (int i = 0; i < src.length; i++) new_array[i] = src[i] + suffix;
        return new_array;
    }

    /**
     * 从数组中移除部分数据，返回一个新数组
     *
     * @param src    源
     * @param remove 需要移除的数据
     * @return
     */
    public static String[] arrayRemove(String[] src, String[] remove) {
        List<String> tmpList = new ArrayList<>();
        for (String val : src) tmpList.add(val);
        for (String r : remove) tmpList.remove(r);
        String[] tmp = new String[tmpList.size()];
        for (int i = 0; i < tmpList.size(); i++) tmp[i] = tmpList.get(i);
        return tmp;
    }

    /**
     * 数组转字符，带分隔符
     *
     * @param src   数组
     * @param affix 分隔符
     * @return
     */
    public static String arrayToStr(String[] src, String affix) {
        StringBuilder prepare = new StringBuilder();
        int affix_len = affix.length();
        for (String val : src) {
            prepare.append(val).append(affix);
        }
        if (prepare.length() > 0) prepare.delete(prepare.length() - affix_len, prepare.length());
        return prepare.toString();
    }

    /**
     * List转换成数组
     *
     * @param list
     * @return
     */
    public static String[] listToArray(List<String> list) {
        String[] tmp = new String[list.size()];
        for (int i = 0; i < list.size(); i++) tmp[i] = list.get(i);
        return tmp;
    }
}
