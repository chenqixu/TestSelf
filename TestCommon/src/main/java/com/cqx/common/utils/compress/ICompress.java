package com.cqx.common.utils.compress;

/**
 * ICompress
 *
 * @author chenqixu
 */
public interface ICompress {

    void compress(String source) throws Exception;

    void compress(String source, String dst) throws Exception;

    void uncompress(String source) throws Exception;

    void uncompress(String source, String dst) throws Exception;
}
