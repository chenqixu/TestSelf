package com.cqx.security;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class KerberosUtilTest {

    private KerberosUtil kerberosUtil;
    private String user;
    private String path;

    @Before
    public void setUp() throws Exception {
        user = "test";
        path = "/tmp";
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void loginUserFromKeytab() {
        KerberosUtil.loginUserFromKeytab(user, path);
    }
}