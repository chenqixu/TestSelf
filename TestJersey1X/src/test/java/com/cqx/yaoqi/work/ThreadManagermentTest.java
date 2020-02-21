package com.cqx.yaoqi.work;

import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ThreadManagermentTest {

    @Test
    public void exec() {
        Map<String, String> map = new HashMap<>();
        map.put("a", "1");
        map.put("b", "2");
        map.put("c", "2");
        map.put("d", "2");
        map.put("e", "1");
        Iterator<String> iterator = map.values().iterator();
        while (iterator.hasNext()) {
            String bookWork = iterator.next();
            if (bookWork.equals("2"))
                iterator.remove();
        }
        System.out.println(map);
    }

    @Test
    public void scanLocalFile() {
        System.out.println(new ThreadManagerment().scanLocalFile());
    }
}