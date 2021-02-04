package com.cqx.common.model.stream;

import java.util.Map;

/**
 * IStreamBolt
 *
 * @author chenqixu
 */
public abstract class IStreamBolt<T> {

    public abstract void prepare(Map params);

    public abstract void execute(T t);

    public void clean() {
    }
}
