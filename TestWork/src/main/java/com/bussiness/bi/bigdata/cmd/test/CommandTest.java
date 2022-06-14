package com.bussiness.bi.bigdata.cmd.test;

import java.util.HashMap;
import java.util.Map;

import com.bussiness.bi.bigdata.cmd.SqoopExpCmd;
import com.bussiness.bi.bigdata.cmd.SqoopImpCmd;
import com.bussiness.bi.bigdata.cmd.TtBulkCpCSCmd;

/**
 * Sqoop Test
 * */
public class CommandTest extends AbstractCommandTest {
	
	/**
	 * Sqoop Exp
	 * */
	public String Exp() {
		SqoopExpCmd sec = new SqoopExpCmd();
		Map<String, String> args = new HashMap<String, String>();
		args.put("config", "/etc/hadoop/conf");
		args.put("export", "");
//		args.put("bdoc_access_id", "12345");
//		args.put("bdoc_access_key", "abcde");
//		args.put("job_queuename", "dev10");
		args.put("connect", "jdbc:oracle:thin:@10.1.8.79:1521/edc_cfg_pri");
		args.put("username", "edc_etl_col");
		args.put("password", "edc_etl_col");
		args.put("parallel", "4");
		args.put("table", "dim_home_area");
		args.put("export-dir", "/test/tmp1");
		args.put("input-fields-terminated-by", ",");
		args.put("staging-table", "tmptest1");
		return sec.getCommand(args);
	}
	
	/**
	 * Sqoop Imp
	 * */
	public String Imp() {
		SqoopImpCmd sic = new SqoopImpCmd();
		Map<String, String> args = new HashMap<String, String>();
		args.put("config", "/etc/hadoop/conf");
		args.put("import", "");
//		args.put("bdoc_access_id", "12345");
//		args.put("bdoc_access_key", "abcde");
//		args.put("job_queuename", "dev10");
		args.put("connect", "jdbc:oracle:thin:@10.1.8.79:1521/edc_cfg_pri");
		args.put("username", "edc_etl_col");
		args.put("password", "edc_etl_col");
		args.put("parallel", "4");
		args.put("split-by", ",");
//		args.put("normal-runmodel", "");
//		args.put("table", "dim_home_area");
//		args.put("columns", "id,name");
//		args.put("where", "id=123");
		args.put("important-runmodel", "");
		args.put("query", "id=123");
		args.put("target-dir", "/test/tmp1");
		args.put("fields-terminated-by", "|");
		args.put("file-type", "");
		args.put("compress", "");
		return sic.getCommand(args);
	}
	
	public String bulkCpCs() {
		TtBulkCpCSCmd tb = new TtBulkCpCSCmd();
		Map<String, String> args = new HashMap<String, String>();
		args.put("copy-in", "1");
		args.put("ttbulkcpcs_dsn", "tt_1122");
		args.put("ttbulkcpcs_tablename", "realtime_status_snapshot");
		args.put("ttbulkcpcs_filepath", "/cqx/data/1.dat");
		return tb.getCommand(args);
	}
	
	/**
	 * Inner Class
	 * */
	class SqoopTestCs {}
	
	public static void main(String[] args) {
		CommandTest st = new CommandTest();
		System.out.println(st.Exp());
		System.out.println(st.Imp());
		System.out.println(st.bulkCpCs());
	}
}
