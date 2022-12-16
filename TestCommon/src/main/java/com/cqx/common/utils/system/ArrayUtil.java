package com.cqx.common.utils.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 数组工具类
 *
 * @author chenqixu
 */
public class ArrayUtil {
    public static final String ISMISSING = "_ismissing";
    public static final String ISMISSING_TYPE = "java.lang.Boolean";

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
     * 数组相加，a1，a2变成一个新数组
     *
     * @param a1
     * @param a2
     * @return
     */
    public static byte[] arrayCopy(byte[] a1, byte[] a2) {
        List<Byte> list = new ArrayList<>();
        for (byte s : a1) list.add(s);
        for (byte s : a2) list.add(s);
        byte[] tmp = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) tmp[i] = list.get(i);
        return tmp;
    }

    /**
     * 数组b1和数组b2相拼接
     *
     * @param b1
     * @param b2
     * @param b2Len
     * @return
     */
    public static byte[] arrayAdd(byte[] b1, byte[] b2, int b2Len) {
        byte[] n1 = new byte[b1.length + b2Len];
        System.arraycopy(b1, 0, n1, 0, b1.length);
        System.arraycopy(b2, 0, n1, b1.length, b2Len);
        return n1;
    }

    /**
     * 数组翻转
     *
     * @param bytes
     * @return
     */
    public static byte[] arrayFlip(byte[] bytes) {
        List<Byte> byteList = new ArrayList<>();
        for (byte bs : bytes) {
            byteList.add(bs);
        }
        Collections.reverse(byteList);
        int len = byteList.size();
        byte[] buf = new byte[len];
        for (int j = 0; j < len; j++) {
            buf[j] = byteList.get(j);
        }
        return buf;
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
     * 数组转字符，带分隔符
     *
     * @param arrays 数组
     * @param affix  分隔符
     * @param <T>    泛型
     * @return
     */
    public static <T> String arrayToStr(T[] arrays, String affix) {
        StringBuilder prepare = new StringBuilder();
        int affix_len = affix.length();
        for (T val : arrays) {
            prepare.append(val.toString()).append(affix);
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

    /**
     * Collection转字符串，拼接splitStr
     *
     * @param collection
     * @param splitStr
     * @return
     */
    public static String collectionToStr(Collection<String> collection, char splitStr) {
        StringBuilder send_fields = new StringBuilder();
        for (String dstTableField : collection) {
            send_fields.append(dstTableField).append(splitStr);
        }
        if (send_fields.length() > 0) {
            send_fields.deleteCharAt(send_fields.length() - 1);
        }
        return send_fields.toString();
    }

    /**
     * 在数组增加 字段_ismissing
     *
     * @param array
     * @return
     */
    public static String[] addIsmissing(String[] array) {
        List<String> tmpList = new ArrayList<>();
        for (String tmp : array) {
            tmpList.add(tmp + ISMISSING);
            tmpList.add(tmp);
        }
        String[] tmp_array = new String[tmpList.size()];
        for (int i = 0; i < tmpList.size(); i++) {
            tmp_array[i] = tmpList.get(i);
        }
        return tmp_array;
    }

    /**
     * 在数组中增加 _ismissing的类型java.lang.Boolean
     *
     * @param array
     * @return
     */
    public static String[] addIsmissingType(String[] array) {
        List<String> tmpList = new ArrayList<>();
        for (String tmp : array) {
            tmpList.add(ISMISSING_TYPE);
            tmpList.add(tmp);
        }
        String[] tmp_array = new String[tmpList.size()];
        for (int i = 0; i < tmpList.size(); i++) {
            tmp_array[i] = tmpList.get(i);
        }
        return tmp_array;
    }
}
