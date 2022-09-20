package com.cqx.common.utils.hdfs;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import java.util.HashMap;
import java.util.Map;

/**
 * KerberosConfiguration
 *
 * @author chenqixu
 */
public class KerberosConfiguration extends Configuration {
    private String keyTab;
    private String principal;

    public KerberosConfiguration(String keyTab, String principal) {
        this.keyTab = keyTab;
        this.principal = principal;
    }

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
        if ("Client".equalsIgnoreCase(name)) {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("debug", "true");
            // 1.enter the username and passsword
            //paramMap.put("storeKey", "true");
            //paramMap.put("doNotPrompt", "false");
            // 2.use keytab file
            paramMap.put("doNotPrompt", "true");
            paramMap.put("useKeyTab", "true");
            paramMap.put("keyTab", keyTab);
            paramMap.put("principal", principal);
            paramMap.put("useTicketCache", "false");
//            paramMap.put("ticketCache", "/hadoop-data/etc/hadoop/keytab_cache");
            AppConfigurationEntry configurationEntry = new AppConfigurationEntry(
                    "com.sun.security.auth.module.Krb5LoginModule"
                    , AppConfigurationEntry.LoginModuleControlFlag.REQUIRED
                    , paramMap);
            return new AppConfigurationEntry[]{configurationEntry};
        }
        return null;
    }
}
