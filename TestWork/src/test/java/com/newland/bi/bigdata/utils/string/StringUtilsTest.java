package com.newland.bi.bigdata.utils.string;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;


public class StringUtilsTest {

    @Test
    public void telnumberProcessing() {
        System.out.println(StringUtils.telnumberProcessing("8613509323824"));
    }

    @Test
    public void printSystemProperties() {
        StringUtils.printSystemProperties();
    }

    @Test
    public void columnSplit() {
        StringBuffer _tmp = new StringBuffer("");
        _tmp.append("1").append(StringUtils.COLUMN_SPLIT);
        _tmp.append("2").append(StringUtils.COLUMN_SPLIT);
        _tmp.append("3").append(StringUtils.COLUMN_SPLIT);
        System.out.println(_tmp.toString());
        _tmp.deleteCharAt(_tmp.length() - 1);
        System.out.println(_tmp.toString());
        System.out.println(_tmp.toString().split(String.valueOf(StringUtils.COLUMN_SPLIT)).length);
    }

    @Test
    public void replaceTest() {
        String rule = "a?b";
        rule = rule.replace('?', '#');
        System.out.println(rule);
    }

    @Test
    public void negativeAssert1() {
        // 创建0到23
        List<String> seqList = StringUtils.generateSeqList(0, 23, 2);
        // 创建9,10,18,20
        List<String> negativeList = Arrays.asList(new String[]{"09", "10", "18", "20"});
        // 剔除，算法1
        StringUtils.negativeAssert1(seqList, negativeList);
        // 打印结果
//        StringUtils.printList(seqList);
        System.out.println(StringUtils.splitList(seqList, "|"));
    }

    @Test
    public void negativeAssert2() {
        // 创建0到23
        List<String> seqList = StringUtils.generateSeqList(0, 23, 2);
        // 创建9,10,18,20
        List<String> negativeList = Arrays.asList(new String[]{"09", "10", "18", "20"});
        // 剔除，算法2
        StringUtils.negativeAssert2(seqList, negativeList);
        // 打印结果
//        StringUtils.printList(seqList);
        System.out.println(StringUtils.splitList(seqList, "|"));
    }
}