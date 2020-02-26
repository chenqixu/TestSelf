package com.newland.bi.bigdata.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * ListUtils
 *
 * @author chenqixu
 */
public class ListUtils {

    public static  <T> List<T> addAndGet(T t) {
        List<T> list = new ArrayList<>();
        list.add(t);
        return list;
    }
}
