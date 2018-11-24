package com.cqx.netty.client;

import com.cqx.netty.bean.ClientQueryBean;
import com.cqx.netty.bean.DiscardBean;

public class QueryTest {

	public static void main(String[] args) throws Exception {
		DiscardBean discardBean = new DiscardBean(3, 1234567l, DiscardBean.buildNullDataBean());
		ClientQueryBean clientQueryBean = new ClientQueryBean();
		new DiscardClient("192.168.230.128", 18888).query(discardBean, clientQueryBean);
		clientQueryBean.query();
	}
}
