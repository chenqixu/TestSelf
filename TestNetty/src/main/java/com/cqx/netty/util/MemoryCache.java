package com.cqx.netty.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存
 *
 * @author chenqixu
 */
public class MemoryCache {

    private Map<String, String> cacheold;
    private Map<String, String> cachenew;
    private String changeTag = "old";

    public void init() {
        cacheold = new HashMap<>();
        cachenew = new HashMap<>();
        cacheold.put("004403000003101016127868F700D901", "ZXV10");
        cachenew.put("004403000003101016127868F700D901", "ZXV10");
    }

    public Map<String, String> getCache() {
        if (changeTag.equals("new"))
            return cachenew;
        else
            return cacheold;
    }
}
