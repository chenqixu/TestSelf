package com.cqx.common.exception.other;

/**
 * Base exception for all nl-component-FujianBI exception. <p/>
 * 
 * @author chenqixu
 * */
public class FujianBIException extends Exception {
	private static final long serialVersionUID = 3049811133869993965L;

    /**
     * Create an FujianBIException from an error code plus parameter to create the exception message. <p/> The value of {@link
     * ErrorCode#getTemplate} is used as a StringFormat template for the exception message. <p/>
     *
     * @param errorCode the error code for the exception.
     * @param cause cause of error
     * @param params parameters used to create the exception message together with the error code template. 
     */
	public FujianBIException(ErrorCode errorCode, Throwable cause, String... params){
		super(errorCode.format(params), cause);
	}
}
