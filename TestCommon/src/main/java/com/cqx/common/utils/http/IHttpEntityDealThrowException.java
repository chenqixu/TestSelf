package com.cqx.common.utils.http;

import org.apache.http.HttpEntity;

import java.io.IOException;
import java.util.Map;

/**
 * IHttpEntityDealThrowException
 *
 * @author chenqixu
 */
public interface IHttpEntityDealThrowException {

    void deal(Map<String, String> responseHeaderMap, HttpEntity entity) throws IOException;
}
