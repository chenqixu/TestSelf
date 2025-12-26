package com.cqx.common.utils.security;

import org.apache.hadoop.util.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * KerberoUtil
 *
 * @author
 * @since 8.0.0
 */
public class KerberosUtil {
    /**
     * JAVA_VENDER
     */
    public static final String JAVA_VENDER = "java.vendor";
    /**
     * IBM_FLAG
     */
    public static final String IBM_FLAG = "IBM";
    /**
     * CONFIG_CLASS_FOR_IBM
     */
    public static final String CONFIG_CLASS_FOR_IBM = "com.ibm.security.krb5.internal.Config";
    /**
     * CONFIG_CLASS_FOR_SUN
     */
    public static final String CONFIG_CLASS_FOR_SUN = "sun.security.krb5.Config";
    /**
     * METHOD_GET_INSTANCE
     */
    public static final String METHOD_GET_INSTANCE = "getInstance";
    /**
     * METHOD_GET_DEFAULT_REALM
     */
    public static final String METHOD_GET_DEFAULT_REALM = "getDefaultRealm";
    /**
     * DEFAULT_REALM
     */
    public static final String DEFAULT_REALM = "HADOOP.COM";
    private static final Logger logger = LoggerFactory.getLogger(KerberosUtil.class);
    /**
     * Percentage of the ticket window to use before we renew ticket.
     */
    private static final float TICKET_RENEW_WINDOW = 0.80f;
    private static boolean shouldRenewImmediatelyForTests = false;

    /**
     * Get Krb5 Domain Realm
     */
    public static String getKrb5DomainRealm() {
        Class<?> krb5ConfClass;
        String peerRealm = null;
        try {
            if (System.getProperty(JAVA_VENDER).contains(IBM_FLAG)) {
                krb5ConfClass = Class.forName(CONFIG_CLASS_FOR_IBM);
            } else {
                krb5ConfClass = Class.forName(CONFIG_CLASS_FOR_SUN);
            }

            Method getInstanceMethod = krb5ConfClass.getMethod(METHOD_GET_INSTANCE);
            Object kerbConf = getInstanceMethod.invoke(krb5ConfClass);

            Method getDefaultRealmMethod = krb5ConfClass.getDeclaredMethod(METHOD_GET_DEFAULT_REALM);
            if (getDefaultRealmMethod.invoke(kerbConf) instanceof String) {
                peerRealm = (String) getDefaultRealmMethod.invoke(kerbConf);
            }
            logger.info("Get default realm successfully, the realm is : {}", peerRealm);

        } catch (ClassNotFoundException e) {
            peerRealm = DEFAULT_REALM;
            logger.warn("Get default realm failed, use default value : " + DEFAULT_REALM);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            peerRealm = DEFAULT_REALM;
            logger.warn("Get default realm failed, use default value : " + DEFAULT_REALM);
        }

        return peerRealm;
    }

    static KerberosTicket getTGT(Subject subject) {
        Set<KerberosTicket> tickets = subject
                .getPrivateCredentials(KerberosTicket.class);
        for (KerberosTicket ticket : tickets) {
            if (isTGSPrincipal(ticket.getServer())) {
                return ticket;
            }
        }
        return null;
    }

    static boolean isTGSPrincipal(KerberosPrincipal principal) {
        if (principal == null)
            return false;
        if (principal.getName().equals("krbtgt/" + principal.getRealm() +
                "@" + principal.getRealm())) {
            return true;
        }
        return false;
    }

    static long getRefreshTime(KerberosTicket tgt) {
        long start = tgt.getStartTime().getTime();
        long end = tgt.getEndTime().getTime();
        return start + (long) ((end - start) * TICKET_RENEW_WINDOW);
    }

    /**
     * 认证检查，过期返回true
     *
     * @param subject
     * @return
     */
    public static boolean checkTGT(Subject subject) {
        if (subject == null) {
            throw new NullPointerException("subject 参数为空！");
        }
        KerberosTicket tgt = getTGT(subject);
        if (tgt == null) {
            throw new NullPointerException(String.format("无法从%s中获取KerberosTicket!", subject));
        }
        long time_now = Time.now();
        long refresh_time = getRefreshTime(tgt);
        if (!KerberosUtil.shouldRenewImmediatelyForTests && time_now < refresh_time) {
            logger.info("[Kerberos认证检查] 认证没有过期，当前时间={}, 过期时间={}", time_now, refresh_time);
        } else {
            logger.warn("[Kerberos认证检查] 认证过期！");
            return true;
        }
        return false;
    }
}
