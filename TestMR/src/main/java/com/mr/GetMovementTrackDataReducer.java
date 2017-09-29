package com.mr;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.mr.comm.GetMovementConstants;
import com.mr.comm.MoveRecord;
import com.mr.util.TempKey;

public class GetMovementTrackDataReducer extends
		Reducer<TempKey, Text, Text, Text> {

	// 配置实例
	private Configuration conf = null;
	// 声明 MultipleOutputs 的变量
	private MultipleOutputs<Text, NullWritable> multipleOutputs;
	// 输出文件名前缀
	private static String outputName = null;
	// 数据时间
	private static String sumDate = null;
	// 存放输入value的list
	private List<String> inputValueList = new ArrayList<String>();
	// 存放输入value的转换后的list
	private List<MoveRecord> recordList = new ArrayList<MoveRecord>();
	// 存放单条源数据记录的数组
	private String[] sourceDataValueArr = null;
	// 统一时间格式为MC的时间的格式
	private SimpleDateFormat sdf_MC;
	// 输出时间的格式
	private SimpleDateFormat sdf_output;

	@SuppressWarnings("unchecked")
	protected void setup(Context context) throws IOException,
			InterruptedException {
		multipleOutputs = new MultipleOutputs(context);
		super.setup(context);
		// 获取Configuration对象
		if (conf == null)
			conf = context.getConfiguration();
		// 输出文件名前缀
		outputName = conf.get(GetMovementConstants.OUTPUT_NAME);
		// 数据时间
		sumDate = conf.get(GetMovementConstants.SOURCE_DATA_DATE);
		// MC的时间的格式
		sdf_MC = new SimpleDateFormat(GetMovementConstants.MC_TIME_FORMAT);
		// 输出时间的格式
		sdf_output = new SimpleDateFormat(
				GetMovementConstants.OUTPUT_TIME_FORMAT);
	}

	protected void reduce(TempKey key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		// vlr_msisdn_number, 0
		// vlr_imei, 1
		// vlr_event_type, 2
		// vlr_lac, 3
		// vlr_cell, 4
		// vlr_report_time, 5
		// seu_type 6
		// dataType 7
		try {
			// 用于存放前一条记录的信息
			String old_lac = null;
			String old_cell = null;
			long old_time = 0;

			// 存放当前记录的信息
			String new_lac = null;
			String new_cell = null;
			long new_time = 0;
			// 时间转换时用到的临时变量
			Date newTimeDate = new Date();
			Date oldTimeDate = new Date();

			// 输入数据list清空
			recordList.clear();
			// 将数据放入list中去
			for (Text text : values) {
				inputValueList.add(text.toString());
			}

			for (int i = 0; i < inputValueList.size(); i++) {
				MoveRecord record = new MoveRecord();
				if (i == 0) {
					// 分隔输入数据到数组
					sourceDataValueArr = inputValueList.get(i).split(
							GetMovementConstants.COMMA_SPLIT_STR, -1);
					// 设置record
					record.setMsisdn(sourceDataValueArr[0]);
					record.setVlr_imei(sourceDataValueArr[1]);
					record.setVlr_event_type(sourceDataValueArr[2]);
					new_lac = sourceDataValueArr[3];
					record.setVlr_lac(new_lac);
					// 第一条记录的lac作为下一条记录old_lac的初始值
					old_lac = new_lac;
					new_cell = sourceDataValueArr[4];
					record.setVlr_cell(new_cell);
					// 第一条记录的cell作为下一条记录old_cell的初始值
					old_cell = new_cell;
					// 时间格式转换
					newTimeDate = sdf_MC.parse(sourceDataValueArr[5]);
					record.setVlr_report_time(sdf_output.format(newTimeDate));
					// 第一条记录的time作为old_time的初始值
					new_time = newTimeDate.getTime();
					old_time = new_time;
					record.setSeu_type(sourceDataValueArr[6]);
					record.setDataType(sourceDataValueArr[7]);
					// 取一天最早的时间作第一条记录的old_time
					Date firstTimeDate = sdf_MC.parse(sumDate + "000000");
					record.setOld_time(sdf_output.format(firstTimeDate));
					record.setOld_lac(old_lac);
					record.setOld_cell(old_cell);
					// 时间间隔大于3小时标志位1，否则0
					long firstTime = sdf_MC.parse(sumDate + "000000").getTime();
					if (new_time - firstTime <= 10800000) {
						record.setIs_exception("0");
					} else {
						record.setIs_exception("1");
					}
					// 存入list
					recordList.add(record);
				} else {
					// 分隔输入数据到数组
					sourceDataValueArr = inputValueList.get(i).split(
							GetMovementConstants.COMMA_SPLIT_STR, -1);

					new_lac = sourceDataValueArr[3];
					new_cell = sourceDataValueArr[4];
					new_time = sdf_MC.parse(sourceDataValueArr[5]).getTime();

					long timeDifference = new_time - old_time;
					// 时间间隔大于3小时标志位1，否则0
					if (new_cell.equals(old_cell) && new_lac.equals(old_lac)
							&& timeDifference <= 10800000) {
						old_time = new_time;
						continue;
					}

					// 设置record
					record.setMsisdn(sourceDataValueArr[0]);
					record.setVlr_imei(sourceDataValueArr[1]);
					record.setVlr_event_type(sourceDataValueArr[2]);
					record.setVlr_lac(new_lac);
					record.setVlr_cell(new_cell);
					// 时间格式转换
					newTimeDate = sdf_MC.parse(sourceDataValueArr[5]);
					record.setVlr_report_time(sdf_output.format(newTimeDate));
					record.setSeu_type(sourceDataValueArr[6]);
					record.setDataType(sourceDataValueArr[7]);
					oldTimeDate.setTime(old_time);
					record.setOld_time(sdf_output.format(oldTimeDate));
					record.setOld_lac(old_lac);
					record.setOld_cell(old_cell);
					// 根据时间差判断异常标志位取值
					if (i < inputValueList.size() - 1) {
						if (timeDifference <= 10800000) {
							record.setIs_exception("0");
						} else {
							record.setIs_exception("1");
						}
					} else {
						// 最后一条记录与当天235959比较
						long lasttTime = sdf_MC.parse(sumDate + "235959")
								.getTime();
						if (lasttTime - new_time <= 10800000) {
							record.setIs_exception("0");
						} else {
							record.setIs_exception("1");
						}
					}
					// 存入list
					recordList.add(record);
					// 更新变量
					old_time = new_time;
					old_lac = new_lac;
					old_cell = new_cell;
				}
			}
			//清空输入数据的list
			inputValueList.clear();
			//创建MoveRecord对象
			MoveRecord record = new MoveRecord();
			
			for (int j = 0; j < recordList.size(); j++) {
				//创建用于输出的StringBuffer
				StringBuffer outputSB = new StringBuffer();
				//取出记录
				record = recordList.get(j);
//				msisdn,
				outputSB.append(record.getMsisdn()).append(
						GetMovementConstants.COMMA_SPLIT_STR);
//				vlr_imei,
				outputSB.append(record.getVlr_imei()).append(
						GetMovementConstants.COMMA_SPLIT_STR);
//				vlr_event_type,
				outputSB.append(record.getVlr_event_type()).append(
						GetMovementConstants.COMMA_SPLIT_STR);
//				vlr_lac,
				outputSB.append(record.getVlr_lac()).append(
						GetMovementConstants.COMMA_SPLIT_STR);
//				vlr_cell,
				outputSB.append(record.getVlr_cell()).append(
						GetMovementConstants.COMMA_SPLIT_STR);
//				old_lac,
				outputSB.append(record.getOld_lac()).append(
						GetMovementConstants.COMMA_SPLIT_STR);
//				old_cell,
				outputSB.append(record.getOld_cell()).append(
						GetMovementConstants.COMMA_SPLIT_STR);
//				in_time,
				if(j==0 || record.getIs_exception().equals("1")){
					outputSB.append(record.getOld_time()).append(
							GetMovementConstants.COMMA_SPLIT_STR);
				}else{
					outputSB.append(record.getVlr_report_time()).append(
							GetMovementConstants.COMMA_SPLIT_STR);
				}
//				out_time,
				if(j < recordList.size()-1){
					MoveRecord nextRecord = recordList.get(j+1);
					if(nextRecord.getIs_exception().equals("1")){
						outputSB.append(nextRecord.getOld_time()).append(
								GetMovementConstants.COMMA_SPLIT_STR);
					}else{
						outputSB.append(nextRecord.getVlr_report_time()).append(
								GetMovementConstants.COMMA_SPLIT_STR);
					}
				}else{
					Date lastTimeDate = sdf_MC.parse(sumDate + "235959");
					outputSB.append(sdf_output.format(lastTimeDate)).append(
							GetMovementConstants.COMMA_SPLIT_STR);
				}
//				seu_type,
				outputSB.append(record.getSeu_type()).append(
						GetMovementConstants.COMMA_SPLIT_STR);
//				is_exception
				if(j < recordList.size()-1){
					outputSB.append(record.getIs_exception()).append(
							GetMovementConstants.COMMA_SPLIT_STR);
				}else{
					long lasttTime = sdf_MC.parse(sumDate + "235959")
					.getTime();
					long lastReportTime = sdf_MC.parse(record.getVlr_report_time())
					.getTime();
					if (lasttTime -  lastReportTime<= 10800000) {
						outputSB.append("0").append(
								GetMovementConstants.COMMA_SPLIT_STR);
					} else {
						outputSB.append("1").append(
								GetMovementConstants.COMMA_SPLIT_STR);
					}
				}
//				dataType
				outputSB.append(record.getDataType());
				
				//设置输出
				Text outputText = new Text();
				outputText.set(outputSB.toString());
				multipleOutputs.write(outputName, outputText, NullWritable.get());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		multipleOutputs.close();
		super.cleanup(context);
	}

}
