package com.cqx.distributed.coordinatio;

import java.util.HashMap;
import java.util.Map;

/**
 * 协作服务本地实现
 *
 * @author chenqixu
 */
public class LocalCoordinatioImpl implements CoordinatioInf {

    private Map<String, String> values = new HashMap<>();

    @Override
    public void init(Map<String, String> params) {
    }

    @Override
    public boolean write(String path, String msg) {
        String result = values.put(path, msg);
        return result != null;
    }

    @Override
    public String read(String path) {
        return values.get(path);
    }

    @Override
    public void close() {
        values.clear();
    }
}
