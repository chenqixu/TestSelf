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
}