package com.cqx.common.bean.model;

import java.text.ParseException;

/**
 * IDataFilterBean
 *
 * @author chenqixu
 */
public interface IDataFilterBean extends Comparable<IDataFilterBean> {
    String getFormatSecond() throws ParseException;

    long getFormatSecond_time() throws ParseException;

    long getCurrent_ts_Micro();
}
