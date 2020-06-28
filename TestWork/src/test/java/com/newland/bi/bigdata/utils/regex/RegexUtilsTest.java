package com.newland.bi.bigdata.utils.regex;

import org.junit.Test;

public class RegexUtilsTest {

    RegexUtils regexUtils = new RegexUtils();

    @Test
    public void match() {
        regexUtils.match("%[0-9]{2}(YY|MM|DD|HH|II)", "%01DD", false);
        System.out.println("%00DDA".substring(1, 3));
        String ex = "FuJianYiDong-A-IuCS-1-${stat_date}??.txt";
        System.out.println(ex.replaceAll("\\$\\{stat_date}", "2020"));
    }
}