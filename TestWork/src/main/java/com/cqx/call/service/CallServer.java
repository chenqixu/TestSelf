package com.cqx.call.service;

import com.cqx.call.manage.MonitorMgtImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * CallServer
 *
 * @author chenqixu
 */
public class CallServer {

    private static Map<String, Class> mgtMap = new HashMap<>();

    static {
        mgtMap.put("MonitorMgtImpl", MonitorMgtImpl.class);
    }

    public void call(String mgt, String func, String param) {

    }
}
