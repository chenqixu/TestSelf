package com.mr;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.mr.comm.GetMovementConstants;
import com.mr.util.TempKey;

public class LTES1MMEMapper extends Mapper<LongWritable,Text,TempKey,Text> {
	// 配置信息实体类
	private static Configuration conf = null;
	// 逐行读入的数据
	private String lineValue;
	// 是否校验源数据接口文件的字段总数
	private static boolean ifCheckSourceDataLength;
	// 源数据字段总数
	private static int sourceDataLength = 0;
	// 存放源数据的数组
	private String[] sourceDataValueArr = null;
	// mapper 输出key
	public TempKey outputKey = new TempKey();
	//统一时间格式为MC的时间的格式
	private SimpleDateFormat sdf;
	private String firstTime =null;
	private String lastTime = null;
	
	protected void setup(Context context) {
		try {
			// 如果配置信息实体类为空，则获取对象
			if (conf == null)
				conf = context.getConfiguration();
			// 获取配置文件是否需要校验源数据字段总数
			ifCheckSourceDataLength = conf.getBoolean(
					GetMovementConstants.IF_CHECK_SOURCE_DATA_LENGTH, false);
			// 获取配置文件是配置的各种接口文件的字段总数
			if (ifCheckSourceDataLength) {
				sourceDataLength = conf.getInt(
						GetMovementConstants.LTE_S1MME_DATA_LENGTH, 0);
			}
			
			//时间格式转换
			sdf = new SimpleDateFormat(GetMovementConstants.MC_TIME_FORMAT);
			firstTime = conf.get(GetMovementConstants.SOURCE_DATA_DATE) + "000000";
			lastTime = conf.get(GetMovementConstants.SOURCE_DATA_DATE) + "235959";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void map(LongWritable key, Text value, Context context)
	throws IOException, InterruptedException {
		// 逐行读入数据
		lineValue = value.toString();
		// 对源数据进行按字段分隔，放入数组中去
		sourceDataValueArr = lineValue.split(
				GetMovementConstants.VERTICLE_LINE_SEPARATOR, -1);
		// 判断数据是否出错
		if (sourceDataValueArr == null || sourceDataValueArr.length == 0) {
			return;
		}
		// 配置文件配置成需要校验，则校验源数据的字段总数
		if (ifCheckSourceDataLength) {
			if (sourceDataLength != sourceDataValueArr.length) {
				return;
			}
		}
		// 存放value的StringBuffer
		StringBuffer outputValueSB = new StringBuffer();
		// mapper 输出value
		Text outputValue = new Text();
		
		//MSISDN  7 取出开头86
		String msisdn = sourceDataValueArr[7];
		if(msisdn.equals("")|| msisdn == null){
			return;
		}
		if(msisdn.length() == 13 && msisdn.startsWith("86")){
			msisdn = msisdn.substring(2);
		}
		outputValueSB.append(msisdn).append(
				GetMovementConstants.COMMA_SPLIT_STR);
		
		//imei 6
		String imei = sourceDataValueArr[6];
		if(imei == "000000000000000"){
			return;
		}
		if(imei.length() > 14){
			imei = imei.substring(0, 14) + "0";
		}
		outputValueSB.append(imei).append(
				GetMovementConstants.COMMA_SPLIT_STR);
		
		//Procedure Type 8
		outputValueSB.append(sourceDataValueArr[8]).append(
				GetMovementConstants.COMMA_SPLIT_STR);
		
		//TAC 35
		String tacStr = sourceDataValueArr[35];
		if(tacStr == null || tacStr.equals("") || tacStr.equals("0")){
			return ;
		}
		int tac = Integer.parseInt(tacStr,16);
		outputValueSB.append(tac).append(
				GetMovementConstants.COMMA_SPLIT_STR);
		//Cell ID 36
		String cellStr = sourceDataValueArr[36];
		if(cellStr == null || cellStr.equals("") || cellStr.equals("0")){
			return ;
		}
		int eci = Integer.parseInt(cellStr,16);
		outputValueSB.append(eci).append(
				GetMovementConstants.COMMA_SPLIT_STR);
		//Procedure Start Time 10
		String inputTime = sourceDataValueArr[10];
		if(inputTime.equals("")||inputTime == null){
			return;
		}
		Date startTimeDate = new Date(Long.valueOf(inputTime));
		String outputTime = sdf.format(startTimeDate);
		if(outputTime.compareTo(firstTime)<0 ||outputTime.compareTo(lastTime)>0 ){
			return;
		}
		outputValueSB.append(outputTime).append(
				GetMovementConstants.COMMA_SPLIT_STR);
		//11
		outputValueSB.append("11").append(
				GetMovementConstants.COMMA_SPLIT_STR);
		//dataType LTE
		outputValueSB.append("LTE");
		
		outputValue.set(outputValueSB.toString());
		
		// 设置电话号码为输出key的firstKey
		outputKey.setFirstKey(msisdn);
		// 设置Procedure Start Time 10为输出key的secondKey
		outputKey.setSecondKey(outputTime);
		// mapper结果输出
		context.write(outputKey, outputValue);
	}
	
}
