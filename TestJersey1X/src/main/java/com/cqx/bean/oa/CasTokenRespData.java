package com.cqx.bean.oa;

/**
 * CasTokenRespData
 *
 * @author chenqixu
 */
public class CasTokenRespData {
    private String access_token;
    private String refresh_token;
    private CasTokenUserInfo user_info;
    private String scope;
    private String token_type;
    private int expires_in;
    private String jti;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public CasTokenUserInfo getUser_info() {
        return user_info;
    }

    public void setUser_info(CasTokenUserInfo user_info) {
        this.user_info = user_info;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }
}
