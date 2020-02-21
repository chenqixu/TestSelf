package com.cqx.jerseyclient;

//import org.apache.commons.lang3.StringUtils;

import com.cqx.bean.ResultBean;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.lang.StringUtils;

public class ClientFactory {
	protected Client client;
	protected WebResource webResource;
	protected ClientResponse response;	
	private static ClientFactory ac = new ClientFactory();
	
	private ClientFactory() {
		client = Client.create();
	}
	
	public static ClientFactory getInstance() {		
		return ac!=null ? ac : new ClientFactory();
	}

	public <T> ResultBean<T> callGetBean(String url, String MediaType, Class<T> tClass) {
		ResultBean<T> result = null;
		if(StringUtils.isNotBlank(url)) {
			webResource = client.resource(url);
			if(StringUtils.isNotBlank(MediaType))
				response = webResource.accept(MediaType).get(ClientResponse.class);
			else
				response = webResource.get(ClientResponse.class);
			result = new ResultBean<T>();
			result.setStatus(response.getStatus());
			result.setT(response.getEntity(tClass));
		}
		return result;
	}
	
	public ResultBean<String> callGetBean(String url, String MediaType) {
		return callGetBean(url, MediaType, String.class);
	}
	
	public ResultBean<String> callGetBean(String url) {
		return callGetBean(url, null, String.class);
	}
	
	public <T> T call(String url, String MediaType, Class<T> tClass) {
		T result = null;
		if(StringUtils.isNotBlank(url)) {
			webResource = client.resource(url);
			if(StringUtils.isNotBlank(MediaType))
				response = webResource.accept(MediaType).get(ClientResponse.class);
			else
				response = webResource.get(ClientResponse.class);
			result = response.getEntity(tClass);
		}
		return result;
	}
	
	public String call(String url, String MediaType) {
		return call(url, MediaType, String.class);
	}
	
	public String call(String url) {
		return call(url, null, String.class);
	}
}
