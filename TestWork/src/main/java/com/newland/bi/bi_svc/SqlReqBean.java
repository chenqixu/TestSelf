package com.newland.bi.bi_svc;

import java.util.Date;

/**
 * SqlReqBean
 *
 * @author chenqixu
 */
public class SqlReqBean {
    private Long   user_id;            // 工号
    private Long   home_city;          // 归属地市
    private Long   sys_id;             // 子系统ID
    private Long   priv_id;            // 功能ID
    private Long   parent_sub_id;      // 来源页面ID
    private String seq_id;             // 操作流水号
    private String verify_code;        // 验证码
    private Date opt_time;           // 操作时间
    private Long   req_resource = null; // 请求来源
    private Long   month_id;           // 月份
    private Long   login_seq_id;       // 登录流水id
    private String browser;            // 浏览器
    private Long   sys_resource;       // 门户来源
    private Date   last_activity_time; // 最后活动时间
    private String login_ip;           // 客户端主机ip
    private String login_mac_addr;     // 客户端主机的mac地址
    private String passwd;             // 登录密码
    private String error_msg;          // 误描述
    private Date   opt_end_time;       // 结束时间
    private String oldpwd;             // 旧密码 密码MD5串
    private String vaild_type;         // 校验方式
    private String main_id;            // 4A主账号
    private String nick_name;          // 别名
    private Long   role_id;            // 角色ID
    private Long   priv_type;          // 菜单类型

    public String getVaild_type() {
        return vaild_type;
    }

    public void setVaild_type(String vaild_type) {
        this.vaild_type = vaild_type;
    }

    public String getOldpwd() {
        return oldpwd;
    }

    public void setOldpwd(String oldpwd) {
        this.oldpwd = oldpwd;
    }

    public Date getOpt_end_time() {
        return opt_end_time;
    }

    public void setOpt_end_time(Date opt_end_time) {
        this.opt_end_time = opt_end_time;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Long getParent_sub_id() {
        return parent_sub_id;
    }

    public void setParent_sub_id(Long parent_sub_id) {
        this.parent_sub_id = parent_sub_id;
    }

    public String getSeq_id() {
        return seq_id;
    }

    public void setSeq_id(String seq_id) {
        this.seq_id = seq_id;
    }

    public String getVerify_code() {
        return verify_code;
    }

    public void setVerify_code(String verify_code) {
        this.verify_code = verify_code;
    }

    public Date getOpt_time() {
        return opt_time;
    }

    public void setOpt_time(Date opt_time) {
        this.opt_time = opt_time;
    }

    public Long getReq_resource() {
        return req_resource;
    }

    public void setReq_resource(Long req_resource) {
        this.req_resource = req_resource;
    }

    public Long getMonth_id() {
        return month_id;
    }

    public void setMonth_id(Long month_id) {
        this.month_id = month_id;
    }

    public Long getLogin_seq_id() {
        return login_seq_id;
    }

    public void setLogin_seq_id(Long login_seq_id) {
        this.login_seq_id = login_seq_id;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public Long getSys_resource() {
        return sys_resource;
    }

    public void setSys_resource(Long sys_resource) {
        this.sys_resource = sys_resource;
    }

    public Date getLast_activity_time() {
        return last_activity_time;
    }

    public void setLast_activity_time(Date last_activity_time) {
        this.last_activity_time = last_activity_time;
    }

    public String getLogin_ip() {
        return login_ip;
    }

    public void setLogin_ip(String login_ip) {
        this.login_ip = login_ip;
    }

    public String getLogin_mac_addr() {
        return login_mac_addr;
    }

    public void setLogin_mac_addr(String login_mac_addr) {
        this.login_mac_addr = login_mac_addr;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getHome_city() {
        return home_city;
    }

    public void setHome_city(Long home_city) {
        this.home_city = home_city;
    }

    public Long getSys_id() {
        return sys_id;
    }

    public void setSys_id(Long sys_id) {
        this.sys_id = sys_id;
    }

    public Long getPriv_id() {
        return priv_id;
    }

    public void setPriv_id(Long priv_id) {
        this.priv_id = priv_id;
    }

    public String getMain_id() {
        return main_id;
    }

    public void setMain_id(String main_id) {
        this.main_id = main_id;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public Long getRole_id() {
        return role_id;
    }

    public void setRole_id(Long role_id) {
        this.role_id = role_id;
    }

    public Long getPriv_type() {
        return priv_type;
    }

    public void setPriv_type(Long priv_type) {
        this.priv_type = priv_type;
    }
}
