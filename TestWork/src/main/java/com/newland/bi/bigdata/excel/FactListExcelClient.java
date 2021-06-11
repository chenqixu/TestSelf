package com.newland.bi.bigdata.excel;

import java.io.IOException;
import java.util.List;

import com.cqx.common.utils.excel.ExcelSheetList;
import com.cqx.common.utils.excel.ExcelUtils;
import org.apache.commons.lang.StringUtils;

public class FactListExcelClient {
	public static final String TABLE_HEAD_TAG_D = "查询表结构说明(日)清单";
	public static final String TABLE_HEAD_TAG_M = "查询表结构说明（月）清单";
	public static final String TABLE_HEAD_TAGH_D = "查询表结构说明(日)汇总";
	public static final String TABLE_HEAD_TAGH_M = "查询表结构说明（月）汇总";
	public static final String CREATE_HIVE_TABLE_RC = "create  table IF NOT EXISTS "; 
	public static final String COMMA = ",";
	public static final String SPACE = " ";
	public static final String LEFT_BRACKET = "(";
	public static final String RIGHT_BRACKET = ")";
	public static final String STORED = " stored as rcfile ";
	public static final String SEMICOLON = ";";
	public static final String NEWLINE = "\n";
	public static final String PARTITION = "partition";
	public static final String PARTITION_FILED_TYPE = "string";
	public static final String PARTITIONED = ") partitioned by (";
	public static final String DROP_TABLE = "drop table ";
	
