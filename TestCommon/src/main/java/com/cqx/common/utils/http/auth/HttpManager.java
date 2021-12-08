package com.cqx.common.utils.http.auth;

import com.cqx.common.bean.http.APIErrorResponse;
import com.cqx.common.bean.http.RESTResponse;
import com.cqx.common.bean.http.ResponseMessage;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.serialize.gson.ParameterizedTypeImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.ClientResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * HttpManager
 *
 * @author huawei
 * @version [V100R002C30, 2014-09-09]
 * @since [OM 1.0]
 */
public class HttpManager {
    private Logger LOG = LoggerFactory.getLogger(HttpManager.class);
    private Gson gson = new GsonBuilder().create();

    /**
     * sendHttpGetRequest
     *
     * @param httpClient    HttpClient
     * @param operationUrl  String
     * @param operationName String
     * @return 结果
     */
    public String sendHttpGetRequest(HttpClient httpClient, String operationUrl, String operationName) {
        HttpResponse httpResponse = sendHttpGetRequestGetHttpResponse(httpClient
                , operationUrl, operationName);
        // 处理httpGet响应
        String responseLineContent = handleHttpResponse(httpResponse, operationName);
        LOG.info("SendHttpGetRequest completely.");
        return responseLineContent;
    }

    /**
     * sendHttpGetRequestGetHttpResponse
     *
     * @param httpClient    HttpClient
     * @param operationUrl  String
     * @param operationName String
     * @return HttpResponse
     */
    public HttpResponse sendHttpGetRequestGetHttpResponse(HttpClient httpClient
            , String operationUrl, String operationName) {
        // 校验
        check(operationUrl, operationName);

        try {
            HttpGet httpGet = new HttpGet(operationUrl);
            httpGet.addHeader("Content-Type", "application/json;charset=UTF-8");

            return httpClient.execute(httpGet);
        } catch (HttpResponseException e) {
            LOG.error("HttpResponseException.", e);
        } catch (ClientProtocolException e) {
            LOG.error("ClientProtocolException.", e);
        } catch (IOException e) {
            LOG.error("IOException.", e);
        }

        return null;
    }

    /**
     * sendHttpPostRequestWithString
     *
     * @param httpClient    HttpClient
     * @param operationUrl  String
     * @param jsonString    String
     * @param operationName String
     * @return 结果
     */
    public String sendHttpPostRequestWithString(HttpClient httpClient, String operationUrl
            , String jsonString, String operationName) {
        HttpResponse httpResponse = sendHttpPostRequestWithStringGetHttpResponse(httpClient
                , operationUrl, jsonString, operationName);
        // 处理httpGet响应
        String responseLineContent = handleHttpResponse(httpResponse, operationName);
        LOG.info("SendHttpPostRequest completely.");
        return responseLineContent;
    }

    /**
     * sendHttpPostRequestWithString
     *
     * @param httpClient    HttpClient
     * @param operationUrl  String
     * @param jsonString    String
     * @param operationName String
     * @return HttpResponse
     */
    public HttpResponse sendHttpPostRequestWithStringGetHttpResponse(HttpClient httpClient
            , String operationUrl, String jsonString, String operationName) {
        // 校验
        check(operationUrl, operationName);

        try {
            HttpPost httpPost = new HttpPost(operationUrl);
            httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
            if (StringUtils.isNotEmpty(jsonString)) {
                httpPost.setEntity(new StringEntity(jsonString, "UTF-8"));
            }

            return httpClient.execute(httpPost);
        } catch (UnsupportedEncodingException e1) {
            LOG.error("UnsupportedEncodingException", e1);
        } catch (ClientProtocolException e1) {
            LOG.error("ClientProtocolException", e1);
        } catch (IOException e) {
            LOG.error("IOException", e);
        }

        return null;
    }

