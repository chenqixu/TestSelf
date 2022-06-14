package com.bussiness.bi.bigdata.cmd.test;

public class T1 extends TF {

	@Override
	protected void setBaseCmdStr() {
		this.BaseCmdStr = "t1-basecmd";
	}

	@Override
	protected void initBcList() {
//		this.options.put("1", new T1Cs1Cmd());
//		this.options.put("2", new T1Cs2Cmd());
//		this.options.put("3", new T1Cs3Cmd());
		this.options.add(new T1Cs1Cmd());
		this.options.add(new T1Cs2Cmd());
		this.options.add(new T1Cs3Cmd());
	}
	
	void childAddOptions(BaseT parent, BaseT child) {
		int parentIndex = this.options.indexOf(parent);
		this.options.add(parentIndex, child);
	}
	
	class T1Cs1Cmd extends BaseT {
	} 
	
	class T1Cs2Cmd extends BaseT {
		@Override
		protected void done() {
			childAddOptions(this, new T1Cs4Cmd());
		}
	}
	
	class T1Cs3Cmd extends BaseT {
	}
	
	class T1Cs4Cmd extends BaseT {
	}
	
}
