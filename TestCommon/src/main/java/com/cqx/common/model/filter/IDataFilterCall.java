package com.cqx.common.model.filter;

import java.util.List;

/**
 * IDataFilterCall
 *
 * @author chenqixu
 */
public interface IDataFilterCall<T> {

    void call(List<T> dataBeans);
}
