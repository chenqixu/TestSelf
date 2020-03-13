package com.cqx.sync.bean;

/**
 * RsmgrCluster
 *
 * @author chenqixu
 */
public class RsmgrCluster {
    private String type_id;
    private String resource_id;
    private String cluster_name;
    private String authentication;
    private String cluster_sign;
    private String mgt_name;
    private int sort;

    public String toString() {
        return "[type_id]" + type_id + ",[resource_id]" + resource_id + ",[cluster_name]"
                + cluster_name + ",[authentication]" + authentication + ",[cluster_sign]"
                + cluster_sign + ",[mgt_name]" + mgt_name + ",[sort]" + sort;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getResource_id() {
        return resource_id;
    }

    public void setResource_id(String resource_id) {
        this.resource_id = resource_id;
    }

    public String getCluster_name() {
        return cluster_name;
    }

    public void setCluster_name(String cluster_name) {
        this.cluster_name = cluster_name;
    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public String getCluster_sign() {
        return cluster_sign;
    }

    public void setCluster_sign(String cluster_sign) {
        this.cluster_sign = cluster_sign;
    }

    public String getMgt_name() {
        return mgt_name;
    }

    public void setMgt_name(String mgt_name) {
        this.mgt_name = mgt_name;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
