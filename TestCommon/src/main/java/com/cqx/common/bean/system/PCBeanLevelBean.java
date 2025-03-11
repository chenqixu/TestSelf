package com.cqx.common.bean.system;

/**
 * PCBeanLevelBean
 *
 * @author chenqixu
 */
public class PCBeanLevelBean {
    private int level = 0;
    private PCBean parent;
    private PCBean pcBeanLv = new PCBean();
    private String currentLv = null;
    private String hisLv = null;

    public PCBeanLevelBean(int level, PCBean parent) {
        this.level = level;
        this.parent = parent;
    }

    public PCBeanLevelBean(int level) {
        this.level = level;
    }

    public String getHisLv() {
        return hisLv;
    }

    public void setHisLv(String hisLv) {
        this.hisLv = hisLv;
    }

    public String getCurrentLv() {
        return currentLv;
    }

    public void setCurrentLv(String currentLv) {
        this.currentLv = currentLv;
    }

    public PCBean getPcBeanLv() {
        return pcBeanLv;
    }

    public void setPcBeanLv(PCBean pcBeanLv) {
        this.pcBeanLv = pcBeanLv;
    }

    public PCBean getParent() {
        return parent;
    }

    public void setParent(PCBean parent) {
        this.parent = parent;
    }
}
