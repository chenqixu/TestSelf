package com.cqx.bean;

public class RestParam {
	/**
	 * 请求url路径
	 */
	private String reqUrl;
	/**
	 * 请求类型"POST"或"GET"
	 */
	private String reqMethod;
	/**
	 * 请求内容
	 */
	private String reqContent;
	/**
	 * 请求模式"https"或"http"(默认http)
	 */
	private String reqHttpsModel;
	/**
	 * 该值为空时不设置，不为空时设置
	 */
	private String sessionId;
	/**
	 * 是否开启代理模式（默认FALSE）"TRUE":开启;"FALSE":不开启;设为TRUE时需要配置以下几项参数
	 */
	private String ifProxy;
	/**
	 * 代理地址
	 */
	private String proxyAddress;
	/**
	 * 代理端口
	 */
	private String proxyPort;
	/**
	 * 代理账号
	 */
	private String proxyUser;
	/**
	 * 代理密码
	 */
	private String proxyPassWord;

	public RestParam(String reqUrl,String reqMethod,String reqContent,
			String reqHttpsModel,String sessionId,String ifProxy,String proxyAddress,
			String proxyPort,String proxyUser,String proxyPassWord) {
		this.reqUrl = reqUrl;
		this.reqMethod = reqMethod;
		this.reqContent = reqContent;
		this.reqHttpsModel = reqHttpsModel;
		this.sessionId = sessionId;
		this.ifProxy = ifProxy;
		this.proxyAddress = proxyAddress;
		this.proxyPort = proxyPort;
		this.proxyUser = proxyUser;
		this.proxyPassWord = proxyPassWord;
	}

	public String getReqUrl() {
		return reqUrl;
	}

	public void setReqUrl(String reqUrl) {
		this.reqUrl = reqUrl;
	}

	public String getReqMethod() {
		return reqMethod;
	}

	public void setReqMethod(String reqMethod) {
		this.reqMethod = reqMethod;
	}

	public String getReqContent() {
		return reqContent;
	}

	public void setReqContent(String reqContent) {
		this.reqContent = reqContent;
	}

	public String getReqHttpsModel() {
		return reqHttpsModel;
	}

	public void setReqHttpsModel(String reqHttpsModel) {
		this.reqHttpsModel = reqHttpsModel;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getIfProxy() {
		return ifProxy;
	}

	public void setIfProxy(String ifProxy) {
		this.ifProxy = ifProxy;
	}

	public String getProxyAddress() {
		return proxyAddress;
	}

	public void setProxyAddress(String proxyAddress) {
		this.proxyAddress = proxyAddress;
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUser() {
		return proxyUser;
	}

	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	public String getProxyPassWord() {
		return proxyPassWord;
	}

	public void setProxyPassWord(String proxyPassWord) {
		this.proxyPassWord = proxyPassWord;
	}
}
