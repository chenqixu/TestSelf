
package com.gary.test.ws.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.spring.test.bean.GreetingRequest;
import com.spring.test.bean.GreetingResponse;

@WebService(name = "GreetingService", targetNamespace = "http://service.ws.test.gary.com/")
@SOAPBinding(use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface GreetingService {

	@WebMethod(operationName = "greeting", action = "")
	@WebResult(name = "return", targetNamespace = "")
	public String greeting(@WebParam(name = "arg0", targetNamespace = "")
	String arg0);

	@WebMethod(operationName = "next", action = "")
	@WebResult(name = "return", targetNamespace = "")
	public String next(@WebParam(name = "arg0", targetNamespace = "")
	String arg0);

	public @WebResult(name = "message")GreetingResponse qry(@WebParam(name = "message")GreetingRequest requestObj);
}
