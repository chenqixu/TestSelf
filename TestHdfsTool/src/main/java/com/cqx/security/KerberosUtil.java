package com.cqx.security;

import com.sun.security.auth.module.Krb5LoginModule;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.Time;

import javax.security.auth.Subject;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.hadoop.util.PlatformName.IBM_JAVA;

/**
 * KerberosUtil
 *
 * @author chenqixu
 */
public class KerberosUtil {

    private static final boolean windows = System.getProperty("os.name").startsWith("Windows");
    private static final boolean is64Bit = System.getProperty("os.arch").contains("64");
    private static final boolean aix = System.getProperty("os.name").equals("AIX");
    private static String OS_LOGIN_MODULE_NAME;
    private static String keytabFile = null;
    private static String keytabPrincipal = null;

    static {
        OS_LOGIN_MODULE_NAME = getOSLoginModuleName();
    }

    public static void createModuel() {
        Krb5LoginModule module = new Krb5LoginModule();
        try {
            module.initialize(null, null, null, null);
            module.login();
            module.commit();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static void loginUserFromKeytab(String user, String path) {
//        if (!isSecurityEnabled())
//            return;

        keytabFile = path;
        keytabPrincipal = user;
        Subject subject = new Subject();
        LoginContext login;
        long start = 0;
        try {
            login = newLoginContext(HadoopConfiguration.KEYTAB_KERBEROS_CONFIG_NAME,
                    subject, new HadoopConfiguration());
            start = Time.now();
            login.login();
        } catch (LoginException le) {
            le.printStackTrace();
        }
    }

    /* Return the OS login module class name */
    private static String getOSLoginModuleName() {
        if (IBM_JAVA) {
            if (windows) {
                return is64Bit ? "com.ibm.security.auth.module.Win64LoginModule"
                        : "com.ibm.security.auth.module.NTLoginModule";
            } else if (aix) {
                return is64Bit ? "com.ibm.security.auth.module.AIX64LoginModule"
                        : "com.ibm.security.auth.module.AIXLoginModule";
            } else {
                return "com.ibm.security.auth.module.LinuxLoginModule";
            }
        } else {
            return windows ? "com.sun.security.auth.module.NTLoginModule"
                    : "com.sun.security.auth.module.UnixLoginModule";
        }
    }

    private static String prependFileAuthority(String keytabPath) {
        return keytabPath.startsWith("file://") ? keytabPath
                : "file://" + keytabPath;
    }

    private static LoginContext newLoginContext(String appName, Subject subject, javax.security.auth.login.Configuration loginConf)
            throws LoginException {
        // Temporarily switch the thread's ContextClassLoader to match this
        // class's classloader, so that we can properly load HadoopLoginModule
        // from the JAAS libraries.
        Thread t = Thread.currentThread();
        ClassLoader oldCCL = t.getContextClassLoader();
        t.setContextClassLoader(UserGroupInformation.HadoopLoginModule.class.getClassLoader());
        try {
            return new LoginContext(appName, subject, null, loginConf);
        } finally {
            t.setContextClassLoader(oldCCL);
        }
    }

    private static class HadoopConfiguration
            extends javax.security.auth.login.Configuration {

        private static final String SIMPLE_CONFIG_NAME = "hadoop-simple";
        private static final String USER_KERBEROS_CONFIG_NAME =
                "hadoop-user-kerberos";
        private static final String KEYTAB_KERBEROS_CONFIG_NAME =
                "hadoop-keytab-kerberos";

        private static final Map<String, String> BASIC_JAAS_OPTIONS =
                new HashMap<String, String>();
        private static final AppConfigurationEntry OS_SPECIFIC_LOGIN =
                new AppConfigurationEntry(OS_LOGIN_MODULE_NAME,
                        AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                        BASIC_JAAS_OPTIONS);
        private static final AppConfigurationEntry HADOOP_LOGIN =
                new AppConfigurationEntry(UserGroupInformation.HadoopLoginModule.class.getName(),
                        AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                        BASIC_JAAS_OPTIONS);
        private static final Map<String, String> USER_KERBEROS_OPTIONS =
                new HashMap<String, String>();
        private static final AppConfigurationEntry USER_KERBEROS_LOGIN =
                new AppConfigurationEntry(org.apache.hadoop.security.authentication.util.KerberosUtil.getKrb5LoginModuleName(),
                        AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL,
                        USER_KERBEROS_OPTIONS);
        private static final Map<String, String> KEYTAB_KERBEROS_OPTIONS =
                new HashMap<String, String>();
        private static final AppConfigurationEntry KEYTAB_KERBEROS_LOGIN =
                new AppConfigurationEntry(org.apache.hadoop.security.authentication.util.KerberosUtil.getKrb5LoginModuleName(),
                        AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                        KEYTAB_KERBEROS_OPTIONS);
        private static final AppConfigurationEntry[] SIMPLE_CONF =
                new AppConfigurationEntry[]{OS_SPECIFIC_LOGIN, HADOOP_LOGIN};
        private static final AppConfigurationEntry[] USER_KERBEROS_CONF =
                new AppConfigurationEntry[]{OS_SPECIFIC_LOGIN, USER_KERBEROS_LOGIN,
                        HADOOP_LOGIN};
        private static final AppConfigurationEntry[] KEYTAB_KERBEROS_CONF =
                new AppConfigurationEntry[]{KEYTAB_KERBEROS_LOGIN, HADOOP_LOGIN};

        static {
            String jaasEnvVar = System.getenv("HADOOP_JAAS_DEBUG");
            if (jaasEnvVar != null && "true".equalsIgnoreCase(jaasEnvVar)) {
                BASIC_JAAS_OPTIONS.put("debug", "true");
            }
        }

        static {
            if (IBM_JAVA) {
                USER_KERBEROS_OPTIONS.put("useDefaultCcache", "true");
            } else {
                USER_KERBEROS_OPTIONS.put("doNotPrompt", "true");
                USER_KERBEROS_OPTIONS.put("useTicketCache", "true");
            }
            String ticketCache = System.getenv("KRB5CCNAME");
            if (ticketCache != null) {
                if (IBM_JAVA) {
                    // The first value searched when "useDefaultCcache" is used.
                    System.setProperty("KRB5CCNAME", ticketCache);
                } else {
                    USER_KERBEROS_OPTIONS.put("ticketCache", ticketCache);
                }
            }
            USER_KERBEROS_OPTIONS.put("renewTGT", "true");
            USER_KERBEROS_OPTIONS.putAll(BASIC_JAAS_OPTIONS);
        }

        static {
            if (IBM_JAVA) {
                KEYTAB_KERBEROS_OPTIONS.put("credsType", "both");
            } else {
                KEYTAB_KERBEROS_OPTIONS.put("doNotPrompt", "true");
                KEYTAB_KERBEROS_OPTIONS.put("useKeyTab", "true");
                KEYTAB_KERBEROS_OPTIONS.put("storeKey", "true");
            }
            KEYTAB_KERBEROS_OPTIONS.put("refreshKrb5Config", "true");
            KEYTAB_KERBEROS_OPTIONS.putAll(BASIC_JAAS_OPTIONS);
        }

        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(String appName) {
            if (SIMPLE_CONFIG_NAME.equals(appName)) {
                return SIMPLE_CONF;
            } else if (USER_KERBEROS_CONFIG_NAME.equals(appName)) {
                return USER_KERBEROS_CONF;
            } else if (KEYTAB_KERBEROS_CONFIG_NAME.equals(appName)) {
                if (IBM_JAVA) {
                    KEYTAB_KERBEROS_OPTIONS.put("useKeytab",
                            prependFileAuthority(keytabFile));
                } else {
                    KEYTAB_KERBEROS_OPTIONS.put("keyTab", keytabFile);
                }
                KEYTAB_KERBEROS_OPTIONS.put("principal", keytabPrincipal);
                return KEYTAB_KERBEROS_CONF;
            }
            return null;
        }
    }
}
