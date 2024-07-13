package com.cqx.common.utils.jdbc.bean;

import java.math.BigDecimal;

/**
 * FloatBean
 *
 * @author chenqixu
 */
public class FloatBean {
    private int total;
    private int not_null_total;
    private BigDecimal percentage;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getNot_null_total() {
        return not_null_total;
    }

    public void setNot_null_total(int not_null_total) {
        this.not_null_total = not_null_total;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
}
