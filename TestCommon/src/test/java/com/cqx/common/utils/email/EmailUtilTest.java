package com.cqx.common.utils.email;

import org.junit.Test;

import java.util.stream.Stream;

public class EmailUtilTest {

    @Test
    public void sendMail() throws Exception {
        EmailUtil emailUtil = new EmailUtil(true);
        EmailServerBean serverBean = new EmailServerBean("10.46.180.90", "125", "smtp");
        EmailUserBean send = new EmailUserBean("cqx@fmcc.boss", "cqx123");
        EmailUserBean receive = new EmailUserBean("cqx@fmcc.boss");
        emailUtil.sendMail(serverBean, send, Stream.of(receive), "test-sub", "test-123");
    }

    @Test
    public void sendMailHasFile() throws Exception {
        EmailUtil emailUtil = new EmailUtil(true);
        EmailServerBean serverBean = new EmailServerBean("10.46.180.90", "125", "smtp");
        EmailUserBean send = new EmailUserBean("cqx@fmcc.boss", "cqx123");
        EmailUserBean receive = new EmailUserBean("cqx@fmcc.boss");
        emailUtil.sendMail(serverBean, send, Stream.of(receive), "test-sub", "test-123", Stream.of("d:\\tmp\\1.txt"));
    }

    @Test
    public void sendMuReMailHasFiles() throws Exception {
        EmailUtil emailUtil = new EmailUtil(true);
        EmailServerBean serverBean = new EmailServerBean("10.46.180.90", "125", "smtp");
        EmailUserBean send = new EmailUserBean("cqx@fmcc.boss", "cqx123");
        EmailUserBean receive = new EmailUserBean("cqx@fmcc.boss");
        emailUtil.sendMail(serverBean, send
                , Stream.of(receive, receive), "test-sub", "test-123"
                , Stream.of("d:\\tmp\\1.txt", "d:\\tmp\\2.txt"));
    }

    @Test
    public void send139MailNoSSL() throws Exception {
        // 从JVM参数中获取，使用方式：-Demail.password=xxx
        String password = System.getProperty("email.password");
        if (password == null || password.length() == 0) throw new NullPointerException("密码为空，请设置-Demail.password=xxx");
        EmailUtil emailUtil = new EmailUtil(true);
        EmailServerBean serverBean = new EmailServerBean("smtp.139.com", "25", "smtp", false);
        EmailUserBean send = new EmailUserBean("13509323824@139.com", password);
        EmailUserBean receive = new EmailUserBean("13509323824@139.com");
        emailUtil.sendMail(serverBean, send, Stream.of(receive), "test-sub", "test-123");
    }

    @Test
    public void send139MailSSL() throws Exception {
        // 从JVM参数中获取，使用方式：-Demail.password=xxx
        String password = System.getProperty("email.password");
        if (password == null || password.length() == 0) throw new NullPointerException("密码为空，请设置-Demail.password=xxx");
        EmailUtil emailUtil = new EmailUtil(true);
        EmailServerBean serverBean = new EmailServerBean("smtp.139.com", "465", "smtp");
        EmailUserBean send = new EmailUserBean("13509323824@139.com", password);
        EmailUserBean receive = new EmailUserBean("13509323824@139.com");
        emailUtil.sendMail(serverBean, send, Stream.of(receive), "test-sub", "test-123");
    }
}