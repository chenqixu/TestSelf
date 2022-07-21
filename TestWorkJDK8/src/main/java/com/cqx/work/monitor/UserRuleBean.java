package com.cqx.work.monitor;

import com.cqx.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.util.IPAddressUtil;

import java.text.ParseException;

/**
 * 监控规则Bean
 */
public class UserRuleBean {
    private static final Logger logger = LoggerFactory.getLogger(UserRuleBean.class);
    private String businessId;
    private String queryId;       //    varchar2(36) not null,
    private String querytype;       //  varchar2(1)  not null,
    private String selectId;       //   varchar2(3)  not null,
    private String startTime;       //  varchar2(32) not null,
    private String endTime;       //    varchar2(32) not null,
    private String srcIp;       //      varchar2(255),
    private String destIp;       //     varchar2(255),
    private String url;       //        varchar2(4000),
    private String urlKey;       //     varchar2(4000),
    private String host;       //       varchar2(4000),
    private String hostKey;       //    varchar2(4000),
    private String netType;       //    varchar2(1),
    private UserRuleTypeEnum userRuleTypeEnum;
    private String srcIpMask;//源公网IP掩码位数	string	3	C	对于IPV4地址，该字段携带IPV4子网掩码位数。对于IPV6地址，该字段携带IPV6前缀的位数。该字段与源公网IP地址共同表示源公网IP地址段。例如1.1.1.1,掩码位数为24，则IPV4地址段为1.1.1.0~1.1.1.255
    private String destIpMask;//目的IP掩码位数	string	3	C	对于IPV4地址，该字段携带IPV4子网掩码位数。对于IPV6地址，该字段携带IPV6前缀的位数。该字段与目的IP地址共同表示目的IP地址段。
    private String wlanStartTime;
    private String wlanEndTime;
    private boolean destIpIsIPv4;
    private boolean destIpIsIPv6;
    private boolean srcIpIsIPv4;
    private boolean srcIpIsIPv6;
    private String microStartTime;
    private String microEndTime;

    public String getMicroStartTime() {
        return microStartTime;
    }

    public void setMicroStartTime(String microStartTime) {
        this.microStartTime = microStartTime;
    }

    public String getMicroEndTime() {
        return microEndTime;
    }

    public void setMicroEndTime(String microEndTime) {
        this.microEndTime = microEndTime;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getQuerytype() {
        return querytype;
    }

    public void setQuerytype(String querytype) {
        this.querytype = querytype;
    }

    public String getSelectId() {
        return selectId;
    }

    public void setSelectId(String selectId) {
        this.selectId = selectId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
        try {
            String walnStartTime = Utils.formatTime(Utils.getTime(startTime), "yyyy-MM-dd HH:mm:ss") + ".000000000";
            setWlanStartTime(walnStartTime);

            setMicroStartTime(Utils.getTime(startTime)+"000");
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
        try {
            String walnEndTime = Utils.formatTime(Utils.getTime(endTime), "yyyy-MM-dd HH:mm:ss") + ".000000000";
            setWlanEndTime(walnEndTime);

            setMicroEndTime(Utils.getTime(endTime)+"000");
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
        setSrcIpIsIPv4(IPAddressUtil.isIPv4LiteralAddress(srcIp));
        setSrcIpIsIPv6(IPAddressUtil.isIPv4LiteralAddress(srcIp));
    }

    public String getDestIp() {
        return destIp;
    }

    public void setDestIp(String destIp) {
        this.destIp = destIp;
        setDestIpIsIPv4(IPAddressUtil.isIPv4LiteralAddress(destIp));
        setDestIpIsIPv6(IPAddressUtil.isIPv6LiteralAddress(destIp));
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlKey() {
        return urlKey;
    }

    public void setUrlKey(String urlKey) {
        this.urlKey = urlKey;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHostKey() {
        return hostKey;
    }

    public void setHostKey(String hostKey) {
        this.hostKey = hostKey;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public UserRuleTypeEnum getUserRuleTypeEnum() {
        return userRuleTypeEnum;
    }

    public void setUserRuleTypeEnum(UserRuleTypeEnum userRuleTypeEnum) {
        this.userRuleTypeEnum = userRuleTypeEnum;
    }

    public String getSrcIpMask() {
        return srcIpMask;
    }

    public void setSrcIpMask(String srcIpMask) {
        this.srcIpMask = srcIpMask;
    }

    public String getDestIpMask() {
        return destIpMask;
    }

    public void setDestIpMask(String destIpMask) {
        this.destIpMask = destIpMask;
    }

    @Override
    public String toString() {
        return "UserRuleBean{" +
                "businessId='" + businessId + '\'' +
                ", queryId='" + queryId + '\'' +
                ", querytype='" + querytype + '\'' +
                ", selectId='" + selectId + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", srcIp='" + srcIp + '\'' +
                ", destIp='" + destIp + '\'' +
                ", url='" + url + '\'' +
                ", urlKey='" + urlKey + '\'' +
                ", host='" + host + '\'' +
                ", hostKey='" + hostKey + '\'' +
                ", netType='" + netType + '\'' +
                ", userRuleTypeEnum=" + userRuleTypeEnum +
                ", srcIpMask='" + srcIpMask + '\'' +
                ", destIpMask='" + destIpMask + '\'' +
                '}';
    }


    public String getWlanStartTime() {
        return wlanStartTime;
    }

    public void setWlanStartTime(String wlanStartTime) {
        this.wlanStartTime = wlanStartTime;
    }

    public String getWlanEndTime() {
        return wlanEndTime;
    }

    public void setWlanEndTime(String wlanEndTime) {
        this.wlanEndTime = wlanEndTime;
    }

    public boolean isDestIpIsIPv4() {
        return destIpIsIPv4;
    }

    public void setDestIpIsIPv4(boolean destIpIsIPv4) {
        this.destIpIsIPv4 = destIpIsIPv4;
    }

    public boolean isDestIpIsIPv6() {
        return destIpIsIPv6;
    }

    public void setDestIpIsIPv6(boolean destIpIsIPv6) {
        this.destIpIsIPv6 = destIpIsIPv6;
    }

    public boolean isSrcIpIsIPv4() {
        return srcIpIsIPv4;
    }

    public void setSrcIpIsIPv4(boolean srcIpIsIPv4) {
        this.srcIpIsIPv4 = srcIpIsIPv4;
    }

    public boolean isSrcIpIsIPv6() {
        return srcIpIsIPv6;
    }

    public void setSrcIpIsIPv6(boolean srcIpIsIPv6) {
        this.srcIpIsIPv6 = srcIpIsIPv6;
    }
}