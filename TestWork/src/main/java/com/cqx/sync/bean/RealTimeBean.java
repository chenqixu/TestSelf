package com.cqx.sync.bean;

import com.cqx.annotation.BeanCompare;
import com.cqx.annotation.BeanCompare.NOT_PK_KEY;
import com.cqx.annotation.BeanCompare.PK_KEY;

/**
 * RealTimeBean
 *
 * @author chenqixu
 */
@BeanCompare
public class RealTimeBean {
    @PK_KEY
    private int id;
    @NOT_PK_KEY
    private int amount;

    public String toString() {
        return id + "," + amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
