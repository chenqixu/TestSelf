package com.cqx.common.utils.ftp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FtpBean
 *
 * @author chenqixu
 */
public class FtpBean {
    private String name;
    private String user_name;
    private String pass_word;
    private String host;
    private int port;
    private FtpTypeEnum type;

    public static FtpBean newbuilder() {
        return new FtpBean();
    }

    public static List<FtpBean> parser(Object param) {
        List<Map<String, ?>> parser = (ArrayList<Map<String, ?>>) param;
        List<FtpBean> result = new ArrayList<>();
        for (Map<String, ?> map : parser) {
            result.add(FtpBean.newbuilder().parserMap(map));
        }
        return result;
    }

    public FtpBean parserMap(Map<String, ?> param) {
        // 解析参数
        setName((String) param.get("name"));
        setUser_name((String) param.get("user_name"));
        setPass_word((String) param.get("pass_word"));
        setHost((String) param.get("host"));
        setPort(((Number) param.get("port")).intValue());
        // type
        String _type_Str = (String) param.get("type");
        if (_type_Str == null) {
            // 默认ftp
            setType(FtpTypeEnum.FTP);
        } else {
            setType(FtpTypeEnum.valueOf(_type_Str));
        }
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPass_word() {
        return pass_word;
    }

    public void setPass_word(String pass_word) {
        this.pass_word = pass_word;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public FtpTypeEnum getType() {
        return type;
    }

    public void setType(FtpTypeEnum type) {
        this.type = type;
    }
}
