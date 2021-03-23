package com.cqx.bean;

/**
 * UserMktBean
 *
 * @author chenqixu
 */
public class UserMktBean {
    private long msisdn;
    private String rela_sale_ids;

    public String toString() {
        return msisdn + " , " + rela_sale_ids;
    }

    public long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(long msisdn) {
        this.msisdn = msisdn;
    }

    public String getRela_sale_ids() {
        return rela_sale_ids;
    }

    public void setRela_sale_ids(String rela_sale_ids) {
        this.rela_sale_ids = rela_sale_ids;
    }
}
