package com.bussiness.bi.bi_svc;

import java.util.Date;

/**
 * SmUserBean
 *
 * @author chenqixu
 */
public class SmUserBean {
    private Long   user_id;                 // 用户编号
    private String nick_name;               // 用户别名
    private String user_name;               // 用户姓名
    private Long   org_id;                  // 机构ID
    private String org_name;                // 机构名称
    private Long   home_city;               // 归属市ID
    private String home_city_name;          // 归属市名称
    private Long   home_county;             // 归属县ID
    private String home_county_name;        // 归属县名称
    private String mobile_phone;            // 手机号
    private String mail_addr;               // 邮件地址

    private Long   passwd_repeat_cnt;       // 密码重复次数
    private Date last_pwd_chg_time = null; // 最后的密码修改时间
    private Long   expire_dates;            // 密码变更周期
    private Long   status;                  // 状态
    private Long   lock_flag;               // 锁状态
    private Date   expire_time       = null; // 帐户失效时间
    private Date   create_time       = null; // 创建时间

    private String verify_code       = null; // 校验码
    private String passwd            = null; // 密码

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_naame) {
        this.user_name = user_naame;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public Long getHome_city() {
        return home_city;
    }

    public void setHome_city(Long home_city) {
        this.home_city = home_city;
    }

    public String getHome_city_name() {
        return home_city_name;
    }

    public void setHome_city_name(String home_city_name) {
        this.home_city_name = home_city_name;
    }

    public Long getHome_county() {
        return home_county;
    }

    public void setHome_county(Long home_county) {
        this.home_county = home_county;
    }

    public String getHome_county_name() {
        return home_county_name;
    }

    public void setHome_county_name(String home_county_name) {
        this.home_county_name = home_county_name;
    }

    public String getMobile_phone() {
        return mobile_phone;
    }

    public void setMobile_phone(String mobile_phone) {
        this.mobile_phone = mobile_phone;
    }

    public String getMail_addr() {
        return mail_addr;
    }

    public void setMail_addr(String mail_addr) {
        this.mail_addr = mail_addr;
    }

    public Long getPasswd_repeat_cnt() {
        return passwd_repeat_cnt;
    }

    public void setPasswd_repeat_cnt(Long passwd_repeat_cnt) {
        this.passwd_repeat_cnt = passwd_repeat_cnt;
    }

    public Date getLast_pwd_chg_time() {
        return last_pwd_chg_time;
    }

    public void setLast_pwd_chg_time(Date last_pwd_chg_time) {
        this.last_pwd_chg_time = last_pwd_chg_time;
    }

    public Long getExpire_dates() {
        return expire_dates;
    }

    public void setExpire_dates(Long expire_dates) {
        this.expire_dates = expire_dates;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getLock_flag() {
        return lock_flag;
    }

    public void setLock_flag(Long lock_flag) {
        this.lock_flag = lock_flag;
    }

    public Date getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(Date expire_time) {
        this.expire_time = expire_time;
    }

    public String getVerify_code() {
        return verify_code;
    }

    public void setVerify_code(String verify_code) {
        this.verify_code = verify_code;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
