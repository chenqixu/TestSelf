package com.spring.test.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * ��Servlet֧��springע�뷽ʽʹ��Spring Bean
 * */
public class SpringSupportServlet extends HttpServlet {

	private static final long serialVersionUID = 3572281877655980104L;

	/**
	 * @throws ServletException x
	 */
	public void init() throws ServletException {
		super.init();
		WebApplicationContextUtils
				.getWebApplicationContext(getServletContext())
				.getAutowireCapableBeanFactory().autowireBean(this);
	}
}
