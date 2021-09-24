package com.cqx.common.utils.list;

import java.util.ArrayList;
import java.util.List;

/**
 * ListHelper
 *
 * @author chenqixu
 */
public class ListHelper<T> {
    private List<T> list = new ArrayList<>();

    public static <T> ListHelper<T> getInstance(Class<T> tClass) {
        return new ListHelper<>();
    }

    public ListHelper<T> add(T t) {
        list.add(t);
        return this;
    }

    public List<T> get() {
        return list;
    }
}
