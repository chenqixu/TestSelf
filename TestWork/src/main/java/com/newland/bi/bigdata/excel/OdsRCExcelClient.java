package com.newland.bi.bigdata.excel;

import com.cqx.common.utils.excel.ExcelSheetList;
import com.cqx.common.utils.excel.ExcelUtils;
import com.cqx.common.utils.excel.ViewBean;
import com.cqx.common.utils.excel.ViewSubBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * 海南上网查证，经分侧，ods层hive建表语句生成<br>
 * 需要生成两张表，一张用于外部表load，一张用与rc压缩
 * */
public class OdsRCExcelClient {
	public static final String HIVE_TABLE = "hive表名"; 
	public static final String HIVE_TMP_LOAD = "_load";
	public static final String CREATE_HIVE_TABLE = "create  EXTERNAL table IF NOT EXISTS "; 
	public static final String CREATE_HIVE_TABLE_RC = "create  table IF NOT EXISTS "; 
	public static final String PARTITIONED = ")partitioned by (date String,data String) ROW FORMAT DELIMITED FIELDS TERMINATED BY '|' LOCATION ";
	public static final String PARTITIONED_RC = ")partitioned by (data String,date String) ";
	public static final String STORED = " stored as rcfile ";
	public static final String START_ROW_DESC = "序号";
	public static final String END_ROW_DESC = "接口修改说明";
	public static final String NEWLINE = "\n";
	public static final String SINGLE_QUOTATION_MARK = "'";
	public static final String SEMICOLON = ";";
	public static final String COMMA = ",";
	public static final String SPACE = " ";
	public static final String LEFT_BRACKET = "(";
	public static final String RIGHT_BRACKET = ")";
	public static final String DROP_TABLE = "drop table ";
	public static final String DROP_VIEW = "drop view ";
	public static final String DEFAULT_VALUE = "";
	public static final String UNION_ALL = " union all ";
	public static final String CREATE_VIEW = "create view ";
	public static final String SELECT = "select ";
	public static final String NULL_AS = "null as ";
	public static final String FROM = "from ";
	public static final String ALIAS = " t1 ";
	public static final String AS = " as ";
	
