package com.main;

import com.mr.util.JobBuilder;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DBMainTest {

    private JobBuilder jobBuilder;
    private String confPath;

    @Before
    public void setUp() {
        jobBuilder = new JobBuilder();
        confPath = "d:\\tmp\\etc\\hadoop\\conf75\\";
    }

    @Test
    public void split() {
        jobBuilder.buildConf(confPath);
        System.out.println(jobBuilder.getConfiguration().get(MRJobConfig.NUM_MAPS));
    }

    @Test
    public void splitMsisdn() {
        String column_name = "user_code";
        long min = 591305000000002l;
        long max = 599500287992185l;
        long split_num = 2000;
        long avg = (max - min) / split_num;
        System.out.println(avg);
        List<SplitBean> splitBeanList = new ArrayList<>();
        for (int i = 0; i < split_num; i++) {
            long start = min + i * avg;
            long end = min + (i + 1) * avg;
            System.out.println(column_name + " >= " + start + " and " + column_name + " < " + end);
            splitBeanList.add(new SplitBean(start, end));
        }
    }

    @Test
    public void DBInputFormatGetSplits() {
        long count = 10l;// select count(*) from table
        /**
         * <pre>
         *     chunks：
         *     1、use default
         *     2、getConfiguration().get(MRJobConfig.NUM_MAPS)
         *     mapreduce.job.maps 旧版参数
         * </pre>
         */
        int chunks = 2;
        long chunkSize = (count / chunks);
        // Split the rows into n-number of chunks and adjust the last chunk
        // accordingly
        for (int i = 0; i < chunks; i++) {
            if ((i + 1) == chunks) {
                System.out.println("i：" + i + "，chunkSize：" + chunkSize +
                        "，i * chunkSize：" + (i * chunkSize) + "，count：" + count);
            } else {
                System.out.println("i：" + i + "，chunkSize：" + chunkSize +
                        "，i * chunkSize：" + i * chunkSize +
                        "，(i * chunkSize) + chunkSize：" + ((i * chunkSize) + chunkSize));
            }
        }
    }

    static class SplitBean {
        long start;
        long end;

        public SplitBean(long start, long end) {
            this.start = start;
            this.end = end;
        }

        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getEnd() {
            return end;
        }

        public void setEnd(long end) {
            this.end = end;
        }
    }
}