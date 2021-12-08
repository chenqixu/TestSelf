package com.cqx.common.utils.http.auth;

import com.cqx.common.bean.http.ResponseMessage;
import com.sun.jersey.api.client.ClientResponse;
import org.apache.http.client.HttpResponseException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RangerUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(RangerUtilTest.class);
    private RangerUtil rangerUtil;
    private HttpManager httpManager;

    @Before
    public void setUp() throws Exception {
        rangerUtil = new RangerUtil("admin", "123!!Qwe");
        httpManager = new HttpManager();
    }

    @Test
    public void get() throws HttpResponseException {
        ClientResponse clientResponse = rangerUtil.get("https://10.1.12.80:20026/Yarn/ResourceManager/190/cluster/");
        ResponseMessage<String> getResponseMessage = httpManager.handleClientResponse(clientResponse, String.class);
        logger.info("status={}，body={}", getResponseMessage.getStatus(), getResponseMessage.getBody());
    }

    @Test
    public void delete() {
    }

    @Test
    public void download() {
    }

    @Test
    public void accept() {
    }
}