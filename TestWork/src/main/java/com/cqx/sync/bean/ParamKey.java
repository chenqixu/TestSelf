package com.cqx.sync.bean;

/**
 * ParamKey
 *
 * @author chenqixu
 */
public class ParamKey {
    private String key;
    private boolean isFrontLike = false;
    private boolean isBehindLike = false;

    public ParamKey(String key, String front, String behind) {
        this.key = key;
        if (front.trim().equals("%")) isFrontLike = true;
        if (behind.trim().equals("%")) isBehindLike = true;
    }

    public static ParamKey newIntance(String key, String front, String behind) {
        return new ParamKey(key, front, behind);
    }

    public String toString() {
        return "[key]" + key + "，[isFrontLike]" + isFrontLike + "，[isBehindLike]" + isBehindLike;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isFrontLike() {
        return isFrontLike;
    }

    public void setFrontLike(boolean frontLike) {
        isFrontLike = frontLike;
    }

    public boolean isBehindLike() {
        return isBehindLike;
    }

    public void setBehindLike(boolean behindLike) {
        isBehindLike = behindLike;
    }
}
