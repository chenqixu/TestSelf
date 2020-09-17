package com.cqx.distributed.coordinatio;

import java.util.Map;

/**
 * 协作服务
 *
 * @author chenqixu
 */
public interface CoordinatioInf {
    void init(Map<String, String> params);

    boolean write(String path, String msg);

    String read(String path);

    void close();
}
