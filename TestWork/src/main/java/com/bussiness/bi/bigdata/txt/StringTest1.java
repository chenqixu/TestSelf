package com.bussiness.bi.bigdata.txt;

import java.net.URLDecoder;
import java.util.HashSet;

public class StringTest1 {
    public static void main(String[] args) {
        String dependence_time = "'$'";
        System.out.println(dependence_time.length());
        dependence_time = "'$-1'";
        System.out.println(dependence_time.length());

        System.out.println(URLDecoder.decode("%E8%8E%86%E7%94%B0%E7%88%B1%E5%AE%B6%E5%85%89%E7%BD%91"));
        System.out.println(URLDecoder.decode("莆田爱家光网"));

        HashSet<String> sceneLacCis = new HashSet<>();
        sceneLacCis.add("1_1");
        sceneLacCis.add("1_2");
        sceneLacCis.add("1_3");
        String inday = sceneLacCis.contains("1_1") ? "1" : "0";
        System.out.println(String.format("inday=%s", inday));
        inday = sceneLacCis.contains("1_2") ? "1" : "0";
        System.out.println(String.format("inday=%s", inday));
        inday = sceneLacCis.contains("1_3") ? "1" : "0";
        System.out.println(String.format("inday=%s", inday));
        inday = sceneLacCis.contains("1_4") ? "1" : "0";
        System.out.println(String.format("inday=%s", inday));
    }
}
