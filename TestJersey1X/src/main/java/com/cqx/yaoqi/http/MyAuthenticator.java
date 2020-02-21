package com.cqx.yaoqi.http;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * 代理模式所需的认证
 *
 * @author chenqixu
 */
public class MyAuthenticator extends Authenticator {
    private String user = "";
    private String password = "";

    public MyAuthenticator(String user, String password) {
        this.user = user;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password.toCharArray());
    }
}
