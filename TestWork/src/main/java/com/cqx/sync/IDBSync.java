package com.cqx.sync;

import java.util.Map;

/**
 * IDBSync
 *
 * @author chenqixu
 */
public interface IDBSync {
    void init(Map<String, Object> params) throws Exception;

    void run() throws Exception;
}