    /**
     * sendHttpPutRequestWithString
     *
     * @param httpclient    HttpClient
     * @param operationUrl  String
     * @param jsonString    String
     * @param operationName String
     * @return String
     */
    public String sendHttpPutRequestWithString(HttpClient httpclient
            , String operationUrl, String jsonString, String operationName) {
        HttpResponse httpResponse = sendHttpPutRequestWithStringGetHttpResponse(httpclient
                , operationUrl, jsonString, operationName);
        // 处理httpGet响应
        String responseLineContent = handleHttpResponse(httpResponse, operationName);
        LOG.info("sendHttpPutRequest completely.");
        return responseLineContent;
    }

    /**
     * sendHttpPutRequestWithStringGetHttpResponse
     *
     * @param httpclient    HttpClient
     * @param operationUrl  String
     * @param jsonString    String
     * @param operationName String
     * @return HttpResponse
     */
    public HttpResponse sendHttpPutRequestWithStringGetHttpResponse(HttpClient httpclient
            , String operationUrl, String jsonString, String operationName) {
        // 校验
        check(operationUrl, operationName);

        try {
            HttpPut httpPut = new HttpPut(operationUrl);
            httpPut.addHeader("Content-Type", "application/json;charset=UTF-8");
            if (StringUtils.isNotEmpty(jsonString)) {
                httpPut.setEntity(new StringEntity(jsonString, "UTF-8"));
            }

            return httpclient.execute(httpPut);
        } catch (UnsupportedEncodingException e1) {
            LOG.error("UnsupportedEncodingException", e1);
        } catch (ClientProtocolException e1) {
            LOG.error("ClientProtocolException", e1);
        } catch (IOException e) {
            LOG.error("IOException", e);
        }

        return null;
    }

    /**
     * sendHttpDeleteRequest
     *
     * @param httpClient    HttpClient
     * @param operationUrl  String
     * @param jsonString    String
     * @param operationName String
     */
    public String sendHttpDeleteRequest(HttpClient httpClient, String operationUrl
            , String jsonString, String operationName) {
        HttpResponse httpResponse = sendHttpDeleteRequestGetHttpResponse(httpClient
                , operationUrl, jsonString, operationName);
        // 处理httpGet响应
        String responseLineContent = handleHttpResponse(httpResponse, operationName);
        LOG.info(String.format("sendHttpDeleteMessage for %s completely.", operationName));
        return responseLineContent;
    }

    /**
     * sendHttpDeleteRequestGetHttpResponse
     *
     * @param httpClient    HttpClient
     * @param operationUrl  String
     * @param jsonString    String
     * @param operationName String
     * @return HttpResponse
     */
    public HttpResponse sendHttpDeleteRequestGetHttpResponse(HttpClient httpClient
            , String operationUrl, String jsonString, String operationName) {
        // 校验
        check(operationUrl, operationName);

        try {
            HttpResponse httpResponse = null;

            if (StringUtils.isEmpty(jsonString)) {
                HttpDelete httpDelete = new HttpDelete(operationUrl);
                httpResponse = httpClient.execute(httpDelete);
            } else {
                MyHttpDelete myHttpDelete = new MyHttpDelete(operationUrl);
                myHttpDelete.addHeader("Content-Type", "application/json;charset=UTF-8");
                myHttpDelete.setEntity(new StringEntity(jsonString, "UTF-8"));
                httpResponse = httpClient.execute(myHttpDelete);
            }
            return httpResponse;
        } catch (ClientProtocolException e1) {
            LOG.error("ClientProtocolException", e1);
        } catch (IOException e) {
            LOG.error("IOException", e);
        } catch (Exception e) {
            LOG.error("Exception", e);
        }

        return null;
    }

