package com.newland.bi.bigdata.cmd;

/**
 * Command interface</p>
 * <pre>
 * Need to set the keyvalue method.
 * Need to achieve access command method
 * </pre>
 * */
public abstract class BaseCmd {
	protected String param;
	
	abstract String getKeyValue();
	
	abstract String getCmd();
	
	protected void setValue(String arg) {
		this.param = arg;
	}
}
