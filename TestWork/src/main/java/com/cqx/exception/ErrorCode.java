package com.cqx.exception;

/**
 * Definition of custom component error code
 * 
 * @author chenqixu
 * */
public enum ErrorCode {
	FJBIE000("Getting the {0} database configuration failure from the connection management, {code}"),
	FJBIE001("No input file from {0}, {code}"),
	FJBIE002("Download {0} File errors from the {1} HDFS cluster, {code}"),
	FJBIE003("Local temporary directory {0} creation failure, {code}"),
	FJBIE004("Command {0} failed to run. Check the log in case, {code}"),
	FJBIE005("Required parameters {0} are empty from the connection management, {code}"),
	FJBIE006("The separators are exceptions, the specific exception content is {0}, {code}"),
	FJBIE007("Getting the {0} Hadoop configuration failure from the connection management, {code}"),
	FJBIE008("Getting the {0} source database connection failure, {code}"),
	FJBIE009("Cleaning table data failure, sql is {0}, {code}"),
	FJBIE010("move table is failure, sql is {0}, {code}"),
	;
	
	private String template;
	
    /**
     * Return the message (StringFormat) template for the error code.
     *
     * @return message template.
     */
    public String getTemplate() {
        return template;
    }
    
    /**
     * Return a templatized error message for the error code.
     *
     * @param args the parameters for the templatized message.
     * @return error message.
     */
    public String format(String... args) {
    	this.template = this.template.replace("{code}", toString());
    	for(int i=0; i<args.length; i++) {
    		this.template = this.template.replace("{"+i+"}", args[i]);
    	}
    	return this.template;
    }
    
    /**
     * Create an error code.
     *
     * @param template template for the exception message.
     */
	private ErrorCode(String template) {
		this.template = template;
	}
}
