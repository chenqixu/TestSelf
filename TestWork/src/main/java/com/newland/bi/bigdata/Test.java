package com.newland.bi.bigdata;

import com.cqx.process.LogInfoFactory;

import java.util.ArrayList;
import java.util.List;

public class Test {

    private static LogInfoFactory logger = LogInfoFactory.getInstance(Test.class);

    public static void checkTokenName(String namedOutput) {
        if (namedOutput == null || namedOutput.length() == 0) {
            throw new IllegalArgumentException("Name cannot be NULL or emtpy");
        }
        for (char ch : namedOutput.toCharArray()) {
            if ((ch >= 'A') && (ch <= 'Z')) {
                continue;
            }
            if ((ch >= 'a') && (ch <= 'z')) {
                continue;
            }
            if ((ch >= '0') && (ch <= '9')) {
                continue;
            }
            throw new IllegalArgumentException("Name cannot be have a '" + ch
                    + "' char");
        }
    }

    /**
     * 模糊显示上网位置前2个字符，后面使用***
     */
    public static String getAreaByLocationAs2Str(String location) {
        String result_str = "未知";
        if (location != null && location.trim().length() > 2) {
            String tmp_result_str = location.substring(0, 2);
            for (int i = 0; i < location.length() - 2; i++) {
                tmp_result_str += "*";
            }
            if (tmp_result_str != null && tmp_result_str.trim().length() > 0)
                result_str = tmp_result_str;
        } else if (location != null && location.trim().length() > 0) {
            result_str = location;
        }
        return result_str;
    }

    /**
     * 计算采集分配的Worker数
     *
     * @param instatnceIndex 当前节点
     */
    public static void calculateChannelForsThisWorker(int instatnceIndex) {
        logger.info("start calculateChannelForsThisWorker：{}", instatnceIndex);
        int parallelismWorker = 3;// 总worker数
        List<String> allSources = new ArrayList<String>();
        allSources.add("123");
        allSources.add("456");
        allSources.add("678");
        allSources.add("234");
        allSources.add("556");
        for (int i = instatnceIndex; i < allSources.size(); i += parallelismWorker) {
            logger.info("i：{}", i);
        }
    }

    public static void main(String[] args) throws Exception {
        // System.out.println("Hello! "+args[0]);
        // Test.checkTokenName("dns_data1");
        // double allFlux2G = 123456789011.0;
        // String a = new BigDecimal(allFlux2G).setScale(0,
        // BigDecimal.ROUND_HALF_UP)+"";
        // String b = String.valueOf(allFlux2G);
        // System.out.println(a);
        // System.out.println(b);
//		Map<String, String> OfficeTacLacMap = new HashMap<String, String>();
//		OfficeTacLacMap.put("b", "ccc1");
//		String tmp = OfficeTacLacMap.get("b");
//		if (tmp != null && tmp.trim().length() > 0) {
//			System.out.println(tmp);
//		}
//		System.out.println(getAreaByLocationAs2Str("海11"));
//		System.out.println(getAreaByLocationAs2Str(" 12 3"));
//		System.out.println(getAreaByLocationAs2Str(null));
//		
//		System.out.println(((char)((int)01)));

//		String a = "2157623";
//		DecimalFormat decimalFormat = new DecimalFormat("#0.0");//格式化设置
//		DecimalFormat decimalFormat1 = new DecimalFormat("#0.###");//格式化设置
//		System.out.println(Integer.valueOf(a));
//		System.out.println(Double.valueOf(a));
//		System.out.println(decimalFormat.format(Double.valueOf(a)));
//		System.out.println(decimalFormat1.format(Double.valueOf(a)));
//		
//		String[][] aa = {{"1","2"},{"3","4"}};
//		Object obj = aa;
//		System.out.println(((String[][])obj)[0][0]);

//		for(int i=0;i<5000;i++){
//			if(i%200==0)System.out.println(i);
//		}
//		
//		double a = 5368709120.0;
//		System.out.println(a);
//		String pdir = "/";
//		String dir = "h:\\logs";
//		String tmpDir = "h:\\logs\\test\\..";
//		File file = new File(tmpDir);
////		System.out.println(file.getCanonicalPath());
//		if(file.getCanonicalPath().endsWith("/")) {
//			System.out.println("endswith");
//		}
//		System.out.println(dir);
//		System.out.println(file.getCanonicalPath());
//		if(dir.toLowerCase().equals(file.getCanonicalPath().toLowerCase())){
//			System.out.println("aa");
//		}

//		// 十六进制计算
//		long x = Long.parseLong("A000", 16);
//		long y = Long.parseLong("10", 16);
//		long z = Long.parseLong("200", 16);
//		System.out.println(Long.toHexString(x*y+z));

//        // 随机0~0.1
//        Random rr = new Random();
//        System.out.println(rr.nextDouble() / 10);
//        System.out.println(rr.nextDouble() / 10);
////		System.out.println(Math.floor(rr.nextDouble()/10));

        // 计算采集分组
        calculateChannelForsThisWorker(0);
        calculateChannelForsThisWorker(1);
        calculateChannelForsThisWorker(2);
    }
}
