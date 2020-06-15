package com.cqx.common.utils.file;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * FileCount
 *
 * @author chenqixu
 */
public abstract class FileCount implements IFileRead {

    private Map<String, Long> count = new HashMap<>();

    public void count(String name) {
        Long cnt = count.get(name);
        if (cnt == null) {
            cnt = setCount(name);
        }
        cnt++;
        count.put(name, cnt);
    }

    public long getCount(String name) {
        return count.get(name);
    }

    private long setCount(String name) {
        long cnt = 0L;
        count.put(name, cnt);
        return cnt;
    }

    public void tearDown() throws IOException {}
}
