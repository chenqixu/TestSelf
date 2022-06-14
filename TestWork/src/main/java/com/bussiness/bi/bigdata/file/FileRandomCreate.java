package com.bussiness.bi.bigdata.file;

import com.cqx.common.utils.string.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * 随机生成文件
 *
 * @author chenqixu
 */
public class FileRandomCreate {
    private String split = "_";
    // 文件名示范：LTE_S1URTSP_008398695002_20190411083717.txt
    // LTE固定
    // S1URTSP、S1UHTTP、S1UOTHER随机3选1
    // 008398695002随机数字
    // 20190411083717时间随机
    // .txt固定
    // _分隔符固定

    private IRule[] rules = {new First(), new Second(), new Thrid(), new Four()};

    public String create() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < rules.length; i++) {
            sb.append(rules[i].create());
            if (i < (rules.length - 1)) sb.append(split);
        }
        sb.append(".txt");
        return sb.toString();
    }

    public void createFile(String fileName) throws IOException {
//        System.out.println("create file：" + fileName);
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    interface IRule {
        String create();
    }

    class First implements IRule {
        @Override
        public String create() {
            return "LTE";
        }
    }

    class Second implements IRule {
        String[] values = {"S1URTSP", "S1UHTTP", "S1UOTHER"};
        Random random = new Random();

        @Override
        public String create() {
//            return values[random.nextInt(2)];
            return "S1MME";
        }
    }

    class Thrid implements IRule {
        Random random = new Random();

        @Override
        public String create() {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 12; i++) {
                sb.append(random.nextInt(9));
            }
            return sb.toString();
        }
    }

    class Four implements IRule {
        // 20190411083717
        Random random = new Random();

        @Override
        public String create() {
            StringBuffer sb = new StringBuffer();
            sb.append("201905");
            int day = random.nextInt(30);
            int hour = random.nextInt(23);
            int minute = random.nextInt(59);
            int seconds = random.nextInt(59);
            sb.append(StringUtil.fillZero(day, 2));
            sb.append(StringUtil.fillZero(hour, 2));
            sb.append(StringUtil.fillZero(minute, 2));
            sb.append(StringUtil.fillZero(seconds, 2));
            return sb.toString();
        }
    }
}
