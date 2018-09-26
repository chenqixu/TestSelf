package com.newland.bi.bigdata.cmd;

import org.apache.commons.lang3.StringUtils;

public class SqoopExpCmd extends AbstractCmd {

	@Override
	protected void setBaseCmdStr() {
		this.BaseCmdStr = "sqoop";
	}

	@Override
	protected void initBcList() {
		this.bclist.add(SqoopExpConfig.class);
		this.bclist.add(SqoopExpExport.class);
		this.bclist.add(SqoopExpBdocAccessId.class);
		this.bclist.add(SqoopExpBdocAccessKey.class);
		this.bclist.add(SqoopExpJobQueuename.class);
		this.bclist.add(SqoopExpConnect.class);
		this.bclist.add(SqoopExpUsername.class);
		this.bclist.add(SqoopExpPassword.class);
		this.bclist.add(SqoopExpParallel.class);
		this.bclist.add(SqoopExpTable.class);
		this.bclist.add(SqoopExpExportDir.class);
		this.bclist.add(SqoopExpInputFieldsTerminatedBy.class);
		this.bclist.add(SqoopExpStagingTable.class);
		this.bclist.add(SqoopExpNullString.class);
		this.bclist.add(SqoopExpNullNonString.class);
	}
	
	/**
	 * config
	 * */
	class SqoopExpConfig extends BaseCmd {
		@Override
		String getKeyValue() {
			return "config";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " --config "+param+" ";
			}
			return "";
		}
	}
	
	/**
	 * export
	 * */
	class SqoopExpExport extends BaseCmd {
		@Override
		String getKeyValue() {
			return "export";
		}
		@Override
		String getCmd() {
			return " export ";
		}
	}
	
	/**
	 * bdoc_access_id
	 * */
	class SqoopExpBdocAccessId extends BaseCmd {
		@Override
		String getKeyValue() {
			return "bdoc_access_id";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " -Dhadoop.security.bdoc.access.id="+param+" ";
			}
			return "";
		}
	}
	
	/**
	 * bdoc_access_key
	 * */
	class SqoopExpBdocAccessKey extends BaseCmd {
		@Override
		String getKeyValue() {
			return "bdoc_access_key";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " -Dhadoop.security.bdoc.access.key="+param+" ";
			}
			return "";
		}
	}
	
	/**
	 * job_queuename
	 * */
	class SqoopExpJobQueuename extends BaseCmd {
		@Override
		String getKeyValue() {
			return "job_queuename";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " -Dmapreduce.job.queuename="+param+" ";
			}
			return "";
		}
	}

	/**
	 * connect
	 * */
	class SqoopExpConnect extends BaseCmd {
		@Override
		String getKeyValue() {
			return "connect";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				param = param.replace(" ", "");
				param = param.replace("(", "\\(");
				param = param.replace(")", "\\)");
				return " --connect "+param+" ";
			}
			return "";
		}		
	}
	
	/**
	 * username
	 * */
	class SqoopExpUsername extends BaseCmd {
		@Override
		String getKeyValue() {
			return "username";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " --username "+param+" ";
			}
			return "";
		}
	}
	
	/**
	 * password
	 * */
	class SqoopExpPassword extends BaseCmd {
		@Override
		String getKeyValue() {
			return "password";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " --password "+param+" ";
			}
			return "";
		}
	}
	
	/**
	 * parallel
	 * */
	class SqoopExpParallel extends BaseCmd {
		@Override
		String getKeyValue() {
			return "parallel";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " -m "+param+" ";
			}
			return "";
		}
	}
	
	/**
	 * table
	 * */
	class SqoopExpTable extends BaseCmd {
		@Override
		String getKeyValue() {
			return "table";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " --table "+param.toUpperCase()+" ";
			}
			return "";
		}
	}
	
	/**
	 * export-dir
	 * */
	class SqoopExpExportDir extends BaseCmd {
		@Override
		String getKeyValue() {
			return "export-dir";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " --export-dir '" + param + "' ";
			}
			return "";
		}
	}
	
	/**
	 * input-fields-terminated-by
	 * */
	class SqoopExpInputFieldsTerminatedBy extends BaseCmd {
		@Override
		String getKeyValue() {
			return "input-fields-terminated-by";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				if(param.indexOf("|")>0) {
					param = param.replace("\\", "");// Remove the escape character.
				}
				return " --input-fields-terminated-by  '" + param + "' ";
			}
			return "";
		}
	}
	
	/**
	 * staging-table
	 * */
	class SqoopExpStagingTable extends BaseCmd {
		@Override
		String getKeyValue() {
			return "staging-table";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " --staging-table " + param + " --clear-staging-table" + " ";
			}
			return "";
		}
	}
	
	/**
	 * null-string : String null substitution
	 * */
	class SqoopExpNullString extends BaseCmd {
		@Override
		String getKeyValue() {
			return "null-string";
		}
		@Override
		String getCmd() {
			return " --null-string '' ";
		}
	}
	
	/**
	 * null-non-string : Not string type null value substitution
	 * */
	class SqoopExpNullNonString extends BaseCmd {
		@Override
		String getKeyValue() {
			return "null-non-string";
		}
		@Override
		String getCmd() {
			return " --null-non-string '' ";
		}
	}
}
