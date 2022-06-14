package com.bussiness.bi.bigdata.cmd;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class SqoopImpCmd extends AbstractCmd {
	// List of exported file formats supported by Sqoop
    private static final List<String> FILE_TYPES = Arrays.asList(
    		new String[]{"TEXT", "SEQUENCE", "PARQUET"});
    
	@Override
	protected void setBaseCmdStr() {
		this.BaseCmdStr = "sqoop";
	}

	@Override
	protected void initBcList() {
		this.bclist.add(SqoopImpConfig.class);
		this.bclist.add(SqoopImpImport.class);
		this.bclist.add(SqoopImpBdocAccessId.class);
		this.bclist.add(SqoopImpBdocAccessKey.class);
		this.bclist.add(SqoopImpJobQueuename.class);
		this.bclist.add(SqoopImpConnect.class);
		this.bclist.add(SqoopImpUsername.class);
		this.bclist.add(SqoopImpPassword.class);
		this.bclist.add(SqoopImpParallel.class);
		this.bclist.add(SqoopImpSplitBy.class);
		this.bclist.add(SqoopImpNormalRunmodel.class);
		this.bclist.add(SqoopImpImportantRunmodel.class);
		this.bclist.add(SqoopImpTargetDir.class);
		this.bclist.add(SqoopImpFieldsTerminatedBy.class);
		this.bclist.add(SqoopImpFileType.class);
		this.bclist.add(SqoopImpCompress.class);
		this.bclist.add(SqoopImpDeleteTargetDir.class);
		this.bclist.add(SqoopImpNullString.class);
		this.bclist.add(SqoopImpNullNonString.class);
	}
	
	/**
	 * config
	 * */
	class SqoopImpConfig extends BaseCmd {
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
	 * import
	 * */
	class SqoopImpImport extends BaseCmd {
		@Override
		String getKeyValue() {
			return "import";
		}
		@Override
		String getCmd() {
			return " import ";
		}
	}
	
	/**
	 * bdoc_access_id
	 * */
	class SqoopImpBdocAccessId extends BaseCmd {
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
	class SqoopImpBdocAccessKey extends BaseCmd {
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
	class SqoopImpJobQueuename extends BaseCmd {
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
	class SqoopImpConnect extends BaseCmd {
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
	class SqoopImpUsername extends BaseCmd {
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
	class SqoopImpPassword extends BaseCmd {
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
	class SqoopImpParallel extends BaseCmd {
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
	 * split-by
	 * */
	class SqoopImpSplitBy extends BaseCmd {
		@Override
		String getKeyValue() {
			return "split-by";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " --split-by '"+param+"' ";
			}
			return "";
		}
	}
	
	/**
	 * normal-runmodel
	 * */
	class SqoopImpNormalRunmodel extends BaseCmd {
		@Override
		String getKeyValue() {
			return "normal-runmodel";
		}
		@Override
		String getCmd() {
			// add child command
			childAddOptions(this, new SqoopImpTable());
			childAddOptions(this, new SqoopImpColumns());
			childAddOptions(this, new SqoopImpWhere());
			return "";
		}		
	}
	
	/**
	 * table
	 * */
	class SqoopImpTable extends BaseCmd {
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
	 * columns
	 * */
	class SqoopImpColumns extends BaseCmd {
		@Override
		String getKeyValue() {
			return "columns";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " --columns '"+param.toUpperCase()+"' ";
			}
			return "";
		}
	}
	
	/**
	 * where
	 * */
	class SqoopImpWhere extends BaseCmd {
		@Override
		String getKeyValue() {
			return "where";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " --where '"+param.toUpperCase()+"' ";
			}
			return "";
		}
	}
	
	/**
	 * important-runmodel
	 * */
	class SqoopImpImportantRunmodel extends BaseCmd {
		@Override
		String getKeyValue() {
			return "important-runmodel";
		}
		@Override
		String getCmd() {
			// add child command
			childAddOptions(this, new SqoopImpQuery());
			return "";
		}		
	}
	
	/**
	 * query
	 * */
	class SqoopImpQuery extends BaseCmd {
		@Override
		String getKeyValue() {
			return "query";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				if(param.toLowerCase().indexOf("where")>0){// The SQL statement contains where.
					return " --query '" + param + " AND $CONDITIONS' ";
				}else{
					return " --query '" + param + " $CONDITIONS' ";
				}
			}
			return "";
		}
	}
	
	/**
	 * target-dir
	 * */
	class SqoopImpTargetDir extends BaseCmd {
		@Override
		String getKeyValue() {
			return "target-dir";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " --target-dir '"+param+"' ";
			}
			return "";
		}
	}
	
	/**
	 * fields-terminated-by
	 * */
	class SqoopImpFieldsTerminatedBy extends BaseCmd {
		@Override
		String getKeyValue() {
			return "fields-terminated-by";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				return " --fields-terminated-by '"+param+"' ";
			}
			return "";
		}
	}
	
	/**
	 * file-type
	 * */
	class SqoopImpFileType extends BaseCmd {
		@Override
		String getKeyValue() {
			return "file-type";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param)){
				if(FILE_TYPES.contains(param)){
					return " --as-" + param.toLowerCase() + "file ";
				}else{
					return " --as-textfile ";
				}
			}
			return "";
		}
	}
	
	/**
	 * compress
	 * */
	class SqoopImpCompress extends BaseCmd {
		@Override
		String getKeyValue() {
			return "compress";
		}
		@Override
		String getCmd() {
			if(StringUtils.isNoneBlank(param) && param.equals("yes")){
				return " --compress ";
			}
			return "";
		}
	}
	
	/**
	 * delete-target-dir
	 * */
	class SqoopImpDeleteTargetDir extends BaseCmd {
		@Override
		String getKeyValue() {
			return "delete-target-dir";
		}
		@Override
		String getCmd() {
			return " --delete-target-dir ";
		}
	}
	
	/**
	 * null-string : String null substitution
	 * */
	class SqoopImpNullString extends BaseCmd {
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
	class SqoopImpNullNonString extends BaseCmd {
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
