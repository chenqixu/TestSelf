package com.cqx.common.utils.http.auth;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * WebClientDevWrapper
 *
 * @author huawei
 * @version [V100R002C30, 2014-09-09]
 * @since [OM 1.0]
 */
public class WebClientDevWrapper {
    private static final String PROTOCOL_NAME = "https";
    private static final int PORT = 443;
    private static final String DEFAULT_SSL_VERSION = "TLS";

    /**
     * 获取一个SSLContext实例
     *
     * @param userTLSVersion TLS版本
     * @return SSLContext实例
     */
    public static SSLContext getSSLContext(String userTLSVersion) {
        SSLContext sslContext = null;
        try {
            // 获取一个SSLContext实例
            sslContext = SSLContext.getInstance(userTLSVersion);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
            return null;
        }

        if (sslContext == null) {
            return null;
        }
        X509TrustManager trustManager = new X509TrustManager() {
            /**
             * 获取接受发行人
             *
             * @return X509Certificate[] 返回受信任的X509证书数组
             */
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            /**
             * 检查受信任的客户端
             * <pre>
             *     该方法检查客户端的证书，若不信任该证书则抛出异常。由于我们不需要对客户端进行认证，
             *     因此我们只需要执行默认的信任管理器的这个方法。JSSE中，默认的信任管理器类为TrustManager。
             * </pre>
             *
             * @param ax509certificate X509Certificate
             * @param s String
             *
             * @throws CertificateException 异常
             */
            public void checkClientTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
            }

            /**
             * 检查受信任的服务器
             * <pre>
             *     该方法检查服务器的证书，若不信任该证书同样抛出异常。
             *     通过自己实现该方法，可以使之信任我们指定的任何证书。
             *     在实现该方法时，也可以简单的不做任何处理，即一个空的函数体，由于不会抛出异常，它就会信任任何证书。
             * </pre>
             *
             * @param ax509certificate X509Certificate
             * @param s  String
             *
             * @throws CertificateException 异常
             */
            public void checkServerTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
            }
        };
        try {
            // 初始化SSLContext实例
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    /**
     * 获取一个TLS/SSL连接的分层套接字工厂
     *
     * @param sslContext SSLContext实例
     * @return TLS/SSL连接的分层套接字工厂
     */
    public static SSLSocketFactory getSSLSocketFactory(SSLContext sslContext) {
        // TLS/SSL连接的分层套接字工厂
        // SSLSocketFactory可用于根据可信证书列表验证HTTPS服务器的身份，并使用私钥对HTTPS服务器进行身份验证。
        SSLSocketFactory sslSocketFactory = null;
        // 这里对华为的代码做了部分调整
        // SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER：关闭了主机名验证，允许连接任何主机
        sslSocketFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        return sslSocketFactory;
    }

    /**
     * 获取一个TLS/SSL连接的分层套接字工厂
     *
     * @param userTLSVersion TLS版本
     * @return TLS/SSL连接的分层套接字工厂
     */
    public static SSLSocketFactory getSSLSocketFactory(String userTLSVersion) {
        // SSLContext实例
        SSLContext sslContext = getSSLContext(userTLSVersion);
        assert sslContext != null;
        // TLS/SSL连接的分层套接字工厂
        // SSLSocketFactory可用于根据可信证书列表验证HTTPS服务器的身份，并使用私钥对HTTPS服务器进行身份验证。
        return getSSLSocketFactory(sslContext);
    }

    /**
     * 对客户端进行包装
     *
     * @param base           HttpClient
     * @param userTLSVersion String
     * @return HttpClient
     */
    public static HttpClient wrapClient(HttpClient base, String userTLSVersion) {
        // TLS/SSL连接的分层套接字工厂
        // SSLSocketFactory可用于根据可信证书列表验证HTTPS服务器的身份，并使用私钥对HTTPS服务器进行身份验证。
        SSLSocketFactory sslSocketFactory = getSSLSocketFactory(userTLSVersion);
        assert sslSocketFactory != null;
        /**
         * ClientConnectionManager
         * <pre>
         *     客户端连接的管理接口。
         *     HTTP连接管理器的目的是作为新HTTP连接的工厂，
         *     管理持久连接并同步对持久连接的访问，确保一次只有一个执行线程可以访问一个连接。
         *     此接口的实现必须是线程安全的。
         *     对共享数据的访问必须同步，因为此接口的方法可以从多个线程执行。
         * </pre>
         */
        ClientConnectionManager ccm = base.getConnectionManager();
        // 一组受支持的协议方案。方案由小写名称标识。
        SchemeRegistry sr = ccm.getSchemeRegistry();
        // 注册一个计划。稍后可以使用getScheme或get按其名称检索方案。
        sr.register(new Scheme(PROTOCOL_NAME, PORT, sslSocketFactory));
        // 根据参数和连接管理器创建新的HTTP客户端。
        return new DefaultHttpClient(ccm, base.getParams());
    }
}
