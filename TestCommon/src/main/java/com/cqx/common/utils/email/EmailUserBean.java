package com.cqx.common.utils.email;

/**
 * EmailUserBean
 *
 * @author chenqixu
 */
public class EmailUserBean {
    private String account;
    private String password;
    private String personal;

    public EmailUserBean() {
    }

    public EmailUserBean(String account) {
        this(account, null);
    }

    public EmailUserBean(String account, String password) {
        this.account = account;
        this.password = password;
        this.personal = account.split("@", -1)[0];
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPersonal() {
        return personal;
    }

    public void setPersonal(String personal) {
        this.personal = personal;
    }
}
