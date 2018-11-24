package com.cqx.netty.client;

import com.cqx.netty.bean.DiscardBean;
import com.cqx.netty.util.Utils;

public class HeartTest {

	public static void main(String[] args) throws Exception {
		DiscardBean discardBean = new DiscardBean(1, 1234567l, DiscardBean.buildDataBean("testThread1", 1, Utils.getNow()));
		new DiscardClient("192.168.230.128", 18888).heart(discardBean);
	}
}
