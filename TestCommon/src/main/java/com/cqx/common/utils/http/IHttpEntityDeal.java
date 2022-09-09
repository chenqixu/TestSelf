package com.cqx.common.utils.http;

import org.apache.http.HttpEntity;

import java.util.Map;

/**
 * IHttpEntityDeal
 *
 * @author chenqixu
 */
public interface IHttpEntityDeal {

    void deal(Map<String, String> responseHeaderMap, HttpEntity entity);
}
