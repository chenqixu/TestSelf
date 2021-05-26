package com.cqx.work.stream;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CollectorUtil
 *
 * @author chenqixu
 */
public class CollectorUtil {

    public void testColllector() {
        List<Pair<String, Double>> pairArrayList = new ArrayList<>(3);
        pairArrayList.add(new Pair<>("version", 6.19));
        pairArrayList.add(new Pair<>("version", 10.24));
        pairArrayList.add(new Pair<>("version", 13.14));
        List<Integer> numList = new ArrayList<>();
        numList.add(1);
        numList.add(1);
        numList.add(2);
        long count = numList.stream().distinct().count();
        numList.stream().limit(2).forEach(System.out::println);
        Map<String, Double> map = pairArrayList.stream().collect(
                Collectors.toMap(Pair::getKey, Pair::getValue, (v1, v2) -> v2));
        System.out.println(map);
        System.out.println(count);
    }
}
