package com.cqx.common.utils.http.auth;

import com.cqx.common.bean.http.ResponseMessage;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import java.io.IOException;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * kerberos+https
 *
 * @author chenqixu
 */
public class Krb5AuthUtil {
    private Logger LOG = LoggerFactory.getLogger(Krb5AuthUtil.class);
    private String principal;
    private String keyTabLocation;
    private String password;
    private final String LoginContextName = "Krb5Login";
    private HttpManager httpManager = new HttpManager();

    public Krb5AuthUtil() {
    }

    public Krb5AuthUtil(String principal, String keyTabLocation) {
        super();
        this.principal = principal;
        this.keyTabLocation = keyTabLocation;
    }

    public Krb5AuthUtil(String principal, String keyTabLocation, boolean isDebug) {
        this(principal, keyTabLocation);
        if (isDebug) {
            System.setProperty("sun.security.spnego.debug", "true");
            System.setProperty("sun.security.krb5.debug", "true");
        }
    }

    public Krb5AuthUtil(String principal, String keyTabLocation, String krb5Location, boolean isDebug) {
        this(principal, keyTabLocation, isDebug);
        System.setProperty("java.security.krb5.conf", krb5Location);
    }

    /**
     * 设置密码
     *
     * @param password
     */
    public void kinit(String password) {
//        String[] kinitParam = {principal, password};
//        // todo 无法使用，不知道这里的Kinit.main把数据初始化到了哪里？
//        Kinit.main(kinitParam);
        this.password = password;
    }

