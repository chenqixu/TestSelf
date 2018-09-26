package com.newland.bi.bigdata.cmd;

import org.apache.commons.lang3.StringUtils;

/**
 * TtBulkCpCS Command Tool
 * */
public class TtBulkCpCSCmd extends AbstractCmd {

	@Override
	protected void setBaseCmdStr() {
		this.BaseCmdStr = "ttBulkCpCSCmd";
	}
	
	@Override
	protected void initBcList() {
		this.bclist.add(TtBulkCpCSCopyIn.class);
		this.bclist.add(TtBulkCpCSCopyOut.class);
		this.bclist.add(TtBulkCpCSSeparator.class);
		this.bclist.add(TtBulkCpCSQuoted_strings.class);
		this.bclist.add(TtBulkCpCSTformat.class);
		this.bclist.add(TtBulkCpCSDSN.class);
		this.bclist.add(TtBulkCpCSTablename.class);
		this.bclist.add(TtBulkCpCSFilepath.class);
	}
	
	/**
	 * copy-in
	 * */
	class TtBulkCpCSCopyIn extends BaseCmd {
		@Override
		public String getKeyValue() {
			return "copy-in";
		}
		@Override
		public String getCmd() {
			if(StringUtils.isNoneBlank(param) && param.equals("1")){
				return " -i";
			}
			return "";
		}		
	}
	
	/**
	 * copy-out
	 * */
	class TtBulkCpCSCopyOut extends BaseCmd {
		@Override
		public String getKeyValue() {
			return "copy-out";
		}
		@Override
		public String getCmd() {
			if(StringUtils.isNoneBlank(param) && param.equals("1")){
				return " -o";
			}
			return "";
		}		
	}
	
	/**
	 * separator
	 * */
	class TtBulkCpCSSeparator extends BaseCmd {
		@Override
		public String getKeyValue() {
			return "separator";
		}
		@Override
		public String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " -s '"+param+"'";
			}
			return "";
		}		
	}
	
	/**
	 * quoted_strings
	 * */
	class TtBulkCpCSQuoted_strings extends BaseCmd {
		@Override
		public String getKeyValue() {
			return "quoted_strings";
		}
		@Override
		public String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " -Q "+param;
			}
			return "";
		}		
	}
	
	/**
	 * tformat
	 * */
	class TtBulkCpCSTformat extends BaseCmd {
		@Override
		public String getKeyValue() {
			return "tformat";
		}
		@Override
		public String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " -dformat '"+param+"'";
			}
			return "";
		}		
	}
	
	/**
	 * ttbulkcpcs_dsn
	 * */
	class TtBulkCpCSDSN extends BaseCmd {
		@Override
		public String getKeyValue() {
			return "ttbulkcpcs_dsn";
		}
		@Override
		public String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " DSN="+param;
			}
			return "";
		}		
	}
	
	/**
	 * ttbulkcpcs_tablename
	 * */
	class TtBulkCpCSTablename extends BaseCmd {
		@Override
		public String getKeyValue() {
			return "ttbulkcpcs_tablename";
		}
		@Override
		public String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " "+param;
			}
			return "";
		}		
	}
	
	/**
	 * ttbulkcpcs_filepath
	 * */
	class TtBulkCpCSFilepath extends BaseCmd {
		@Override
		public String getKeyValue() {
			return "ttbulkcpcs_filepath";
		}
		@Override
		public String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " "+param;
			}
			return "";
		}		
	}
}
