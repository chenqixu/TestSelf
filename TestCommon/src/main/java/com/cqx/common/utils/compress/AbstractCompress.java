package com.cqx.common.utils.compress;

/**
 * AbstractCompress
 *
 * @author chenqixu
 */
public abstract class AbstractCompress implements ICompress {

    public abstract void compress(String source) throws Exception;

    public void compress(String source, String dst) throws Exception {
    }

    public abstract void uncompress(String source) throws Exception;

    public void uncompress(String source, String dst) throws Exception {
    }
}
