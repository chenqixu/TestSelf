package com.cqx.common.utils.http;

import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.FileNotFoundException;

public class JavaScriptUtilTest {
    private JavaScriptUtil javaScriptUtil;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void exec() throws ScriptException, FileNotFoundException {
        javaScriptUtil = new JavaScriptUtil("d:\\tmp\\html\\oa\\js\\", "md5");
        javaScriptUtil.exec("var md5Pwd = hex_md5('12345674a')", "md5Pwd");
        System.out.println(System.currentTimeMillis() / 1000);
        // newdoValidate，获取casKey
        // url:'https://10.1.4.252/nloa/baseData.do?action=newDoValidate&sasVea='+document.getElementById("username").value+'&sacVeb='+tempPwd+'&_t'+(Date.parse(new Date()) / 1000)

        // 跳转到NL开发云平台
        // var encodeKey = encodeURIComponent(casKey);
        // var encodeUrl = encodeURIComponent(window.location.href);
        // var agent = navigator.userAgent.toLowerCase();
        // http://10.1.4.79:8080/casAuth?casKey=PPkun60rO5fDMw0LS3PTUHJoLWt4auz297sATLpJTKM=&casUrl=https://10.1.4.252/cas/login?act=login

        //
    }
}