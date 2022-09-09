package com.cqx.common.utils.http;

import org.apache.http.HttpEntity;

import java.util.Map;

/**
 * HttpResponseBean
 *
 * @author chenqixu
 */
public class HttpResponseBean {
    private Map<String, String> responseHeaderMap;
    private HttpEntity entity;

    public HttpResponseBean() {
    }

    public HttpResponseBean(Map<String, String> responseHeaderMap, HttpEntity entity) {
        this.responseHeaderMap = responseHeaderMap;
        this.entity = entity;
    }

    public Map<String, String> getResponseHeaderMap() {
        return responseHeaderMap;
    }

    public void setResponseHeaderMap(Map<String, String> responseHeaderMap) {
        this.responseHeaderMap = responseHeaderMap;
    }

    public HttpEntity getEntity() {
        return entity;
    }

    public void setEntity(HttpEntity entity) {
        this.entity = entity;
    }
}
