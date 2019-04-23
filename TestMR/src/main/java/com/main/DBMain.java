package com.main;

import com.mr.bean.DBBean;
import com.mr.bean.DBType;
import com.mr.db.DBMapper;
import com.mr.db.Student;
import com.mr.util.HadoopConfUtil;
import com.mr.util.JobBuilder;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * DBMain
 *
 * @author chenqixu
 */
public class DBMain {

    public static void main(String[] args) throws Exception {
        HadoopConfUtil.setHadoopUser("edc_base");
        String outputPath = "hdfs://10.1.8.75:8020/test/mroutput";
        String jarName = "D:\\Document\\Workspaces\\Git\\TestSelf\\TestMR\\target\\TestMR-1.0.0.jar";
        String confPath;
        if (HadoopConfUtil.isWindow())
            confPath = "d:\\tmp\\etc\\hadoop\\conf75\\";
        else
            confPath = "/etc/hadoop/conf/";
        String dbUrl = "jdbc:oracle:thin:@10.1.0.242:1521:ywxx";
        String userName = "bishow";
        String passwd = "C%MuhN#q$4";
        DBBean dbBean = DBBean.newbuilder()
                .setDbType(DBType.ORACLE)
                .setDbUrl(dbUrl)
                .setUserName(userName)
                .setPasswd(passwd);

        JobBuilder jobBuilder = JobBuilder.newbuilder()
                .buildConf(confPath)
                .buildDBConfig(dbBean)
                .buildFileSystem();
        jobBuilder.buildJob(DBMain.class, jarName)
                .setLocalMode()
                .setDBInputFormat(Student.class, "student",
                        null, null, new String[]{"sno", "sname"})
                .setMapper(DBMapper.class, Text.class, NullWritable.class)
                .setNullReducer(Text.class, NullWritable.class)
                .deleteAndSetOutPutPath(outputPath, TextOutputFormat.class)
                .waitForCompletion();
    }
}
