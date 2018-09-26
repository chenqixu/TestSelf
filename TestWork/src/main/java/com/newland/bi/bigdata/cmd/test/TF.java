package com.newland.bi.bigdata.cmd.test;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class TF {
	protected String BaseCmdStr = "";
	
	protected List<BaseT> options = new CopyOnWriteArrayList<BaseT>();
	
	public TF(){
		setBaseCmdStr();
		initBcList();
		System.out.println("TF init");
	}
	
	protected abstract void setBaseCmdStr();
	
	protected abstract void initBcList();
	
	public String getBaseCmdStr() {
		return this.BaseCmdStr;
	}
	
	public void getCommand() {
//		for(BaseT bt : options) {
////		for(Map.Entry<String, BaseT> me : options.entrySet()) {
////			System.out.println(me.getKey());
////			me.getValue().done();
//			bt.done();
//		}
		Iterator<BaseT> it = options.iterator();
		while(it.hasNext()) {
			BaseT bt = it.next();
			bt.done();
			options.remove(bt);
			it = options.iterator();
		}
	}
}
