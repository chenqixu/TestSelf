package com.cqx.common.utils.email;

import com.sun.mail.util.MailSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * EmailUtil
 *
 * @author chenqixu
 */
public class EmailUtil {
    private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);
    private boolean isDebug;

    public EmailUtil() {
    }

    public EmailUtil(boolean isDebug) {
        this.isDebug = isDebug;
    }

    /**
     * 通过EmailUserBean构造发送对象的地址
     *
     * @param receive
     * @return
     */
    private InternetAddress buildInternetAddress(EmailUserBean receive) {
        try {
            return new InternetAddress(receive.getAccount(), receive.getPersonal(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建一封只包含文本的简单邮件
     *
     * @param session  和服务器交互的会话
     * @param send     发件人
     * @param receives 收件人
     * @param subject  主题
     * @param content  正文
     * @return
     * @throws Exception
     */
    private MimeMessage createMimeMessage(Session session, EmailUserBean send
            , List<EmailUserBean> receives, String subject, String content) throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);
        // 2. From: 发件人
        message.setFrom(new InternetAddress(send.getAccount(), send.getPersonal(), "UTF-8"));
        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipients(MimeMessage.RecipientType.TO
                , receives.stream().map(this::buildInternetAddress).toArray(Address[]::new));
        // 4. Subject: 邮件主题
        message.setSubject(subject, "UTF-8");
        // 5. Content: 邮件正文（可以使用html标签）
        message.setContent(content, "text/html;charset=UTF-8");
        // 6. 设置发件时间
        message.setSentDate(new Date());
        // 7. 保存设置
        message.saveChanges();
        return message;
    }

    /**
     * 创建一封带附件的邮件
     *
     * @param session
     * @param send
     * @param receives
     * @param subject
     * @param content
     * @param appendixs
     * @return
     * @throws Exception
     */
    private MimeMessage createMimeMultiMessage(Session session, EmailUserBean send
            , List<EmailUserBean> receives, String subject, String content, List<String> appendixs) throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);
        // 2. From: 发件人
        message.setFrom(new InternetAddress(send.getAccount(), send.getPersonal(), "UTF-8"));
        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipients(MimeMessage.RecipientType.TO
                , receives.stream().map(this::buildInternetAddress).toArray(Address[]::new));
        // 4. Subject: 邮件主题
        message.setSubject(subject, "UTF-8");
        // 5. Content: 邮件正文
        MimeBodyPart contentText = new MimeBodyPart();
        contentText.setContent(content, "text/html;charset=utf-8");
        // 6. Appendix: 邮件附件
        List<MimeBodyPart> appendixParts = new ArrayList<>();
        for (String _appendixs : appendixs) {
            File _file = new File(_appendixs);
            if (_file.isFile()) {
                MimeBodyPart appendix = new MimeBodyPart();
                appendix.setDataHandler(new DataHandler(new FileDataSource(_appendixs)));
                appendix.setFileName(_file.getName());
                appendixParts.add(appendix);
                logger.info("成功添加附件 {}。", _appendixs);
            } else {
                logger.warn("非法的附件: {} 不是文件！", _appendixs);
            }
        }
        // 7. 拼接附件和正文
        MimeMultipart allFile = new MimeMultipart();
        for (MimeBodyPart _appendix : appendixParts) {
            allFile.addBodyPart(_appendix);// 附件
        }
        allFile.addBodyPart(contentText);// 正文
        allFile.setSubType("mixed"); // 正文和附件都存在邮件中，所有类型设置为mixed
        // 8. 放到Message消息中
        message.setContent(allFile);
        // 9. 设置发件时间
        message.setSentDate(new Date());
        // 10. 保存设置
        message.saveChanges();
        return message;
    }

    /**
     * 发送邮件
     *
     * @param serverBean
     * @param send
     * @param receives
     * @param subject
     * @param content
     * @throws Exception
     */
    public void sendMail(EmailServerBean serverBean, EmailUserBean send
            , Stream<EmailUserBean> receives, String subject, String content) throws Exception {
        sendMail(serverBean, send, receives.collect(Collectors.toList()), subject, content);
    }

    /**
     * 发送邮件
     *
     * @param serverBean
     * @param send
     * @param receives
     * @param subject
     * @param content
     * @throws Exception
     */
    public void sendMail(EmailServerBean serverBean, EmailUserBean send
            , List<EmailUserBean> receives, String subject, String content) throws Exception {
        sendMail(serverBean, send, receives, subject, content, new ArrayList<>());
    }

    /**
     * 发送邮件
     *
     * @param serverBean
     * @param send
     * @param receives
     * @param subject
     * @param content
     * @param appendixs
     * @throws Exception
     */
    public void sendMail(EmailServerBean serverBean, EmailUserBean send
            , List<EmailUserBean> receives, String subject, String content, Stream<String> appendixs) throws Exception {
        sendMail(serverBean, send, receives, subject, content, appendixs.collect(Collectors.toList()));
    }

    /**
     * 发送邮件
     *
     * @param serverBean
     * @param send
     * @param receives
     * @param subject
     * @param content
     * @param appendixs
     * @throws Exception
     */
    public void sendMail(EmailServerBean serverBean, EmailUserBean send
            , Stream<EmailUserBean> receives, String subject, String content, Stream<String> appendixs) throws Exception {
        sendMail(serverBean, send, receives.collect(Collectors.toList()), subject, content, appendixs.collect(Collectors.toList()));
    }

    /**
     * 发送邮件
     *
     * @param serverBean 服务器
     * @param send       发送人
     * @param receives   接收人
     * @param subject    主题
     * @param content    正文
     * @param appendixs  附件
     * @throws Exception
     */
    public void sendMail(EmailServerBean serverBean, EmailUserBean send
            , List<EmailUserBean> receives, String subject, String content, List<String> appendixs) throws Exception {
        Properties properties = new Properties();
        // 使用的协议
        properties.put("mail.transport.protocol", serverBean.getProtocol());
        // 发件人的邮箱的 SMTP 服务器地址
        properties.put("mail.host", serverBean.getServerHost());
        // 设置端口
        properties.put("mail.smtp.port", serverBean.getSmtpPort());
        // 需要请求认证
        properties.put("mail.smtp.auth", "true");
        // 设置SSL加密
        if (serverBean.isIs_ssl()) {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            // 信任所有的地址
            sf.setTrustAllHosts(true);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.ssl.socketFactory", sf);
        }

        // 创建一个session对象
        Session session = Session.getDefaultInstance(properties);
        // 两种创建session对象都可以，只不过如果connect的密码为空，会从session获取密码，区别在这
//        Session session = Session.getDefaultInstance(properties, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(send.getAccount(), send.getPassword());
//            }
//        });
        // 开启debug模式
        session.setDebug(isDebug);
        // 连接对象
        Transport transport = null;
        try {
            // 获取连接对象
            transport = session.getTransport();
            // 连接服务器
            transport.connect(serverBean.getServerHost(), send.getAccount(), send.getPassword());
            MimeMessage message;
            if (appendixs != null && appendixs.size() > 0) {
                // 创建一封带附件的邮件
                message = createMimeMultiMessage(session, send, receives, subject, content, appendixs);
            } else {
                // 创建一封只包含文本的简单邮件
                message = createMimeMessage(session, send, receives, subject, content);
            }
            // 发送邮件
            transport.sendMessage(message, message.getAllRecipients());
            logger.info("邮件发送完成。");
        } finally {
            if (transport != null) {
                // 关闭连接
                transport.close();
            }
        }
    }
}
