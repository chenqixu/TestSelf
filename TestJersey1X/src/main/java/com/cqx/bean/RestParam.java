package com.cqx.bean;

public class RestParam {
	/**
	 * ����url·��
	 */
	private String reqUrl;
	/**
	 * ��������"POST"��"GET"
	 */
	private String reqMethod;
	/**
	 * ��������
	 */
	private String reqContent;
	/**
	 * ����ģʽ"https"��"http"(Ĭ��http)
	 */
	private String reqHttpsModel;
	/**
	 * ��ֵΪ��ʱ�����ã���Ϊ��ʱ����
	 */
	private String sessionId;
	/**
	 * �Ƿ�������ģʽ��Ĭ��FALSE��"TRUE":����;"FALSE":������;��ΪTRUEʱ��Ҫ�������¼������
	 */
	private String ifProxy;
	/**
	 * �����ַ
	 */
	private String proxyAddress;
	/**
	 * ����˿�
	 */
	private String proxyPort;
	/**
	 * �����˺�
	 */
	private String proxyUser;
	/**
	 * ��������
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
