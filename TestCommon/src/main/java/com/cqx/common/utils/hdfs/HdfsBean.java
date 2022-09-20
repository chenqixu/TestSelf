package com.cqx.common.utils.hdfs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private String name;
    private String hadoop_conf;
    private String jaas;

    public static HdfsBean newbuilder() {
        return new HdfsBean();
    }

    public static List<HdfsBean> parser(Object param) {
        List<Map<String, ?>> parser = (ArrayList<Map<String, ?>>) param;
        List<HdfsBean> result = new ArrayList<>();
        for (Map<String, ?> map : parser) {
            result.add(HdfsBean.newbuilder().parserMap(map));
        }
        return result;
    }

    public HdfsBean parserMap(Map<String, ?> param) {
        // 解析参数
        setName((String) param.get("name"));
        setHadoop_conf((String) param.get("hadoop_conf"));
        setAuth_type((String) param.get("auth_type"));
        setBdoc_id((String) param.get("bdoc_id"));
        setBdoc_key((String) param.get("bdoc_key"));
        setPrincipal((String) param.get("principal"));
        setKeytab((String) param.get("keytab"));
        setKrb5((String) param.get("krb5"));
        setJaas((String) param.get("jaas"));
        return this;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHadoop_conf() {
        return hadoop_conf;
    }

    public void setHadoop_conf(String hadoop_conf) {
        this.hadoop_conf = hadoop_conf;
    }

    public String getJaas() {
        return jaas;
    }

    public void setJaas(String jaas) {
        this.jaas = jaas;
    }
}
