package com.cqx.common.utils.http.auth;

import com.cqx.common.bean.http.HttpType;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import org.apache.http.client.HttpResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.MediaType;

/**
 * ranger工具
 *
 * @author chenqixu
 */
public class RangerUtil {
    private Logger LOG = LoggerFactory.getLogger(RangerUtil.class);
    private String username = "rangeradmin";
    private String password = "Huawei@123";

    public RangerUtil() {
    }

    public RangerUtil(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static RangerUtil getInstance() {
        return new RangerUtil();
    }

    public static RangerUtil getInstance(String username, String password) {
        return new RangerUtil(username, password);
    }

    /**
     * GET请求
     *
     * @param reqUrl 请求地址
     * @return
     */
    public ClientResponse get(String reqUrl) throws HttpResponseException {
        return accept(reqUrl, HttpType.GET, null);
    }

    /**
     * DELETE请求，不带参数
     *
     * @param reqUrl 请求地址
     * @return
     */
    public ClientResponse delete(String reqUrl) throws HttpResponseException {
        return accept(reqUrl, HttpType.DELETE, null);
    }

    /**
     * DELETE请求，带参数
     *
     * @param reqUrl 请求地址
     * @return
     */
    public ClientResponse delete(String reqUrl, Object requestEntity) throws HttpResponseException {
        return accept(reqUrl, HttpType.DELETE, requestEntity);
    }

    /**
     * 下载文件
     *
     * @param reqUrl 请求地址
     * @return
     */
    public ClientResponse download(String reqUrl) throws HttpResponseException {
        return accept(reqUrl, HttpType.DOWNLOAD, null);
    }

    /**
     * GET、PUT、POST、DELETE请求
     *
     * @param reqUrl   请求地址
     * @param httpType 请求类型
     * @return
     */
    public ClientResponse accept(String reqUrl, HttpType httpType, Object requestEntity) throws HttpResponseException {
        LOG.info("accept, reqUrl=" + reqUrl);
        ClientResponse response = null;
        Client client = null;
        try {
            SSLContext sslContext = WebClientDevWrapper.getSSLContext("TLSv1.2");
            assert sslContext != null;
            ClientConfig clientConfig = new DefaultClientConfig();
            clientConfig.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES
                    , new HTTPSProperties(new MyHostnameVerifier(), sslContext));
            client = Client.create(clientConfig);
            client.addFilter(new HTTPBasicAuthFilter(username, password));
            WebResource webResource = client.resource(reqUrl);
            switch (httpType) {
                case GET:// RangerRESTUtils.REST_MIME_TYPE_JSON
                    response = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
                            .get(ClientResponse.class);
                    break;
                case POST:
                    if (requestEntity == null) throw new NullPointerException("POST请求没有传入参数！");
                    response = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
                            .type(MediaType.APPLICATION_JSON_TYPE)
                            .post(ClientResponse.class, requestEntity);
                    break;
                case PUT:
                    if (requestEntity == null) throw new NullPointerException("PUT请求没有传入参数！");
                    response = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
                            .type(MediaType.APPLICATION_JSON_TYPE)
                            .put(ClientResponse.class, requestEntity);
                    break;
                case DELETE:
                    if (requestEntity == null) {
                        response = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
                                .delete(ClientResponse.class);
                    } else {
                        response = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
                                .type(MediaType.APPLICATION_JSON_TYPE)
                                .delete(ClientResponse.class, requestEntity);
                    }
                    break;
                case DOWNLOAD:
                    response = webResource.accept(MediaType.APPLICATION_OCTET_STREAM)
                            .get(ClientResponse.class);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LOG.error("createWebResource is fail, errMessage=" + e.getMessage());
            throw new HttpResponseException(400, e.getMessage());
        } finally {
            if (client != null) {
                client.destroy();
            }
        }
        return response;
    }
}