    /**
     * 构建一个Simple and Protected GSSAPI Negotiation Mechanism (SPNEGO) <br>
     * 即简单且受保护的GSSAPI协商机制下的HTTP客户端
     *
     * @return
     */
    private static HttpClient buildSpengoHttpClient() {
        // CloseableHttpClient实例的生成器
        HttpClientBuilder builder = HttpClientBuilder.create();
        // Lookup接口：一个可以根据名称小写来进行查找的接口
        // AuthSchemeProvider：身份验证方案的工厂类
        // AuthSchemes.SPNEGO：SPNEGO身份验证方案
        // SPNegoSchemeFactory：SPNEGO身份验证方案工厂类
        Lookup<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create().
                register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory(true)).build();
        // 设置验证方案
        builder.setDefaultAuthSchemeRegistry(authSchemeRegistry);
        // 创建一个默认凭据提供程序
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        // 设置给定身份验证作用域的凭据
        credentialsProvider.setCredentials(
                // 使用给定的主机、端口和域定义身份验证范围
                new AuthScope(null, -1, null)
                // 此接口表示由安全主体和可用于建立用户身份的密码组成的一组凭据
                , new Credentials() {
                    @Override
                    public Principal getUserPrincipal() {
                        return null;
                    }

                    @Override
                    public String getPassword() {
                        return null;
                    }
                });
        // 分配默认凭据提供程序实例，如果未在客户端执行上下文中显式设置，则该实例将用于请求执行。
        builder.setDefaultCredentialsProvider(credentialsProvider);
        // SSLContext和SSLSocketFactory二选一即可
        // SSLContext：表示安全套接字协议的实现， 它是SSLSocketFactory、SSLServerSocketFactory和SSLEngine的工厂
        builder.setSSLContext(WebClientDevWrapper.getSSLContext("TLSv1.2"));
//        builder.setSSLSocketFactory(WebClientDevWrapper.getSSLSocketFactory("TLSv1.2"));
        // SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER：关闭了主机名验证，允许连接任何主机
        builder.setSSLHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        return builder.build();
    }

    /**
     * 创建一个配置，使用到KeyTab文件
     *
     * @return 配置
     */
    public Configuration createConfiguration() {
        return new Configuration() {

            @Override
            public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
                return new AppConfigurationEntry[]{new AppConfigurationEntry(
                        com.sun.security.auth.module.Krb5LoginModule.class.getName()
                        , AppConfigurationEntry.LoginModuleControlFlag.REQUIRED
                        , new HashMap<String, Object>() {
                    {
                        put("useTicketCache", "true");
                        put("useKeyTab", "true");
                        put("keyTab", keyTabLocation);
                        put("refreshKrb5Config", "true");
                        put("principal", principal);
                        put("storeKey", "true");
                        put("doNotPrompt", "true");
                        put("isInitiator", "true");
                        put("debug", "true");
                    }
                })};
            }
        };
    }

    /**
     * 创建一个无KeyTab的配置
     *
     * @return 配置
     */
    public Configuration createNoKeyTabConfiguration() {
        return new Configuration() {

            @Override
            public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
                return new AppConfigurationEntry[]{new AppConfigurationEntry(
                        com.sun.security.auth.module.Krb5LoginModule.class.getName()
                        , AppConfigurationEntry.LoginModuleControlFlag.REQUIRED
                        , new HashMap<String, Object>() {
                })};
            }
        };
    }

    /**
     * 创建一个主题
     * <pre>
     *     主题表示单个实体（如个人）的一组相关信息。
     *     此类信息包括主体的身份及其安全相关属性（例如密码和加密密钥）。
     * </pre>
     *
     * @return
     */
    public Subject createSubject() {
        Set<Principal> princ = new HashSet<>(1);
        princ.add(new KerberosPrincipal(principal));
        return new Subject(false, princ, new HashSet<>(), new HashSet<>());
    }

    /**
     * 在通过身份验证的情况下执行对应的action
     *
     * @param privilegedAction 需要执行的action
     * @return HttpResponse Http响应
     */
    public HttpResponse subjectDoAs(PrivilegedAction<HttpResponse> privilegedAction) {
        try {
            /**
             * LoginContext类描述用于验证主题的基本方法，并提供一种独立于底层验证技术开发应用程序的方法。
             * 配置指定要与特定应用程序一起使用的身份验证技术或LoginModule。
             * 可以在应用程序下插入不同的LoginModule，而无需对应用程序本身进行任何修改。
             *
             * 除了支持可插拔身份验证外，此类还支持堆叠身份验证的概念。
             * 应用程序可以配置为使用多个LoginModule。
             * 例如，可以在应用程序下配置Kerberos登录模块和智能卡登录模块。
             *
             * 典型的调用方使用名称和CallbackHandler实例化LoginContext。
             * LoginContext将名称用作配置的索引，以确定应使用哪些LoginModule，
             * 以及哪些必须成功才能使整体身份验证成功。
             * CallbackHandler被传递给底层LoginModule，
             * 以便它们可以与用户通信和交互（例如，通过图形用户界面提示输入用户名和密码）。
             */
            LoginContext lc = new LoginContext(LoginContextName
                    , createSubject()
                    , new SetUsPwCallbackHandler()
                    , createNoKeyTabConfiguration()
            );
            // 执行身份验证
            lc.login();
            // 返回经过身份验证的主题
            Subject serviceSubject = lc.getSubject();
            // 在通过身份验证的情况下执行对应的action
            return Subject.doAs(serviceSubject, privilegedAction);
        } catch (Exception le) {
            LOG.error(le.getMessage(), le);
        }
        return null;
    }

    /**
     * 发送Get请求
     *
     * @param url
     * @param operationName
     * @return
     */
    public HttpResponse sendHttpGetRequest(final String url, final String operationName) {
        return subjectDoAs(new PrivilegedAction<HttpResponse>() {

            @Override
            public HttpResponse run() {
                return httpManager.sendHttpGetRequestGetHttpResponse(buildSpengoHttpClient()
                        , url
                        , operationName);
            }
        });
    }

    /**
     * 发送Put请求
     *
     * @param url
     * @param jsonString
     * @param operationName
     * @return
     */
    public HttpResponse sendHttpPutRequestWithString(final String url
            , final String jsonString, final String operationName) {
        return subjectDoAs(new PrivilegedAction<HttpResponse>() {

            @Override
            public HttpResponse run() {
                return httpManager.sendHttpPutRequestWithStringGetHttpResponse(buildSpengoHttpClient()
                        , url
                        , jsonString
                        , operationName);
            }
        });
    }

    /**
     * 简单解析Http返回
     *
     * @param response
     * @return
     * @throws IOException
     */
    public ResponseMessage<String> handleHttpResponse(HttpResponse response) throws IOException {
        return httpManager.handleHttpResponse(response);
    }

    /**
     * 设置Kerberos认证用户名和密码的回调
     */
    class SetUsPwCallbackHandler implements CallbackHandler {

        @Override
        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (Callback callback : callbacks) {
                if (callback instanceof NameCallback) {
                    NameCallback nameCallback = (NameCallback) callback;
                    nameCallback.setName(principal);
                } else if (callback instanceof PasswordCallback) {
                    PasswordCallback passwordCallback = (PasswordCallback) callback;
                    passwordCallback.setPassword(password.toCharArray());
                }
            }
        }
    }
}
