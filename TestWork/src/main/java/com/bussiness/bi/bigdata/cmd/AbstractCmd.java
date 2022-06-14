package com.bussiness.bi.bigdata.cmd;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author chenqixu
 * */
public abstract class AbstractCmd {
	protected String BaseCmdStr;
	protected List<Class<? extends BaseCmd>> bclist = new ArrayList<Class<? extends BaseCmd>>();
	
	/**
	 * parameters, orderly
	 * */
	protected final List<BaseCmd> options = new CopyOnWriteArrayList<BaseCmd>();
	
	/**
	 * constructor.
	 * */
	public AbstractCmd() {
		setBaseCmdStr();
		initBcList();
		initBaseCmdClass();
	}
	
	/**
	 * set BaseCmdStr
	 * */
	protected abstract void setBaseCmdStr();
	
	/**
	 * init bclist.
	 * */
	protected abstract void initBcList();
	
	/**
	 * Initialization BaseCmdClass
	 * <pre>
	 * Step 1: structure
	 * Step 2: set keywords and Instances
	 * </pre>
	 * */
	private void initBaseCmdClass() {
		if(bclist !=null)  {
			for(Class<? extends BaseCmd> bc : bclist) {
				try {
					Constructor<?> constructor = bc.getDeclaredConstructor(this.getClass());
					BaseCmd bcresult = (BaseCmd) constructor.newInstance(this);					
//					BaseCmd bcresult = bc.newInstance();// structure
					if(bcresult !=null) {
//						options.put(bcresult.getKeyValue(), bcresult);// set keywords and Instances
						options.add(bcresult);// add Instances
					}else{
						// throws Exception
					}
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Setting parameters to basic commands
	 * */
	protected void setBaseCmd(BaseCmd bc, String arg) {
		if(bc != null)
			bc.setValue(arg);
	}
	
	/**
	 *  Add child elements to the location of the parent element.
	 * */
	protected void childAddOptions(BaseCmd parent, BaseCmd child) {
		this.options.add(this.options.indexOf(parent), child);
	}
	
	/**
	 * Initialize command line by name and value
	 * */
	public String getCommand(Map<String, String> args) {
		StringBuilder sb = new StringBuilder();
		sb.append(BaseCmdStr);
		
//		for(Map.Entry<String, BaseCmd> me : options.entrySet()) {
//			// Set value
//			setBaseCmd(me.getValue(), args.get(me.getKey()));
//			// Get command to splice
//			sb.append(me.getValue().getCmd());
//		}
		Iterator<BaseCmd> it = options.iterator();
		while(it.hasNext()) {
			BaseCmd currentBc = it.next();
			// Set value
			setBaseCmd(currentBc, args.get(currentBc.getKeyValue()));
			// Get command to splice
			sb.append(currentBc.getCmd());
			// Removes the current element and refreshes the processing queue.
			options.remove(currentBc);
			it = options.iterator();
		}
		
		return sb.toString();
	}
}
