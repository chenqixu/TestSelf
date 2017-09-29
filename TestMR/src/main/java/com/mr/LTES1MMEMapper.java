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
	// ������Ϣʵ����
	private static Configuration conf = null;
	// ���ж��������
	private String lineValue;
	// �Ƿ�У��Դ���ݽӿ��ļ����ֶ�����
	private static boolean ifCheckSourceDataLength;
	// Դ�����ֶ�����
	private static int sourceDataLength = 0;
	// ���Դ���ݵ�����
	private String[] sourceDataValueArr = null;
	// mapper ���key
	public TempKey outputKey = new TempKey();
	//ͳһʱ���ʽΪMC��ʱ��ĸ�ʽ
	private SimpleDateFormat sdf;
	private String firstTime =null;
	private String lastTime = null;
	
	protected void setup(Context context) {
		try {
			// ���������Ϣʵ����Ϊ�գ����ȡ����
			if (conf == null)
				conf = context.getConfiguration();
			// ��ȡ�����ļ��Ƿ���ҪУ��Դ�����ֶ�����
			ifCheckSourceDataLength = conf.getBoolean(
					GetMovementConstants.IF_CHECK_SOURCE_DATA_LENGTH, false);
			// ��ȡ�����ļ������õĸ��ֽӿ��ļ����ֶ�����
			if (ifCheckSourceDataLength) {
				sourceDataLength = conf.getInt(
						GetMovementConstants.LTE_S1MME_DATA_LENGTH, 0);
			}
			
			//ʱ���ʽת��
			sdf = new SimpleDateFormat(GetMovementConstants.MC_TIME_FORMAT);
			firstTime = conf.get(GetMovementConstants.SOURCE_DATA_DATE) + "000000";
			lastTime = conf.get(GetMovementConstants.SOURCE_DATA_DATE) + "235959";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void map(LongWritable key, Text value, Context context)
	throws IOException, InterruptedException {
		// ���ж�������
		lineValue = value.toString();
		// ��Դ���ݽ��а��ֶηָ�������������ȥ
		sourceDataValueArr = lineValue.split(
				GetMovementConstants.VERTICLE_LINE_SEPARATOR, -1);
		// �ж������Ƿ����
		if (sourceDataValueArr == null || sourceDataValueArr.length == 0) {
			return;
		}
		// �����ļ����ó���ҪУ�飬��У��Դ���ݵ��ֶ�����
		if (ifCheckSourceDataLength) {
			if (sourceDataLength != sourceDataValueArr.length) {
				return;
			}
		}
		// ���value��StringBuffer
		StringBuffer outputValueSB = new StringBuffer();
		// mapper ���value
		Text outputValue = new Text();
		
		//MSISDN  7 ȡ����ͷ86
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
		
		// ���õ绰����Ϊ���key��firstKey
		outputKey.setFirstKey(msisdn);
		// ����Procedure Start Time 10Ϊ���key��secondKey
		outputKey.setSecondKey(outputTime);
		// mapper������
		context.write(outputKey, outputValue);
	}
	
}
