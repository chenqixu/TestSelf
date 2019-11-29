package com.newland.bi.bigdata.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeTest {

    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * 2个时间字符串进行比较，返回
     */
    public static boolean dateCompareTo(String date1, String date2,
                                        String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date1).after(sdf.parse(date2));
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 对时间字符串进行格式化
     */
    public static String formatDateString(String date, String origFormat,
                                          String destFormat) throws ParseException {
        SimpleDateFormat sf1 = new SimpleDateFormat(origFormat);
        Date d = sf1.parse(date);
        SimpleDateFormat sf2 = new SimpleDateFormat(destFormat);
        return sf2.format(d);
    }

    /**
     * 判断时间是否在20160311之前
     */
    public static boolean ifBefore20160311Times(String times) {
        boolean result = false;
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
        try {
            result = sdf1.parse(times).before(sdf1.parse("20160311"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 输入时间1开始结束，时间2开始结束，获得时间1落在时间2内的秒数
     */
    public static long getSplitTime(String date1_s, String date1_e,
                                    String date2_s, String date2_e, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        long result = 0;
        if (date1_s != null && date1_e != null && date2_s != null
                && date2_e != null && date1_s.trim().length() > 0
                && date1_e.trim().length() > 0 && date2_s.trim().length() > 0
                && date2_e.trim().length() > 0) {
            try {
                // 开始时间大于查询开始时间，结束时间小于等于查询结束时间
                if (sdf.parse(date1_s).after(sdf.parse(date2_s))
                        && sdf.parse(date1_e).before(sdf.parse(date2_e))) {
                    result = sdf.parse(date1_e).getTime()
                            - sdf.parse(date1_s).getTime();
                }
                // 开始时间小于查询开始时间，结束时间小于等于查询结束时间
                else if (sdf.parse(date1_s).before(sdf.parse(date2_s))
                        && (sdf.parse(date1_e).before(sdf.parse(date2_e)) || sdf
                        .parse(date1_e).equals(sdf.parse(date2_e)))) {
                    result = sdf.parse(date1_e).getTime()
                            - sdf.parse(date2_s).getTime();
                }
                // 开始时间大于等于查询开始时间，结束时间大于查询结束时间
                else if ((sdf.parse(date1_s).after(sdf.parse(date2_s)) || sdf
                        .parse(date1_s).equals(sdf.parse(date2_s)))
                        && sdf.parse(date1_e).after(sdf.parse(date2_e))) {
                    result = sdf.parse(date2_e).getTime()
                            - sdf.parse(date1_s).getTime();
                }
                // 开始时间小于等于查询开始时间，结束时间大于等于查询结束时间
                else if ((sdf.parse(date1_s).before(sdf.parse(date2_s)) || sdf
                        .parse(date1_s).equals(sdf.parse(date2_s)))
                        && (sdf.parse(date1_e).after(sdf.parse(date2_e)) || sdf
                        .parse(date1_e).equals(sdf.parse(date2_e)))) {
                    result = sdf.parse(date2_e).getTime()
                            - sdf.parse(date2_s).getTime();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result / 1000;
    }

    /**
     * 得到UTC时间，类型为字符串，格式为"yyyy-MM-dd HH:mm"<br />
     * 如果获取失败，返回null
     *
     * @return
     */
    public static String getUTCTimeStr() {
        StringBuffer UTCTimeBuffer = new StringBuffer();
        // 1、取得本地时间：
        Calendar cal = Calendar.getInstance();
        // 2、取得时间偏移量：
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        UTCTimeBuffer.append(year).append("-").append(month).append("-")
                .append(day);
        UTCTimeBuffer.append(" ").append(hour).append(":").append(minute);
        try {
            format.parse(UTCTimeBuffer.toString());
            return UTCTimeBuffer.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将UTC时间转换为东八区时间
     *
     * @param UTCTime
     * @return
     */
    public static String getLocalTimeFromUTC(String UTCTime) {
        java.util.Date UTCDate = null;
        String localTimeStr = null;
        try {
            UTCDate = format.parse(UTCTime);
            format.setTimeZone(TimeZone.getTimeZone("GMT-8"));
            localTimeStr = format.format(UTCDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return localTimeStr;
    }

    public static String getNowGMTDate(String timezone, String type) {
        return format.format(Calendar.getInstance(TimeZone.getTimeZone("GMT" + type + timezone)).getTime());
    }

    public static String getNow() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(now);
    }

    public static String supplementZero(String date) {
        StringBuffer sb = null;
        if (date != null && date.trim().length() > 0 && !date.contains(":")) {
            sb = new StringBuffer(date);
            int datalen = date.length();
            if (datalen < 14) {
                for (int i = datalen; i < 14; i++) {
                    sb.append("0");
                }
            }
        }
        return sb != null ? sb.toString() : null;
    }

    public static void main(String[] args) throws Exception {
//		// String time2 = "180_hw_1452129156865.CHK";
//		// int begin = 0;
//		// int end = 13;
//		// System.out.println(time2.substring(begin, end));
//
//		// long time1 = 1462817971693l;
//		// Date date = new Date(time1);
//		// System.out.println(date);
//		// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//		// System.out.println(sdf.format(date));
//
//		// String time3 = "19910414000000";
//		String time3 = "20160522113010";
//		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
//		System.out.println("原来的时间" + sdf3.parse(time3));
//		Calendar cale = Calendar.getInstance();
//		cale.set(2016, 5, 22, 11, 30, 10);
//		System.out.println(cale.isSet(2));
//		cale.setTime(sdf3.parse(time3));
//		System.out.println("初始化的时间" + sdf3.format(cale.getTime()));
//		cale.add(Calendar.DAY_OF_MONTH, 1);
//		System.out.println("初始化的时间" + sdf3.format(cale.getTime()));
//		// cale.add(Calendar.MINUTE, -10);
//		// cale.add(Calendar.MILLISECOND, -1);
//		// System.out.println("修改后的时间"+cale.getTime());
//		// System.out.println("修改后的时间"+sdf3.format(cale.getTime()));
//		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHH");
//		System.out.println("[datebefore]"
//				+ sdf1.parse("2016031111").before(sdf1.parse("2016031112")));
//
//		// System.out.println(dateCompareTo(time3, "20160224143559",
//		// "yyyyMMddHHmmss"));
//		// 20160224142600-20160224145600
//		// 20160224143600-20160224145900
//		// 20160224143600-20160224145600
//		// getSplitTime("20160224142600","20160224145600","20160224143600","20160224145900","yyyyMMddHHmmss");
//		// 20160224143600-20160224145900
//		// getSplitTime("20160224142600","20160224151600","20160224143600","20160224145900","yyyyMMddHHmmss");
//		// 20160224144900-20160224145900
//		// long c =
//		// getSplitTime("20160216010639","20160216010649","20160216010644","20160216010649","yyyyMMddHHmmss");
//		// System.out.println(c);
//		// System.out.println(23*60);
//		// Calendar nowTime = Calendar.getInstance();
//		// Date nowDate = (Date) nowTime.getTime(); //得到当前时间
//		// Calendar afterTime = Calendar.getInstance();
//		// afterTime.roll(Calendar.MONTH, 5); //当前月份+5
//		// afterTime.add(Calendar.MINUTE, -5); //当前分钟+5
//		// Date afterDate = (Date) afterTime.getTime();
//		// System.out.println("今天时间"+nowDate);
//		// System.out.println("修改后的 时间"+afterDate);
//
//		// for(int i=1;i<10;i++)
//		// System.out.println(i+"-"+i% 3);
//
//		// String ss = "164824_";
//		// System.out.println(ss.substring(0, 2));
//		// int queryDate = 20160301;
//		// for(int i=0;i<12;i++){
//		// int end = queryDate+i;
//		// System.out.println(end+" "+ifBefore20160311Times(end+""));
//		// }
//		//
//		// String Starttime_s = "20160301000000";
//		// System.out.println(Starttime_s.substring(0,8));
//		//
//		// List<String> oldlist = null;
//		// List<String> newlist = null;
//		// // oldlist = new ArrayList<String>();
//		// // oldlist.add("1");
//		// newlist = new ArrayList<String>();
//		// newlist.add("2");
//		// if(oldlist!=null){
//		// if(newlist==null)
//		// newlist = new ArrayList<String>();
//		// newlist.addAll(oldlist);
//		// }
//		// System.out.println(newlist);
//
//		// 判断是否整点
//		String time4 = "20170215110000";
//		Date now = sdf3.parse(time4);
//		// Date now=new Date();
//		Long time = now.getTime();
//		System.out.println(time);
//		if (time % (1000 * 60 * 60) == 0) {
//			System.out.println("整点");
//		}

//		// 获取UTC时间
//		String UTCTimeStr = getUTCTimeStr();
//		System.out.println(UTCTimeStr);
//		// UTC时间转换为本地时区时间
//		System.out.println(getLocalTimeFromUTC(UTCTimeStr));
//
////		System.out.println(getNowGMTDate("8", "+"));
//		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));

        System.out.println(getNow());
        System.out.println(supplementZero(""));
        System.out.println(supplementZero(null));
        System.out.println(supplementZero("20181114 01:01"));
        System.out.println(supplementZero("20181114"));
        System.out.println(supplementZero("2018111401"));
        System.out.println(supplementZero("201811140101"));
        System.out.println(supplementZero("20181114010101"));
    }

    /**
     * 返回微秒
     *
     * @return
     */
    public static Long getmicTime() {
        Long cutime = System.currentTimeMillis() * 1000; // 微秒
        Long nanoTime = System.nanoTime(); // 纳秒
        long tmp = (nanoTime - nanoTime / 1000000 * 1000000) / 1000;
        System.out.println("cutime：" + cutime + "，tmp：" + tmp);
        return cutime + tmp;
    }

    /**
     * 解析时间戳中的微秒
     *
     * @param time
     * @return
     */
    public static Long parserMicTime(Long time) {
        Long millis = time / 1000; // 毫秒
        return time - millis * 1000; // 微秒
    }

}
