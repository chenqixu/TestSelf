package com.mr;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.mr.comm.GetMovementConstants;
import com.mr.util.TempKey;

public class MCcdrDataMapper extends
		Mapper<LongWritable, Text, TempKey, Text> {
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
						GetMovementConstants.MC_CDR_DATA_LENGTH, 0);
			}
			
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
				GetMovementConstants.COMMA_SPLIT_STR, -1);
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

		String Eventresult = sourceDataValueArr[36];
		if (!Eventresult.equals("0"))
			return;
		// 字段与序号对应关系，从0开始
		// lac, 12
		// ci, 13
		// firstlac 18
		// firstci 19
		// lastlac 20
		// lastci 21
		// callingnum 23
		// callednum 24
		// callingimsi 25
		// calledimsi 26
		// callingimei 27
		// calledimei 28
		// Eventresult 36
		// eventid, 6
		// btime, 0
		// etime 1
		// pagingresptype 76
		// hoflag 21

		// 根据事件ID进行处理
		int eventid = Integer.parseInt(sourceDataValueArr[6]);
		if (eventid == 12 || eventid == 13 || eventid == 14 || eventid == 15
				|| eventid == 17 || eventid == 28) {
			// -----信令数据-----11 eventid in (12,13,14,15,17,28)

			String callingnum = sourceDataValueArr[23].trim();
			if (callingnum.equals("") || callingnum == null) {
				return;
			}
			if (callingnum.length() == 13 && callingnum.startsWith("86")) {
				callingnum = callingnum.substring(2);
			}
			String callingimsi = sourceDataValueArr[25].trim();
			String lac = sourceDataValueArr[12];
			String ci = sourceDataValueArr[13];

			if (callingimsi.equals("000000000000000") || lac == null
					|| lac.equals("0") || lac.equals("") || ci == null
					|| ci.equals("0") || ci.equals("")) {
				return;
			}

			// 设置电话号码为输出key的firstKey
			outputKey.setFirstKey(callingnum);
			// 设置 vlr_report_time即btime为输出key的secondKey
			String btime = sourceDataValueArr[0];
			if (btime.equals("") || btime == null) {
				return;
			}
			if(btime.compareTo(firstTime)<0 ||btime.compareTo(lastTime)>0 ){
				return;
			}
			outputKey.setSecondKey(btime);

			// 输出value
			// callingnum
			outputValueSB.append(callingnum).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// substr(trim(callingimei),1,14)+"0"
			if(sourceDataValueArr[27].length() < 14){
				outputValueSB.append("")
				.append(GetMovementConstants.COMMA_SPLIT_STR);
			}else{
				outputValueSB
				.append(sourceDataValueArr[27].trim().substring(0, 14) + "0")
				.append(GetMovementConstants.COMMA_SPLIT_STR);
			}
			// eventid
			outputValueSB.append(eventid).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// lac
			outputValueSB.append(lac).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// ci
			outputValueSB.append(ci).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// btime
			outputValueSB.append(btime).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			
			// 11
			outputValueSB.append("11").append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// MC
			outputValueSB.append("MC");

			outputValue.set(outputValueSB.toString());
			// mapper结果输出
			context.write(outputKey, outputValue);

		} else if (eventid == 16 || eventid == 25) {
			// ---信令数据---11 eventid in (16,25)
			if (eventid == 16) {
				String pagingresptype = sourceDataValueArr[76];
				if (!pagingresptype.equals("0") && !pagingresptype.equals("1")
						&& !pagingresptype.equals("2")
						&& !pagingresptype.equals("3")) {
					return;
				}
			}
			String callednum = sourceDataValueArr[24].trim();
			if (callednum.equals("") || callednum == null) {
				return;
			}
			if (callednum.length() == 13 && callednum.startsWith("86")) {
				callednum = callednum.substring(2);
			}
			String calledimsi = sourceDataValueArr[26].trim();
			String lac = sourceDataValueArr[12];
			String ci = sourceDataValueArr[13];
			String lastlac = sourceDataValueArr[20];
			String lastci = sourceDataValueArr[21];

			if (calledimsi.equals("000000000000000")) {
				return;
			}
			if ((lac == null || lac.equals("0") || lac.equals(""))
					&& (lastlac == null || lastlac.equals("0") || lastlac
							.equals(""))) {
				return;
			}
			if ((ci == null || ci.equals("0") || ci.equals(""))
					&& (lastci == null || lastci.equals("0") || lastci
							.equals(""))) {
				return;
			}

			// 设置电话号码为输出key的firstKey
			outputKey.setFirstKey(callednum);
			// 设置 vlr_report_time即btime为输出key的secondKey
			String btime = sourceDataValueArr[0];
			if (btime.equals("") || btime == null) {
				return;
			}
			if(btime.compareTo(firstTime)<0 ||btime.compareTo(lastTime)>0 ){
				return;
			}
			
			outputKey.setSecondKey(btime);

			// 输出value
			// callednum
			outputValueSB.append(callednum).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// substr(trim(t1.calledimei),1,14)+"0"
			if(sourceDataValueArr[28].length() < 14){
				outputValueSB.append("")
				.append(GetMovementConstants.COMMA_SPLIT_STR);
			}else{
				outputValueSB
				.append(sourceDataValueArr[28].trim().substring(0, 14) + "0")
				.append(GetMovementConstants.COMMA_SPLIT_STR);
			}
			
			// eventid
			outputValueSB.append(eventid).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// lac
			outputValueSB.append(lac).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// ci
			outputValueSB.append(ci).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// btime
			outputValueSB.append(btime).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// 11
			outputValueSB.append("11").append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// MC
			outputValueSB.append("MC");

			outputValue.set(outputValueSB.toString());
			// mapper结果输出
			context.write(outputKey, outputValue);

		} else if (eventid == 9 || eventid == 10 || eventid == 26) {

			String hoflag = sourceDataValueArr[21];
			if (hoflag.equals("0")) {
				// ---信令数据---11 eventid in (9,10,26) hoflag = 0 主叫
				String callingnum = sourceDataValueArr[23].trim();
				if (callingnum.equals("") || callingnum == null) {
					return;
				}
				if (callingnum.length() == 13 && callingnum.startsWith("86")) {
					callingnum = callingnum.substring(2);
				}
				String callingimsi = sourceDataValueArr[25].trim();
				String lac = sourceDataValueArr[12];
				String ci = sourceDataValueArr[13];
				String lastlac = sourceDataValueArr[20];
				String lastci = sourceDataValueArr[21];

				if (callingimsi.equals("000000000000000")) {
					return;
				}
				if ((lac == null || lac.equals("0") || lac.equals(""))
						&& (lastlac == null || lastlac.equals("0") || lastlac
								.equals(""))) {
					return;
				}
				if ((ci == null || ci.equals("0") || ci.equals(""))
						&& (lastci == null || lastci.equals("0") || lastci
								.equals(""))) {
					return;
				}

				// 设置电话号码为输出key的firstKey
				outputKey.setFirstKey(callingnum);
				// 设置 vlr_report_time即btime为输出key的secondKey
				String etime = sourceDataValueArr[1];
				if (etime.equals("") || etime == null) {
					return;
				}
				if(etime.compareTo(firstTime)<0 ||etime.compareTo(lastTime)>0 ){
					return;
				}
				outputKey.setSecondKey(etime);

				// 输出value
				// callingnum
				outputValueSB.append(callingnum).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// substr(trim(callingimei),1,16)
				if(sourceDataValueArr[27].length() < 14){
					outputValueSB.append("")
					.append(GetMovementConstants.COMMA_SPLIT_STR);
				}else{
					outputValueSB
					.append(sourceDataValueArr[27].trim().substring(0, 14) + "0")
					.append(GetMovementConstants.COMMA_SPLIT_STR);
				}
				// eventid
				outputValueSB.append(eventid).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// nvl(t1.lastlac,lac),
				outputValueSB.append(lastlac == null ? lac : lastlac).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// nvl(t1.lastci,ci)
				outputValueSB.append(lastci == null ? ci : lastci).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// etime
				outputValueSB.append(etime).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// 11
				outputValueSB.append("11").append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// MC
				outputValueSB.append("MC");

				outputValue.set(outputValueSB.toString());
				// mapper结果输出
				context.write(outputKey, outputValue);

			} else if (hoflag.equals("1")) {
				// ---信令数据---11 eventid in (9,10,26) hoflag = 1 被叫

				String callednum = sourceDataValueArr[24].trim();
				if (callednum.equals("") || callednum == null) {
					return;
				}
				if (callednum.length() == 13 && callednum.startsWith("86")) {
					callednum = callednum.substring(2);
				}
				String calledimsi = sourceDataValueArr[26].trim();
				String lac = sourceDataValueArr[12];
				String ci = sourceDataValueArr[13];
				String lastlac = sourceDataValueArr[20];
				String lastci = sourceDataValueArr[21];

				if (calledimsi.equals("000000000000000")) {
					return;
				}
				if ((lac == null || lac.equals("0") || lac.equals(""))
						&& (lastlac == null || lastlac.equals("0") || lastlac
								.equals(""))) {
					return;
				}
				if ((ci == null || ci.equals("0") || ci.equals(""))
						&& (lastci == null || lastci.equals("0") || lastci
								.equals(""))) {
					return;
				}

				// 设置电话号码为输出key的firstKey
				outputKey.setFirstKey(callednum);
				// 设置 vlr_report_time即btime为输出key的secondKey
				String etime = sourceDataValueArr[1];
				if (etime.equals("") || etime == null) {
					return;
				}
				if(etime.compareTo(firstTime)<0 ||etime.compareTo(lastTime)>0 ){
					return;
				}
				outputKey.setSecondKey(etime);

				// 输出value
				// callingnum
				outputValueSB.append(callednum).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// substr(trim(t1.calledimei),1,14)+"0"
				if(sourceDataValueArr[28].length() < 14){
					outputValueSB.append("")
					.append(GetMovementConstants.COMMA_SPLIT_STR);
				}else{
					outputValueSB
					.append(sourceDataValueArr[28].trim().substring(0, 14) + "0")
					.append(GetMovementConstants.COMMA_SPLIT_STR);
				}
				// eventid
				outputValueSB.append(eventid).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// nvl(t1.lastlac,lac),
				outputValueSB.append(lastlac == null ? lac : lastlac).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// nvl(t1.lastci,ci)
				outputValueSB.append(lastci == null ? ci : lastci).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// etime
				outputValueSB.append(etime).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// 11
				outputValueSB.append("11").append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// MC
				outputValueSB.append("MC");

				outputValue.set(outputValueSB.toString());
				// mapper结果输出
				context.write(outputKey, outputValue);
			}

		} else if (eventid == 1 || eventid == 2 || eventid == 4 || eventid == 6) {
			// --主叫开始位置 21|eventid (1,2,4) 31|eventid = 6

			String callingnum = sourceDataValueArr[23].trim();
			if (callingnum.equals("") || callingnum == null) {
				return;
			}
			if (callingnum.length() == 13 && callingnum.startsWith("86")) {
				callingnum = callingnum.substring(2);
			}
			String callingimsi = sourceDataValueArr[25].trim();
			String firstlac = sourceDataValueArr[18];
			String firstci = sourceDataValueArr[19];

			boolean comformFlag = true;
			if (callingimsi.equals("000000000000000") || firstlac == null
					|| firstlac.equals("0") || firstlac.equals("")
					|| firstci == null || firstci.equals("0")
					|| firstci.equals("")) {
				comformFlag = false;
			}

			if (comformFlag) {
				// 设置电话号码为输出key的firstKey
				outputKey.setFirstKey(callingnum);
				// 设置 vlr_report_time即btime为输出key的secondKey
				String btime = sourceDataValueArr[0];
				if (btime.equals("") || btime == null) {
					return;
				}
				if(btime.compareTo(firstTime)<0 ||btime.compareTo(lastTime)>0 ){
					return;
				}
				outputKey.setSecondKey(btime);

				// 输出value
				//清空outputValueSB
				outputValueSB.setLength(0);
				// callingnum
				outputValueSB.append(callingnum).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// substr(trim(callingimei),1,14)+"0"
				if(sourceDataValueArr[27].length() < 14){
					outputValueSB.append("")
					.append(GetMovementConstants.COMMA_SPLIT_STR);
				}else{
					outputValueSB
					.append(sourceDataValueArr[27].trim().substring(0, 14) + "0")
					.append(GetMovementConstants.COMMA_SPLIT_STR);
				}
				// eventid
				outputValueSB.append(eventid).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// firstlac
				outputValueSB.append(firstlac).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// firstci
				outputValueSB.append(firstci).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// btime
				outputValueSB.append(btime).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// case when eventid = 6 then 31 else 21 end seu_type
				if (eventid == 6) {
					outputValueSB.append("31").append(
							GetMovementConstants.COMMA_SPLIT_STR);
				} else {
					outputValueSB.append("21").append(
							GetMovementConstants.COMMA_SPLIT_STR);
				}

				// MC
				outputValueSB.append("MC");

				outputValue.set(outputValueSB.toString());
				// mapper结果输出
				context.write(outputKey, outputValue);
			}

			// --主叫结束位置 21|eventid (1,2,4) 31|eventid = 6

			String lastlac = sourceDataValueArr[20];
			String lastci = sourceDataValueArr[21];
			String lac = sourceDataValueArr[12];
			String ci = sourceDataValueArr[13];

			if (callingimsi.equals("000000000000000")) {
				return;
			}
			if ((lac == null || lac.equals("0") || lac.equals(""))
					&& (lastlac == null || lastlac.equals("0") || lastlac
							.equals(""))) {
				return;
			}
			if ((ci == null || ci.equals("0") || ci.equals(""))
					&& (lastci == null || lastci.equals("0") || lastci
							.equals(""))) {
				return;
			}

			// 设置电话号码为输出key的firstKey
			outputKey.setFirstKey(callingnum);
			// 设置 vlr_report_time即etime为输出key的secondKey
			String etime = sourceDataValueArr[1];
			if (etime.equals("") || etime == null) {
				return;
			}
			if(etime.compareTo(firstTime)<0 ||etime.compareTo(lastTime)>0 ){
				return;
			}
			outputKey.setSecondKey(etime);

			// 输出value
			//清空outputValueSB
			outputValueSB.setLength(0);
			// callingnum
			outputValueSB.append(callingnum).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// substr(trim(callingimei),1,14)+"0"
			if(sourceDataValueArr[27].length() < 14){
				outputValueSB.append("")
				.append(GetMovementConstants.COMMA_SPLIT_STR);
			}else{
				outputValueSB
				.append(sourceDataValueArr[27].trim().substring(0, 14) + "0")
				.append(GetMovementConstants.COMMA_SPLIT_STR);
			}
			// eventid
			outputValueSB.append(eventid).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// nvl(t1.lastlac,lac),
			outputValueSB.append(lastlac == null ? lac : lastlac).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// nvl(t1.lastci,ci),
			outputValueSB.append(lastci == null ? ci : lastci).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// etime
			outputValueSB.append(etime).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// case when eventid = 6 then 31 else 21 end seu_type
			if (eventid == 6) {
				outputValueSB.append("31").append(
						GetMovementConstants.COMMA_SPLIT_STR);
			} else {
				outputValueSB.append("21").append(
						GetMovementConstants.COMMA_SPLIT_STR);
			}

			// MC
			outputValueSB.append("MC");
			// mapper 输出value
			Text outputValue2 = new Text();
			outputValue2.set(outputValueSB.toString());
			// mapper结果输出
			context.write(outputKey, outputValue2);

		} else if (eventid == 3 || eventid == 5 || eventid == 7) {
			// --被叫开始位置 21|eventid (3,5) 31|eventid = 7

			String callednum = sourceDataValueArr[24].trim();
			if (callednum.equals("") || callednum == null) {
				return;
			}
			if (callednum.length() == 13 && callednum.startsWith("86")) {
				callednum = callednum.substring(2);
			}
			String calledimsi = sourceDataValueArr[26].trim();
			String firstlac = sourceDataValueArr[18];
			String firstci = sourceDataValueArr[19];

			boolean comformFlag = true;
			if (calledimsi.equals("000000000000000") || firstlac == null
					|| firstlac.equals("0") || firstlac.equals("")
					|| firstci == null || firstci.equals("0")
					|| firstci.equals("")) {
				comformFlag = false;
			}

			if (comformFlag) {
				// 设置电话号码为输出key的firstKey
				outputKey.setFirstKey(callednum);
				// 设置 vlr_report_time即btime为输出key的secondKey
				String btime = sourceDataValueArr[0];
				if (btime.equals("") || btime == null) {
					return;
				}
				if(btime.compareTo(firstTime)<0 ||btime.compareTo(lastTime)>0 ){
					return;
				}
				outputKey.setSecondKey(btime);

				// 输出value
				//清空outputValueSB
				outputValueSB.setLength(0);
				// callednum
				outputValueSB.append(callednum).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// substr(trim(calledimei),1,14)+"0" imei,
				if(sourceDataValueArr[28].length() < 14){
					outputValueSB.append("")
					.append(GetMovementConstants.COMMA_SPLIT_STR);
				}else{
					outputValueSB
					.append(sourceDataValueArr[28].trim().substring(0, 14) + "0")
					.append(GetMovementConstants.COMMA_SPLIT_STR);
				}
				// eventid
				outputValueSB.append(eventid).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// firstlac
				outputValueSB.append(firstlac).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// firstci
				outputValueSB.append(firstci).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// btime
				outputValueSB.append(btime).append(
						GetMovementConstants.COMMA_SPLIT_STR);
				// case when eventid = 7 then 31 else 21 end seu_type
				if (eventid == 7) {
					outputValueSB.append("31").append(
							GetMovementConstants.COMMA_SPLIT_STR);
				} else {
					outputValueSB.append("21").append(
							GetMovementConstants.COMMA_SPLIT_STR);
				}
				// MC
				outputValueSB.append("MC");

				outputValue.set(outputValueSB.toString());
				// mapper结果输出
				context.write(outputKey, outputValue);
			}

			// --被叫结束位置 21|eventid (3,5) 31|eventid = 7

			String lastlac = sourceDataValueArr[20];
			String lastci = sourceDataValueArr[21];
			String lac = sourceDataValueArr[12];
			String ci = sourceDataValueArr[13];

			if (calledimsi.equals("000000000000000")) {
				return;
			}
			if ((lac == null || lac.equals("0") || lac.equals(""))
					&& (lastlac == null || lastlac.equals("0") || lastlac
							.equals(""))) {
				return;
			}
			if ((ci == null || ci.equals("0") || ci.equals(""))
					&& (lastci == null || lastci.equals("0") || lastci
							.equals(""))) {
				return;
			}

			// 设置电话号码为输出key的firstKey
			outputKey.setFirstKey(callednum);
			// 设置 vlr_report_time即etime为输出key的secondKey
			String etime = sourceDataValueArr[1];
			if (etime.equals("") || etime == null) {
				return;
			}
			if(etime.compareTo(firstTime)<0 ||etime.compareTo(lastTime)>0 ){
				return;
			}
			outputKey.setSecondKey(etime);

			// 输出value
			//清空outputValueSB
			outputValueSB.setLength(0);
			// callednum
			outputValueSB.append(callednum).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// substr(trim(t1.calledimei),1,14)+"0" imei,
			if(sourceDataValueArr[28].length() < 14){
				outputValueSB.append("")
				.append(GetMovementConstants.COMMA_SPLIT_STR);
			}else{
				outputValueSB
				.append(sourceDataValueArr[28].trim().substring(0, 14) + "0")
				.append(GetMovementConstants.COMMA_SPLIT_STR);
			}
			// eventid
			outputValueSB.append(eventid).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// nvl(t1.lastlac,lac),
			outputValueSB.append(lastlac == null ? lac : lastlac).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// nvl(t1.lastci,ci),
			outputValueSB.append(lastci == null ? ci : lastci).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// etime
			outputValueSB.append(etime).append(
					GetMovementConstants.COMMA_SPLIT_STR);
			// case when eventid = 7 then 31 else 21 end seu_type
			if (eventid == 7) {
				outputValueSB.append("31").append(
						GetMovementConstants.COMMA_SPLIT_STR);
			} else {
				outputValueSB.append("21").append(
						GetMovementConstants.COMMA_SPLIT_STR);
			}
			// MC
			outputValueSB.append("MC");

			// mapper 输出value
			Text outputValue2 = new Text();
			outputValue2.set(outputValueSB.toString());
			// mapper结果输出
			context.write(outputKey, outputValue2);

		}

	}

}