    /**
     * 获取返回信息
     *
     * @param httpResponse  http返回
     * @param operationName 操作名称
     * @return 返回信息
     */
    public String handleHttpResponse(HttpResponse httpResponse, String operationName) {
        String lineContent = "";
        if (httpResponse == null) {
            LOG.error("The httpResponse is empty.");
            throw new NullPointerException("The httpResponse is empty.");
        }
        if ((operationName == null) || (operationName.isEmpty())) {
            LOG.error("The operationName is empty.");
            operationName = "UserOperation";
        }
        BufferedReader bufferedReader = null;
        InputStream inputStream = null;
        try {
            LOG.info(String.format("The %s status is %s.", operationName, httpResponse.getStatusLine()));
            // 正常模式
            // application/json;charset=UTF-8
            // 下载模式
            // application/x-download;charset=UTF-8
            // 文件名
            // attachment;filename="yz_newland_1632625100790_keytab.tar"
            String contentType = null;
            String fileName = null;
            for (Header header : httpResponse.getAllHeaders()) {
                if (header.getName().equals("Content-Type")) {
                    contentType = header.getValue().split(";")[0];
                } else if (header.getName().equals("Content-Disposition")) {
                    fileName = header.getValue().split(";")[1].split("=")[1].replace("\"", "");
                }
            }
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null && entity.getContent() != null) {
                // 内容
                inputStream = entity.getContent();
                // 下载
                if (contentType != null && contentType.contains("x-download")) {
                    // get file from input stream
                    String filePath = "d:\\tmp\\chm\\";
                    FileOutputStream fileOutputStream = new FileOutputStream(filePath + fileName);
                    FileUtil.copyBytes(inputStream, fileOutputStream);
                    lineContent = "Download file is " + filePath + fileName;
                    LOG.info(String.format("Download file is %s.", filePath + fileName));
                } else { // 非下载
                    // get content from input stream
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    lineContent = bufferedReader.readLine();
                    LOG.info(String.format("The response lineContent is %s.", lineContent));
                }
            }
        } catch (IOException e) {
            LOG.warn("ReadLine failed.");
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    LOG.info("Close bufferedReader failed.");
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOG.info("Close inputStream failed.");
                }
            }
        }
        return lineContent;
    }

    /**
     * 校验
     *
     * @param operationUrl  地址
     * @param operationName 操作名称
     */
    private void check(String operationUrl, String operationName) {
        LOG.info(String.format("Enter userOperation %s.", operationName));
        if ((operationUrl == null) || (operationUrl.isEmpty())) {
            LOG.error("The operationUrl is empty.");
            throw new NullPointerException("The operationUrl is empty.");
        }
        LOG.info(String.format("The operationUrl is:%s", operationUrl));
    }

    /**
     * 简单解析Http返回
     *
     * @param httpResponse
     * @return
     * @throws IOException
     */
    public ResponseMessage<String> handleHttpResponse(HttpResponse httpResponse) throws IOException {
        ResponseMessage<String> responseMessage = new ResponseMessage<>();
        InputStream is = null;
        try {
            is = httpResponse.getEntity().getContent();
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            String result = new String(IOUtils.toByteArray(is), StandardCharsets.UTF_8);
            LOG.info(String.format("Status code : %s", statusCode));
            LOG.info(String.format("结果 : %s", result));
            responseMessage.setStatus(statusCode);
            responseMessage.setBody(result);
            switch (statusCode) {
                case 200:// 成功，OK/正常
                    responseMessage.setSuccess(true);
                    break;
                default:
                    break;
            }
        } finally {
            if (is != null) is.close();
        }
        return responseMessage;
    }

    /**
     * HTTP 204，空返回
     *
     * @param response 客户端返回对象
     * @throws HttpResponseException
     */
    public ResponseMessage handleClientNoResponse(ClientResponse response) throws HttpResponseException {
        return handleClientResponse(response, String.class);
    }

    /**
     * application/json，json解析
     *
     * @param response 客户端返回对象
     * @param tClass   结果类
     * @param <T>      结果类
     * @param isList   是否List
     * @return
     * @throws HttpResponseException
     */
    public <T> ResponseMessage<T> handleClientResponseWithJson(ClientResponse response
            , Class<T> tClass, boolean isList, Gson customGson) throws HttpResponseException {
        ResponseMessage<T> responseMessage = new ResponseMessage<>();
        ResponseMessage<String> jsonResponseMessage = handleClientResponse(response, String.class);
        if (jsonResponseMessage.isSuccess() && jsonResponseMessage.getBody() != null) {
            responseMessage.setStatus(jsonResponseMessage.getStatus());
            responseMessage.setSuccess(jsonResponseMessage.isSuccess());
            if (isList) {
                Type listType = new ParameterizedTypeImpl(List.class, new Class[]{tClass});
                if (customGson != null) {
                    responseMessage.setBodyList((List<T>) customGson.fromJson(jsonResponseMessage.getBody(), listType));
                } else {
                    responseMessage.setBodyList((List<T>) gson.fromJson(jsonResponseMessage.getBody(), listType));
                }
            } else {
                if (customGson != null) {
                    responseMessage.setBody(customGson.fromJson(jsonResponseMessage.getBody(), tClass));
                } else {
                    responseMessage.setBody(gson.fromJson(jsonResponseMessage.getBody(), tClass));
                }
            }
        }
        return responseMessage;
    }

    public <T> ResponseMessage<T> handleClientResponseWithJson(ClientResponse response
            , Class<T> tClass, boolean isList) throws HttpResponseException {
        return handleClientResponseWithJson(response, tClass, isList, null);
    }

    /**
     * 解析返回值
     * <pre>
     *     ==HTTP状态码==
     *     100-199 用于指定客户端应相应的某些动作
     *     200-299 用于表示请求成功
     *     300-399 用于已经移动的文件并且常被包含在定位头信息中指定新的地址信息
     *     400-499 用于指出客户端的错误
     *     500-599 用于支持服务器错误
     * </pre>
     *
     * @param response 客户端返回对象
     * @param tClass   结果类
     * @param <T>      结果类
     * @return 结果
     * @throws HttpResponseException
     */
    public <T> ResponseMessage<T> handleClientResponse(ClientResponse response, Class<T> tClass) throws HttpResponseException {
        ResponseMessage<T> responseMessage = new ResponseMessage<>();
        T body = null;
        try {
            if (response != null) {
                int status = response.getStatus();
                responseMessage.setStatus(status);
                switch (status) {
                    case 200:// 成功，OK/正常
                    case 201:// 成功，Created/已创建
                        responseMessage.setSuccess(true);
                        body = response.getEntity(tClass);
                        LOG.info(String.format("request is success , the status=%s，the resp=%s"
                                , status, (body != null ? body.toString() : "null")));
                        break;
                    case 204:// 成功，No Content/无内容
                        responseMessage.setSuccess(true);
                        LOG.info(String.format("request is success , the status=%s，no resp", status));
                        break;
                    case 400:// 失败，请求错误，参数无效
                    case 404:// 失败，找不到资源
                    case 500:// 失败，内部服务器错误
                        String responseEntity = response.getEntity(String.class);
                        // 尝试能否解析成APIErrorResponse
                        APIErrorResponse apiErrorResponse;
                        try {
                            apiErrorResponse = gson.fromJson(responseEntity, APIErrorResponse.class);
                        } catch (Exception e) {
                            String reasonPhrase = response.getStatusInfo().getReasonPhrase();
                            // INFORMATIONAL, SUCCESSFUL, REDIRECTION, CLIENT_ERROR, SERVER_ERROR, OTHER
                            Response.Status.Family family = response.getStatusInfo().getFamily();
                            throw new HttpResponseException(status, String.format("status=%s，reasonPhrase=%s，family=%s"
                                    , status, reasonPhrase, family));
                        }
                        throw new HttpResponseException(status, apiErrorResponse.getErrorMessage());
                    default:
                        String _responseEntity = response.getEntity(String.class);
                        throw new HttpResponseException(status, String.format("未处理的状态，status=%s，reason=%s"
                                , status, _responseEntity));
                }
            } else {
                RESTResponse resp = RESTResponse.fromClientResponse(response);
                LOG.warn("request is fail," + resp.toString());
                throw new HttpResponseException(-1, "请求失败，返回为空，未知的异常");
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
        responseMessage.setBody(body);
        return responseMessage;
    }
}
