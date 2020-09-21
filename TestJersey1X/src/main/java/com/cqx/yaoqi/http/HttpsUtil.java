package com.cqx.yaoqi.http;

import com.cqx.bean.RestParam;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import com.cqx.yaoqi.FileUtil;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

/**
 * HttpsUtil
 *
 * @author chenqixu
 */
public class HttpsUtil {
    private static final MyLogger logger = MyLoggerFactory.getLogger(HttpsUtil.class);

    // TODO: 2020/9/21 缺文件发送，可参考https://www.cnblogs.com/huadoumi/p/4772719.html

    /**
     * 发起http/https请求并获取结果
     */
    public Object httpRequest(RestParam restParam,
                              String deal_type, FileUtil fileUtil) {
        Object jsonObject = null;
        // 创建代理服务器
        InetSocketAddress addr = null;
        Proxy proxy = null;
        boolean ifProxyModel = restParam.getIfProxy() != null
                && restParam.getIfProxy() != ""
                && "TRUE".equals(restParam.getIfProxy());

        if (ifProxyModel) {
            addr = new InetSocketAddress(restParam.getProxyAddress(),
                    Integer.parseInt(restParam.getProxyPort()));
            proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理
//			Authenticator.setDefault(new MyAuthenticator(restParam
//					.getProxyUser(), restParam.getProxyPassWord()));// 设置代理的用户和密码
        }

        try {

            URL url = new URL(restParam.getReqUrl());
            if ("https".equals(restParam.getReqHttpsModel())) {
                TrustManager[] tmCerts = new javax.net.ssl.TrustManager[1];
                tmCerts[0] = new SimpleTrustManager();
                try {
                    SSLContext sslContext = SSLContext.getInstance("SSL");
                    sslContext.init(null, tmCerts, null);
                    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
                            .getSocketFactory());

                    HostnameVerifier hostnameVerifier = new SimpleHostnameVerifier();
                    HttpsURLConnection
                            .setDefaultHostnameVerifier(hostnameVerifier);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HttpsURLConnection httpUrlConn = null;
                if (ifProxyModel) {
                    httpUrlConn = (HttpsURLConnection) url
                            .openConnection(proxy);
                } else {
                    httpUrlConn = (HttpsURLConnection) url.openConnection();
                }

                // httpUrlConn.setSSLSocketFactory(ssf);
                jsonObject = ardoHttpsURLConnection(httpUrlConn,
                        restParam.getReqMethod(), restParam.getReqContent(),
                        restParam.getSessionId(), deal_type, fileUtil);
            } else {
                HttpURLConnection httpUrlConn = null;
                if (ifProxyModel) {
                    httpUrlConn = (HttpURLConnection) url.openConnection(proxy);
                } else {
                    httpUrlConn = (HttpURLConnection) url.openConnection();
                }
                jsonObject = ardoHttpURLConnection(httpUrlConn,
                        restParam.getReqMethod(), restParam.getReqContent(),
                        restParam.getSessionId(), deal_type, fileUtil);

            }

        } catch (ConnectException ce) {
            logger.error("API server connection timed out.");
            logger.error("【rest连接异常信息】" + ce.getMessage());
        } catch (Exception e) {
            logger.error("API https or http request error:{}", e);
            logger.error("【rest异常信息】" + e.getMessage());
        }
        return jsonObject;
    }

    /**
     * http请求方法
     *
     * @param httpUrlConn   请求路径
     * @param requestMethod 请求类型POST|GET
     * @param outputStr     请求内容
     * @param sessionId     sessionId(非必填)
     * @return JSONObject类型数据
     */
    public Object ardoHttpURLConnection(
            HttpURLConnection httpUrlConn, String requestMethod,
            String outputStr, String sessionId, String deal_type, FileUtil fileUtil) {
        Object jsonObject = null;
        StreamDeal streamDeal = null;
        InputStream inputStream = null;
        if (deal_type.equals("string")) {
            streamDeal = new StringStreamDeal();
        } else if (deal_type.equals("file")) {
            streamDeal = new FileStreamDeal();
        } else {
            return null;
        }
        try {
            // httpUrlConn = (HttpURLConnection) url.openConnection();

            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);

            if (sessionId != null && sessionId != "") {
                httpUrlConn.setRequestProperty("Cookie", "JSESSIONID="
                        + sessionId);
            }

            // 设置请求方式GET/POST
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod))
                httpUrlConn.connect();

            // 当有数据需要提交时
            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 将返回的输入流转换
            inputStream = httpUrlConn.getInputStream();
            jsonObject = streamDeal.deal(inputStream, fileUtil).toString();
        } catch (ConnectException ce) {
            logger.error("API server connection timed out.");
            logger.error("【rest http连接异常信息】" + ce.getMessage());
        } catch (FileNotFoundException e) {
            logger.error("【找不到文件，网站可能已经删除】" + e.getMessage());
        } catch (Exception e) {
            logger.error("API http request error:{}", e);
            logger.error("【rest http异常信息】" + e.getMessage());
        } finally {
            // 释放资源
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = null;
            if (httpUrlConn != null) httpUrlConn.disconnect();
        }
        return jsonObject;
    }

    /**
     * https请求方法
     *
     * @param httpUrlConn   请求路径
     * @param requestMethod 请求类型POST|GET
     * @param outputStr     请求内容
     * @param sessionId     sessionId(非必填)
     * @return JSONObject类型数据
     */
    public Object ardoHttpsURLConnection(
            HttpsURLConnection httpUrlConn, String requestMethod,
            String outputStr, String sessionId, String deal_type, FileUtil fileUtil) {
        StreamDeal streamDeal = null;
        String jsonObject = null;
        InputStream inputStream = null;
        if (deal_type.equals("string")) {
            streamDeal = new StringStreamDeal();
        } else if (deal_type.equals("file")) {
            streamDeal = new FileStreamDeal();
        } else {
            return null;
        }
        try {
            // httpUrlConn = (HttpsURLConnection) url.openConnection();
            httpUrlConn.setRequestProperty("User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);

            if (sessionId != null && sessionId != "") {
                httpUrlConn.setRequestProperty("Cookie", "JSESSIONID="
                        + sessionId);
            }

            // 设置请求方式GET/POST
            httpUrlConn.setRequestMethod(requestMethod);
            httpUrlConn.setRequestProperty("Content-Type", "application/json");

            if ("GET".equalsIgnoreCase(requestMethod))
                httpUrlConn.connect();

            // 当有数据需要提交时
            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 将返回的输入流进行处理
            inputStream = httpUrlConn.getInputStream();
            jsonObject = streamDeal.deal(inputStream, fileUtil).toString();
        } catch (ConnectException ce) {
            logger.error("API server connection timed out.");
            logger.error("【rest https连接异常信息】" + ce.getMessage());
        } catch (Exception e) {
            logger.error("API https request error:{}", e);
            logger.error("【rest https异常信息】" + e.getMessage());
        } finally {
            // 释放资源
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = null;
            if (httpUrlConn != null) httpUrlConn.disconnect();
        }
        return jsonObject;
    }
}
