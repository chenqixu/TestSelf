package com.cqx.jerseyclient;

import com.cqx.bean.ResultBean;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.sun.jersey.multipart.impl.MultiPartWriter;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClientFactory {
    private static ClientFactory ac = new ClientFactory();
    private Client client;
    private WebResource webResource;
    private ClientResponse response;
    private List<File> fileList;

    private ClientFactory() {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getClasses().add(MultiPartWriter.class);
        client = Client.create(clientConfig);
    }

    public static ClientFactory getInstance() {
        return ac != null ? ac : new ClientFactory();
    }

    public <T> ResultBean<T> callGetBean(String url, String MediaType, Class<T> tClass) {
        ResultBean<T> result = null;
        if (StringUtils.isNotBlank(url)) {
            webResource = client.resource(url);
            if (StringUtils.isNotBlank(MediaType))
                response = webResource.accept(MediaType).get(ClientResponse.class);
            else
                response = webResource.get(ClientResponse.class);
            result = new ResultBean<T>();
            result.setStatus(response.getStatus());
            result.setT(response.getEntity(tClass));
        }
        return result;
    }

    public ResultBean<String> callGetBean(String url, String MediaType) {
        return callGetBean(url, MediaType, String.class);
    }

    public ResultBean<String> callGetBean(String url) {
        return callGetBean(url, null, String.class);
    }

    /**
     * 调用
     *
     * @param url
     * @param MediaType 参考javax.ws.rs.core.MediaType
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T get(String url, String MediaType, Class<T> tClass) {
        T result = null;
        if (StringUtils.isNotBlank(url)) {
            webResource = client.resource(url);
            if (StringUtils.isNotBlank(MediaType))
                response = webResource.accept(MediaType).get(ClientResponse.class);
            else
                response = webResource.get(ClientResponse.class);
            result = response.getEntity(tClass);
        }
        return result;
    }

    public String get(String url, String MediaType) {
        return get(url, MediaType, String.class);
    }

    public String get(String url) {
        return get(url, null, String.class);
    }

    public <T> T get(String url, Class<T> tClass) {
        return get(url, null, tClass);
    }

    public void postJSON(String url, String requestEntity) {
        post(url, MediaType.APPLICATION_JSON, requestEntity, null);
    }

    public void post(String url, String MediaType, Object requestEntity) {
        post(url, MediaType, requestEntity, null);
    }

    public <T> T post(String url, String MediaType, Object requestEntity, Class<T> tClass) {
        T result = null;
        if (StringUtils.isNotBlank(url)) {
            webResource = client.resource(url);
            if (StringUtils.isNotBlank(MediaType) && requestEntity != null) {
                response = webResource.type(MediaType).post(ClientResponse.class, requestEntity);
                if (tClass != null) result = response.getEntity(tClass);
            }
        }
        return result;
    }

    public <T> T postFile(String url, Class<T> tClass) {
        FormDataMultiPart requestEntity = new FormDataMultiPart();
        if (fileList == null || fileList.size() == 0) return null;
        for (File file : fileList) {
            requestEntity.bodyPart(new FileDataBodyPart("file", file));
        }
        client.setConnectTimeout(3000);//连接3秒超时
        client.setReadTimeout(3000);//读取3秒超时
        if (StringUtils.isNotBlank(url)) {
            return post(url, MediaType.MULTIPART_FORM_DATA, requestEntity, tClass);
        }
        return null;
    }

    public ClientFactory buildFile() {
        fileList = new ArrayList<>();
        return this;
    }

    public ClientFactory addFile(File file) {
        fileList.add(file);
        return this;
    }
}