	/**
	 * 删表语句
	 * */
	public static String getDropTableStr(String path){
		StringBuffer sb = new StringBuffer("");		
		List<ExcelSheetList> list = null;
		ExcelUtils eu = new ExcelUtils();
		try {
			list = eu.readExcel(path);
			if (list != null) {
				// 循环sheet
				for (int i=0; i<list.size(); i++) {
					String table_name_d = "";
					String table_name_m = "";
					// 循环sheet每一行第一列
					for (int j=0; j<list.get(i).getSheetList().size(); j++) {
						if (list.get(i).getSheetList().get(j).size()<=0) {
							continue;
						}
						if (list.get(i).getSheetList().get(j).get(0).equals(TABLE_HEAD_TAG_D) || list.get(i).getSheetList().get(j).get(0).equals(TABLE_HEAD_TAGH_D)) {
							table_name_d = list.get(i).getSheetList().get(j).get(1);
							sb.append(DROP_TABLE+table_name_d+SEMICOLON);
							sb.append(NEWLINE);
						}						
						if (list.get(i).getSheetList().get(j).get(0).equals(TABLE_HEAD_TAG_M) || list.get(i).getSheetList().get(j).get(0).equals(TABLE_HEAD_TAGH_M)) {
							table_name_m = list.get(i).getSheetList().get(j).get(1);
							sb.append(DROP_TABLE+table_name_m+SEMICOLON);
							sb.append(NEWLINE);
							break;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * 根据文件路径，返回建表语句，提供字段名位置和字段类型位置
	 * */
	public static String getCreateTableStr(String path, int Field_position, int Field_type_location){
		return getCreateTableStr(null, path, Field_position, Field_type_location);
	}
	
	/**
	 * 根据文件路径，sheetName，返回建表语句，提供字段名位置和字段类型位置
	 * */
	public static String getCreateTableStr(String sheetName, String path, int Field_position, int Field_type_location){
		StringBuffer sb = new StringBuffer("");
		List<ExcelSheetList> list = null;
		ExcelUtils eu = new ExcelUtils();
		try {
			list = eu.readExcel(path);
			if (list != null) {
				// 循环sheet
				for (int i=0; i<list.size(); i++) {
					if(StringUtils.isNotEmpty(sheetName)){
						if(!list.get(i).getSheetName().equals(sheetName))continue;
					}
					int D1 = 0;
					int M1 = 0;
					String table_name_d = "";
					String table_name_m = "";
					// 循环sheet每一行第一列
					for (int j=0; j<list.get(i).getSheetList().size(); j++) {
						if (list.get(i).getSheetList().get(j).size()<=0) {
							continue;
						}
						if (list.get(i).getSheetList().get(j).get(0).equals(TABLE_HEAD_TAG_D) || list.get(i).getSheetList().get(j).get(0).equals(TABLE_HEAD_TAGH_D)) {
							table_name_d = list.get(i).getSheetList().get(j).get(1);
							D1 = j;
						}						
						if (list.get(i).getSheetList().get(j).get(0).equals(TABLE_HEAD_TAG_M) || list.get(i).getSheetList().get(j).get(0).equals(TABLE_HEAD_TAGH_M)) {
							table_name_m = list.get(i).getSheetList().get(j).get(1);
							M1 = j;
						}
						// 循环row
						if(D1>0 && M1>0) { // 日表月表都有
							int _start_row = D1+2;
							int _end_row = M1-1;
							int _m_start_row = M1+2;
							int _m_end_row = _end_row-_start_row+_end_row+3;
							// 有可能没有元素
							if(list.get(i).getSheetList().get(_end_row).size()>0
									&& list.get(i).getSheetList().get(_end_row).get(0).length()>0){
								_end_row=M1;
								_m_end_row=_m_end_row+1;
							}
							StringBuffer _tmp_table_col = new StringBuffer("");
							StringBuffer partitionsb = new StringBuffer("");
							// 日表
							sb.append(CREATE_HIVE_TABLE_RC+table_name_d+LEFT_BRACKET);
							for (int x=_start_row; x<_end_row; x++) {
								if (list.get(i).getSheetList().get(x).get(Field_type_location).equals(PARTITION)) {
									partitionsb.append(list.get(i).getSheetList().get(x).get(Field_position)+SPACE
											+PARTITION_FILED_TYPE+COMMA);
								} else {
									_tmp_table_col.append(list.get(i).getSheetList().get(x).get(Field_position)+SPACE
										+list.get(i).getSheetList().get(x).get(Field_type_location)+COMMA);
								}
							}
							_tmp_table_col.deleteCharAt(_tmp_table_col.length()-1);
							sb.append(_tmp_table_col.toString());
							sb.append(PARTITIONED);
							partitionsb.deleteCharAt(partitionsb.length()-1);
							sb.append(partitionsb);
							sb.append(RIGHT_BRACKET);
							sb.append(STORED+SEMICOLON);
							sb.append(NEWLINE);
							// 月表
							_tmp_table_col.delete(0, _tmp_table_col.length());
							partitionsb.delete(0, partitionsb.length());
							sb.append(CREATE_HIVE_TABLE_RC+table_name_m+LEFT_BRACKET);							
							for (int x=_m_start_row; x<_m_end_row; x++) {								
								if (list.get(i).getSheetList().get(x).get(Field_type_location).equals(PARTITION)) {
									partitionsb.append(list.get(i).getSheetList().get(x).get(Field_position)+SPACE
											+PARTITION_FILED_TYPE+COMMA);
								} else {
									_tmp_table_col.append(list.get(i).getSheetList().get(x).get(Field_position)+SPACE
										+list.get(i).getSheetList().get(x).get(Field_type_location)+COMMA);
								}
							}
							_tmp_table_col.deleteCharAt(_tmp_table_col.length()-1);
							sb.append(_tmp_table_col.toString());
							sb.append(PARTITIONED);
							partitionsb.deleteCharAt(partitionsb.length()-1);
							sb.append(partitionsb);
							sb.append(RIGHT_BRACKET);
							sb.append(STORED+SEMICOLON);
							sb.append(NEWLINE);
							break;
						} else if (D1==0 && M1>0){ // 只有月表
							int _start_row = M1+2;
							StringBuffer _tmp_table_col = new StringBuffer("");
							StringBuffer partitionsb = new StringBuffer("");
							// 月表
							sb.append(CREATE_HIVE_TABLE_RC+table_name_m+LEFT_BRACKET);
							for (int x=_start_row; x<list.get(i).getSheetList().size(); x++) {
								if (list.get(i).getSheetList().get(x).size()<=0 || list.get(i).getSheetList().get(x).get(0).equals("")){
									break;
								}
								if (list.get(i).getSheetList().get(x).get(Field_type_location).equals(PARTITION)) {
									partitionsb.append(list.get(i).getSheetList().get(x).get(Field_position)+SPACE
											+PARTITION_FILED_TYPE+COMMA);
								} else {
									_tmp_table_col.append(list.get(i).getSheetList().get(x).get(Field_position)+SPACE
										+list.get(i).getSheetList().get(x).get(Field_type_location)+COMMA);
								}
							}
							_tmp_table_col.deleteCharAt(_tmp_table_col.length()-1);
							sb.append(_tmp_table_col.toString());
							sb.append(PARTITIONED);
							partitionsb.deleteCharAt(partitionsb.length()-1);
							sb.append(partitionsb);
							sb.append(RIGHT_BRACKET);
							sb.append(STORED+SEMICOLON);
							sb.append(NEWLINE);
							break;
						}
					}
				}				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
//		String path = "d:/Work/CVS/BI/SSC/NETLOGANALYSIS/Develop/Design/DatabaseDesign/NETLOGANALYSIS_事实表(hive)设计.xlsx";		
//		System.out.println(FactListExcelClient.getDropTableStr(path));
//		System.out.println(FactListExcelClient.getCreateTableStr(path, 0, 6));
		String path = "d:/Work/ETL/上网查证/海南/数据库设计文档/事实表/NETLOGANALYSIS_事实表(hive)设计.xlsx";
		System.out.println(FactListExcelClient.getCreateTableStr("基站分析", path, 0, 6));
	}
}
