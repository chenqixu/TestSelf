package com.cqx.write;

import java.util.concurrent.Callable;

/**
 * IWatcher
 *
 * @author chenqixu
 */
public interface IWatcher<K> extends Callable<K> {
    void changeRebuilder();
}
