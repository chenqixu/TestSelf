package com.cqx.common.utils.hdfs;

/**
 * HdfsBean
 *
 * @author chenqixu
 */
public class HdfsBean {
    private String auth_type = "default";//default、bdoc、kerberos
    private String bdoc_id;
    private String bdoc_key;
    private String principal;
    private String keytab;
    private String krb5;

    public String getAuth_type() {
        return auth_type;
    }

    public void setAuth_type(String auth_type) {
        this.auth_type = auth_type;
    }

    public String getBdoc_id() {
        return bdoc_id;
    }

    public void setBdoc_id(String bdoc_id) {
        this.bdoc_id = bdoc_id;
    }

    public String getBdoc_key() {
        return bdoc_key;
    }

    public void setBdoc_key(String bdoc_key) {
        this.bdoc_key = bdoc_key;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getKeytab() {
        return keytab;
    }

    public void setKeytab(String keytab) {
        this.keytab = keytab;
    }

    public String getKrb5() {
        return krb5;
    }

    public void setKrb5(String krb5) {
        this.krb5 = krb5;
    }
}