	public static String getViewStr(String path, int Field_position,
			String viewname, Map<String, String> exptablename,
			boolean ifneedfiled){
		StringBuffer sql = new StringBuffer("");		
		List<ViewBean> beanlist = new Vector<ViewBean>();
		Map<String, ViewSubBean> all_tablefiled = new HashMap<String, ViewSubBean>();
		List<ExcelSheetList> list = null;
		ExcelUtils eu = new ExcelUtils();
		try {
			list = eu.readExcel(path);
			if (list != null) {
				// 循环sheet
				for (int i=0; i<list.size(); i++) {
					if (list.get(i).getSheetList().get(0).get(0).equals(HIVE_TABLE)) {
						String _table_name = list.get(i).getSheetList().get(0).get(1);
						// 过滤指定表名
						if(exptablename!=null && exptablename.get(_table_name)!=null) continue;
						ViewBean viewbean = new ViewBean();
						viewbean.setTablename(_table_name);
						// 循环row
						int _start_row = -1;
						int _end_row = -1;
						for (int j=0; j<list.get(i).getSheetList().size(); j++) {
							if (list.get(i).getSheetList().get(j).get(0).equals(START_ROW_DESC)) {
								_start_row = j+1;
							}
							if (list.get(i).getSheetList().get(j).get(0).equals(END_ROW_DESC)) {
								_end_row = j-1;
							}
							if (!list.get(i).getSheetList().get(j).get(0).equals(START_ROW_DESC) 
									&& _start_row>0 
									&& _end_row<0
									&& !list.get(i).getSheetList().get(j).get(Field_position).equals("")) {
								ViewSubBean vsb = new ViewSubBean();
								vsb.setValue(Field_position, list.get(i).getSheetList().get(j));
								viewbean.getTablefiled().put(list.get(i).getSheetList().get(j).get(Field_position), vsb);
							}
						}
						beanlist.add(viewbean);
						all_tablefiled.putAll(viewbean.getTablefiled());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 解析处理视图语句
		sql.append(DROP_VIEW+SPACE+viewname+SEMICOLON);
		sql.append(NEWLINE);
		sql.append(CREATE_VIEW+viewname+LEFT_BRACKET);
		for(Map.Entry<String, ViewSubBean> s : all_tablefiled.entrySet()){
			sql.append(s.getKey()+COMMA);
			if(ifneedfiled)
				System.out.println(s.getValue().toString()+ViewSubBean.EXCEL_SPLIT+"\""+s.getKey()+"\"");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(RIGHT_BRACKET);
		sql.append(SPACE+AS+SPACE);
		sql.append(SELECT);
		for(Map.Entry<String, ViewSubBean> s : all_tablefiled.entrySet()){
			sql.append(s.getKey()+COMMA);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(SPACE+FROM+LEFT_BRACKET);
		for(ViewBean vb : beanlist){
			sql.append(SELECT);
			for(Map.Entry<String, ViewSubBean> s : all_tablefiled.entrySet()){
				if(vb.getTablefiled().get(s.getKey())!=null){
					sql.append(s.getKey()+SPACE+COMMA);
				}else{
					sql.append(NULL_AS+s.getKey()+SPACE+COMMA);
				}
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(FROM+vb.getTablename()+UNION_ALL);
		}
		if(sql.length()>0) sql.delete(sql.length()-UNION_ALL.length(), sql.length()-1);
		sql.append(RIGHT_BRACKET+ALIAS+SEMICOLON);
		return sql.toString();
	}
	
	public static String getDropTableStr(String path, Map<String, String> exptablename){
		StringBuffer sb = new StringBuffer("");		
		List<ExcelSheetList> list = null;
		ExcelUtils eu = new ExcelUtils();
		try {
			list = eu.readExcel(path);
			if (list != null) {
				// 循环sheet
				for (int i=0; i<list.size(); i++) {
					if (list.get(i).getSheetList().get(0).get(0).equals(HIVE_TABLE)) {
						String _table_name = list.get(i).getSheetList().get(0).get(1);
						// 过滤指定表名
						if(exptablename!=null && exptablename.get(_table_name)!=null) continue;
						sb.append(DROP_TABLE+_table_name+SEMICOLON);
						sb.append(NEWLINE);
						sb.append(DROP_TABLE+_table_name+HIVE_TMP_LOAD+SEMICOLON);
						sb.append(NEWLINE);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static String getCreateTableStr(String path, int Field_position,
			int Field_type_location, Map<String, String> exptablename){
		StringBuffer sb = new StringBuffer("");
		List<ExcelSheetList> list = null;
		ExcelUtils eu = new ExcelUtils();
		try {
			list = eu.readExcel(path);
			if (list != null) {
				// 循环sheet
				for (int i=0; i<list.size(); i++) {
					if (list.get(i).getSheetList().get(0).get(0).equals(HIVE_TABLE)) {
						String _table_name = list.get(i).getSheetList().get(0).get(1);
						String _table_location = list.get(i).getSheetList().get(1).get(1);
						String _table_load_name = _table_name+HIVE_TMP_LOAD;						
						// 过滤指定表名
						if(exptablename!=null && exptablename.get(_table_name)!=null) continue;
						// 临时load外部表
						sb.append(CREATE_HIVE_TABLE+_table_load_name+LEFT_BRACKET);
						StringBuffer _tmp_table_col = new StringBuffer("");
						// 循环row
						int _start_row = -1;
						int _end_row = -1;
						for (int j=0; j<list.get(i).getSheetList().size(); j++) {
							if (list.get(i).getSheetList().get(j).get(0).equals(START_ROW_DESC)) {
								_start_row = j+1;
							}
							if (list.get(i).getSheetList().get(j).get(0).equals(END_ROW_DESC)) {
								_end_row = j-1;
							}
							if (!list.get(i).getSheetList().get(j).get(0).equals(START_ROW_DESC) 
									&& _start_row>0 
									&& _end_row<0
									&& !list.get(i).getSheetList().get(j).get(Field_position).equals("")) {
								_tmp_table_col.append(list.get(i).getSheetList().get(j).get(Field_position)+SPACE
										+list.get(i).getSheetList().get(j).get(Field_type_location)+COMMA);
							}
						}
						_tmp_table_col.deleteCharAt(_tmp_table_col.length()-1);
						sb.append(_tmp_table_col.toString());
						sb.append(PARTITIONED+SINGLE_QUOTATION_MARK
								+_table_location+SINGLE_QUOTATION_MARK+SEMICOLON);
						sb.append(NEWLINE);
						// rc压缩表
						sb.append(CREATE_HIVE_TABLE_RC+_table_name+LEFT_BRACKET);
						sb.append(_tmp_table_col.toString());
						sb.append(PARTITIONED_RC+STORED+SEMICOLON);
						sb.append(NEWLINE);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String path = "";
		
		// lte信令面表名列表
		Map<String, String> lte_exptablename = new HashMap<String, String>();
		lte_exptablename.put("tb_lte_s1mme", "");
		lte_exptablename.put("tb_lte_s6a", "");
		lte_exptablename.put("tb_lte_s11", "");
		lte_exptablename.put("tb_lte_sgs", "");
		lte_exptablename.put("tb_ods_lte_view", "");
		// lte视图
		Map<String, String> lte_view_exptablename = new HashMap<String, String>();
		lte_view_exptablename.put("tb_ods_lte_view", "");
		path = "d:/Work/CVS/BI/SSC/NETLOGANALYSIS/Develop/Design/DatabaseDesign/NETLOGANALYSIS_ODS层数据（LTE）.xlsx";
		System.out.println(getDropTableStr(path, lte_view_exptablename));
		System.out.println(getCreateTableStr(path, 6, 7, lte_view_exptablename));	
//		System.out.println(getViewStr(path, 6, "tb_ods_lte_view", lte_exptablename, false));
		
		// gn信令面表名列表
		Map<String, String> gn_exptablename = new HashMap<String, String>();
		gn_exptablename.put("tb_gn_pdp", "");
		gn_exptablename.put("tb_ods_gn_view", "");
		// gn视图
		Map<String, String> gn_view_exptablename = new HashMap<String, String>();
		gn_view_exptablename.put("tb_ods_gn_view", "");
		path = "d:/Work/CVS/BI/SSC/NETLOGANALYSIS/Develop/Design/DatabaseDesign/NETLOGANALYSIS_ODS层数据（GN）.xlsx";
		System.out.println(getDropTableStr(path, gn_view_exptablename));
		System.out.println(getCreateTableStr(path, 5, 6, gn_view_exptablename));
//		System.out.println(getViewStr(path, 5, "tb_ods_gn_view", gn_exptablename, false));
	}
}
