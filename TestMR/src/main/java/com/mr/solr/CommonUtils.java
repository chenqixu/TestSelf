package com.mr.solr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * solr工具类
 *
 * @author chenqixu
 */
public class CommonUtils {

    static SimpleDateFormat hbaseDateFormat = new SimpleDateFormat(
            "yyyyMMddHHmmss");

    public static boolean ifFileExit(Job job, FileSystem fs, String path) {
        boolean fileExistFlag = false;
        Path inputPath = new Path(path);
        try {
            if (fs.exists(inputPath)) {
                FileStatus[] subdirectoryArr = fs.listStatus(inputPath);
                for (FileStatus subdirectory : subdirectoryArr) {
                    if (subdirectory.isDir()) {
                        fileExistFlag = ifFileExit(job, fs, subdirectory
                                .getPath().toString());
                        if (fileExistFlag)
                            break;
                    } else {
                        fileExistFlag = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileExistFlag;
    }

    public static void addInputPath(Job job, FileSystem fs, Path inputDir, String filter, Class mapper)
            throws FileNotFoundException, IOException {
        if (fs.exists(inputDir)) {
            System.out.println("扫描输入路径:" + inputDir.toString());

            FileStatus[] subdirectoryArr = fs.listStatus(inputDir);
            for (FileStatus subdirectory : subdirectoryArr) {
                if (subdirectory.isDir()) {
                    addInputPath(job, fs, subdirectory.getPath(), filter,
                            mapper);
                } else {
                    System.out.println("加载文件:" +
                            subdirectory.getPath().toString());
                    MultipleInputs.addInputPath(job, subdirectory.getPath(),
                            TextInputFormat.class, mapper);
                }
            }
        } else {
            System.out.println("The Path:" + inputDir + " is not exists!");
        }
    }

    public static String generateModKey(long telnumber, int modNum) {
        try {
            Random random = new Random();
            random.setSeed(telnumber);
            long result = Math.abs(random.nextLong() % modNum);
            String resStr = result + "";
            String modStr = modNum + "";
            int resDigit = resStr.length();
            int modDigit = modStr.length();
            for (int i = 0; i < modDigit - resDigit; i++) {
                resStr = "0" + resStr;
            }
            return resStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTableName(String tableName, String type, String utapTime) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
            Calendar ca = new GregorianCalendar();
            ca.setTime(df.parse(utapTime));
            if (type.equals("0"))
                tableName = tableName + "_" + ca.get(5);
            else {
                tableName = tableName + "_" + (ca.get(2) + 1);
            }
            System.out.println("tbName:" + tableName + " statisTime:" +
                    utapTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tableName;
    }

    public static int getTaskSplitLend(String taskType) {
        int leng = 0;

        if (taskType.equals("0")) {
            leng = 10;
        } else if (taskType.equals("1")) {
            leng = 8;
        } else {
            leng = 6;
        }
        return leng;
    }

    public static String pretreatmentPhone(String line, Configuration conf) {
        String[] values = (String[]) null;
        if (conf.get("SOURCE_SPILT").equals("|"))
            values = line.split("\\|");
        else {
            values = line.split(conf.get("SOURCE_SPILT"));
        }
        String[] phonePisition = conf.get("SOURCE_PHONE_POSITIONS")
                .split(",");
        for (int i = 0; i < phonePisition.length; i++) {
            if ((values[java.lang.Integer.parseInt(phonePisition[i])] == null) || ("".equals(values[java.lang.Integer.parseInt(phonePisition[i])]))) {
                continue;
            }
            String str = preProcessMsisdn(values[java.lang.Integer.parseInt(phonePisition[i])]);
            if (str == null) {
                str = "";
            }
            line = line.replace(values[java.lang.Integer.parseInt(phonePisition[i])], str);
        }
        return line;
    }

    public static void main(String[] args) {
        System.out.println(timeChange("", "-1"));
    }

    public static String pretreatmentTime(String line, Configuration conf) {
        if (conf.get("IS_TIME_FORMAT").equals("1")) {
            String[] values = (String[]) null;
            if (conf.get("SOURCE_SPILT").equals("|"))
                values = line.split("\\|", -1);
            else {
                values = line.split(conf.get("SOURCE_SPILT"), -1);
            }
            String[] timePisition = conf.get("SOURCE_TIME_POSITIONS")
                    .split(",");
            for (int i = 0; i < timePisition.length; i++) {
                if ((values[java.lang.Integer.parseInt(timePisition[i])] == null) || ("".equals(values[java.lang.Integer.parseInt(timePisition[i])]))) {
                    continue;
                }
                line = line.replace(values[java.lang.Integer.parseInt(timePisition[i])], timeChange(values[java.lang.Integer.parseInt(timePisition[i])], conf
                        .get("SOURCE_TIME_FORMAT")));
            }
        }
        return line;
    }

    public static String timeChange(String time, String farmat) {
        String date = "";
        try {
            if (farmat.equals("-1")) {
                long lt = new Long(time).longValue();
                Date dateTime = new Date(lt);
                date = hbaseDateFormat.format(dateTime);
            } else {
                SimpleDateFormat sourceDateFormat = new SimpleDateFormat(farmat);
                Date d = sourceDateFormat.parse(time);
                date = hbaseDateFormat.format(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("date=" + date);
        return date;
    }

    public static String preProcessMsisdn(String telNum) {
        if (telNum.startsWith("86")) {
            telNum = telNum.substring(2);
            if (telNum.length() > 15)
                return null;
        } else if (telNum.length() > 18) {
            return null;
        }
        return telNum;
    }

    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
