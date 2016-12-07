package com.newland.bi.netverify.action;

import java.io.PrintWriter;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Scope("prototype")
@Controller
@RequestMapping(value = "/mvc")
public class CoffersApproveController {
	@RequestMapping(value = "/hello")
	public String hello(){
		return "hello";
	}
	
	@RequestMapping(value = "/doCBAuth")
	public void doCBAuth(String reqBean, PrintWriter pw) {
		System.out.println("[doCBAuth.reqBean]"+reqBean);
		pw.write("hello,"+reqBean);
	}
}
