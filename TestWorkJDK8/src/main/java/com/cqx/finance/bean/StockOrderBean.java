package com.cqx.finance.bean;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * StockBean
 *
 * @author chenqixu
 */
public class StockOrderBean implements BeanComparable {
    // 订单号
    private String orderId;
    // 公司
    private String companyName;
    // 订单类型
    private StockOrderType type;
    // 客户
    private String cusName;
    // 下单价
    private float hopePrice;
    // 成交价
    private float finalPrice;
    // 下单量
    private int hopeCount;
    // 下单时间
    private long hopeTime;
    // 最新订单量
    private int count;
    // 成交时间
    private long finalTime;
    // 成交量
    private int finalCount;
    // 成交单号列表
    private List<String> finalOrderList = new ArrayList<>();

    public StockOrderBean(float hopePrice, int hopeCount) {
        this("测试用户", StockOrderType.BUY, hopePrice, hopeCount);
    }

    public StockOrderBean(String cusName, StockOrderType type, float hopePrice, int hopeCount) {
        this(cusName, "000001", type, hopePrice, hopeCount);
    }

    public StockOrderBean(String cusName, String companyName, StockOrderType type, float hopePrice, int hopeCount) {
        setCusName(cusName);
        setCompanyName(companyName);
        setType(type);
        setHopePrice(hopePrice);
        setHopeCount(hopeCount);
        setCount(hopeCount);
        this.orderId = UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof StockOrderBean) {
            StockOrderBean _o = (StockOrderBean) o;
            long ret = this.getHopeTime() - _o.getHopeTime();
            if (ret < 0) {
                return -1;
            } else if (ret > 0) {
                return 1;
            } else {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public float getHopePrice() {
        return hopePrice;
    }

    public void setHopePrice(float hopePrice) {
        this.hopePrice = hopePrice;
        setHopeTime(System.currentTimeMillis());
    }

    public float getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(float finalPrice) {
        this.finalPrice = finalPrice;
        setFinalTime(System.currentTimeMillis());
    }

    @Override
    public long getHopeTime() {
        return hopeTime;
    }

    public void setHopeTime(long hopeTime) {
        this.hopeTime = hopeTime;
    }

    public long getFinalTime() {
        return finalTime;
    }

    public void setFinalTime(long finalTime) {
        this.finalTime = finalTime;
    }

    public StockOrderType isType() {
        return type;
    }

    public void setType(StockOrderType type) {
        this.type = type;
    }

    public String getCusName() {
        return cusName;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getHopeCount() {
        return hopeCount;
    }

    public void setHopeCount(int hopeCount) {
        this.hopeCount = hopeCount;
    }

    public int getFinalCount() {
        return finalCount;
    }

    public void setFinalCount(int finalCount) {
        this.finalCount = finalCount;
    }
}
