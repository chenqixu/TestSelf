package com.cqx.common.model.filter;

import com.cqx.common.bean.model.IDataFilterBean;

import java.util.List;

/**
 * IDataFilterCall
 *
 * @author chenqixu
 */
public interface IDataFilterCall<T extends IDataFilterBean> {

    void call(List<T> dataBeans);
}
