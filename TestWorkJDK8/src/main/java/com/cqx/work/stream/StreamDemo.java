package com.cqx.work.stream;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * StreamDemo<br>
 * <pre>
 *     延迟方法：该类方法调用后，会返回一个【新的Stream对象】，并且不会执行任何的业务操作
 *     filter -- 实现数据过滤
 *     map -- 将一种数据类型的流转成另一种数据类型的流
 *     skip -- 跳过前几个
 *     limit -- 取前几个
 *     Stream.concat() -- 合并流对象
 *
 *     终结方法：该类方法调用后，不会返回Stream流对象，会触发之前所有的业务操作
 *     forEach  --- 实现遍历
 *     count -- 实现计数
 *     collect -- 将流中的数据重新转为集合
 * </pre>
 *
 * @author chenqixu
 */
public class StreamDemo {
    private static final Logger logger = LoggerFactory.getLogger(StreamDemo.class);
    private Random random = new Random(System.currentTimeMillis());

    public static void main(String[] args) {
        StreamDemo demo = new StreamDemo();
        demo.listToStreamAndOperate();
        demo.Aggregate();
        demo.Grouping();
        demo.partitioning();
        demo.streamToCollect();
        demo.streamToArray();
        demo.iterate();
    }

    public void listToStreamAndOperate() {
        logger.info("[listToStreamAndOperate] start=============");
        List<Integer> numList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            numList.add(random.nextInt(15));
        }
        logger.info("[listToStreamAndOperate] list: {}", numList);

        //==========================
        // count
        //==========================
        long count = buildIntegerStream(numList).count();
        logger.info("[listToStreamAndOperate] count, result: {}", count);

        //==========================
        // distinct
        //==========================
        long distinctCount = buildIntegerStream(numList).distinct().count();
        logger.info("[listToStreamAndOperate] distinct, result: {}", distinctCount);

        //==========================
        // forEach and print
        //==========================
        logger.info("[listToStreamAndOperate] forEach and print");
        buildIntegerStream(numList).forEach(System.out::println);

        //==========================
        // limit and forEach and print
        //==========================
        logger.info("[listToStreamAndOperate] limit and forEach and print");
        buildIntegerStream(numList).limit(5).forEach(System.out::println);

        //==========================
        // join
        //==========================
        String join1 = buildIntegerStream(numList).map(s -> s + "").collect(Collectors.joining());
        String join2 = buildIntegerStream(numList).map(s -> s + "").collect(Collectors.joining(","));
        String join3 = buildIntegerStream(numList).map(s -> s + "").collect(Collectors.joining(",", "[", "]"));
        logger.info("[listToStreamAndOperate] join1: {}", join1);
        logger.info("[listToStreamAndOperate] join2: {}", join2);
        logger.info("[listToStreamAndOperate] join3: {}", join3);

        logger.info("[listToStreamAndOperate] stop=============");
    }

    /**
     * 聚合操作
     */
    public void Aggregate() {
        logger.info("[Aggregate] start=============");
        List<Integer> numList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            numList.add(random.nextInt(10));
        }
        logger.info("[Aggregate] list: {}", numList);

        //==========================
        // Aggregate Operation
        //==========================
        logger.info("[Aggregate] ====Aggregate Operation====");
        //==========================
        // max
        //==========================
        Optional<Integer> max = buildIntegerStream(numList).max(Comparator.comparingInt(c -> c));
        logger.info("[Aggregate] max, result[orElse -1]: {}", max.orElse(-1));

        //==========================
        // min
        //==========================
        Optional<Integer> min = buildIntegerStream(numList).min(Comparator.comparingInt(c -> c));
        logger.info("[Aggregate] min, result[orElse -1]: {}", min.orElse(-1));

        //==========================
        // sum
        //==========================
        int sum = buildIntegerStream(numList).mapToInt(Integer::intValue).sum();
        logger.info("[Aggregate] sum, result: {}", sum);

        //==========================
        // average
        //==========================
        OptionalDouble average = buildIntegerStream(numList).mapToInt(Integer::intValue).average();
        logger.info("[Aggregate] average, result[orElse -1]: {}", average.orElse(-1));

        logger.info("[Aggregate] stop=============");
    }

    /**
     * 分组操作
     */
    public void Grouping() {
        logger.info("[Grouping] start=============");

        String[] names = {"小红", "小张", "小陈", "小林", "小微", "小伟", "小魏", "小洪", "小宏", "小虹", "小李", "小丽", "小莉", "小黎"};
        List<Pair<String, Pair<Integer, Integer>>> pairArrayList = new ArrayList<>();
        for (String name : names) {
            pairArrayList.add(new Pair<>(name, new Pair<>(random.nextInt(10), random.nextInt(100))));
        }
        logger.info("[Grouping] pairArrayList: {}", pairArrayList);

        //==========================
        // Grouping Operation
        //==========================
        logger.info("[Grouping] ====Grouping Operation====");
        // 1.按照具体年龄分组
        logger.info("[Grouping] 按照具体年龄分组");
        Map<Integer, List<Pair<String, Pair<Integer, Integer>>>> map1 = pairArrayList.stream().collect(
                Collectors.groupingBy((s -> s.getValue().getKey())));
        map1.forEach((key, value) -> System.out.println(key + "---->" + value));

        // 2.按照分数分多个组：优秀>=90、良好>=80、及格>=60、差>=40、极差<40
        logger.info("[Grouping] 按照分数分多个组：优秀>=90、良好>=80、及格>=60、差>=40、极差<40");
        Map<String, List<Pair<String, Pair<Integer, Integer>>>> map2 = pairArrayList.stream().collect(
                Collectors.groupingBy(s -> {
                    if (s.getValue().getValue() >= 90) {
                        return "优秀";
                    } else if (s.getValue().getValue() >= 80) {
                        return "良好";
                    } else if (s.getValue().getValue() >= 60) {
                        return "及格";
                    } else if (s.getValue().getValue() >= 40) {
                        return "差";
                    } else {
                        return "极差";
                    }
                }));
        map2.forEach((key, value) -> System.out.println(key + "---->" + value));

        // 3.按照年龄分组,规约求每组的最大值最小值(规约：reducing)
        logger.info("[Grouping] 按照年龄分组,规约求每组分数最大值(规约：reducing)");
        Map<Integer, Optional<Pair<String, Pair<Integer, Integer>>>> reducingMax = pairArrayList.stream().collect(
                Collectors.groupingBy((s -> s.getValue().getKey())
                        , Collectors.reducing(BinaryOperator.maxBy(Comparator.comparingInt(a -> a.getValue().getValue()))))
        );
        reducingMax.forEach((key, value) -> System.out.println(key + "---->" + value));
        logger.info("[Grouping] 按照年龄分组,规约求每组分数最小值(规约：reducing)");
        Map<Integer, Optional<Pair<String, Pair<Integer, Integer>>>> reducingMin = pairArrayList.stream().collect(
                Collectors.groupingBy((s -> s.getValue().getKey())
                        , Collectors.reducing(BinaryOperator.minBy(Comparator.comparingInt(a -> a.getValue().getValue()))))
        );
        reducingMin.forEach((key, value) -> System.out.println(key + "---->" + value));

        // 多级分组
        logger.info("[Grouping] 多级分组，先按年龄分组，再按分数分组");
        Map<Integer, Map<Integer, Map<String, List<Pair<String, Pair<Integer, Integer>>>>>> mapMultistage =
                pairArrayList.stream().collect(
                        Collectors.groupingBy((s -> s.getValue().getKey())
                                , Collectors.groupingBy(s -> s.getValue().getValue(), Collectors.groupingBy((s) -> {
                                    if (s.getValue().getValue() >= 60) {
                                        return "及格";
                                    } else {
                                        return "不及格";
                                    }
                                }))));
        mapMultistage.forEach((key, value) -> {
            System.out.println("年龄:" + key);
            value.forEach((k2, v2) -> System.out.println("\t" + v2));
        });

        logger.info("[Grouping] stop=============");
    }

    /**
     * 分区操作
     */
    public void partitioning() {
        logger.info("[partitioning] start=============");

        String[] names = {"小红", "小张", "小陈", "小林", "小微", "小伟", "小魏", "小洪", "小宏", "小虹", "小李", "小丽", "小莉", "小黎"};
        List<Pair<String, Pair<Integer, Integer>>> pairArrayList = new ArrayList<>();
        for (String name : names) {
            pairArrayList.add(new Pair<>(name, new Pair<>(random.nextInt(10), random.nextInt(100))));
        }
        logger.info("[partitioning] pairArrayList: {}", pairArrayList);
        logger.info("[partitioning] 按照分数>=60 分为\"及格\"一区  <60 分为\"不及格\"一区");
        Map<Boolean, List<Pair<String, Pair<Integer, Integer>>>> map2 = pairArrayList.stream().collect(
                Collectors.partitioningBy(s -> s.getValue().getValue() >= 60));
        map2.forEach((key, value) -> System.out.println(key + "---->" + value));

        logger.info("[partitioning] stop=============");
    }

    /**
     * 收集流中的数据到集合中
     */
    public void streamToCollect() {
        logger.info("[streamToCollect] start=============");
        String[] values = {"aaa", "bbb", "ccc", "bbb"};

        // 1.收集流中的数据到 list
        List<String> list = buildStringStream(values).collect(Collectors.toList());
        logger.info("[streamToCollect] List<String>: {}", list);

        // 2.收集流中的数据到 set
        Set<String> collect = buildStringStream(values).collect(Collectors.toSet());
        logger.info("[streamToCollect] Set<String>: {}", collect);

        // 3.收集流中的数据(ArrayList)(不收集到list,set等集合中,而是)收集到指定的集合中
        ArrayList<String> arrayList = buildStringStream(values).collect(Collectors.toCollection(ArrayList::new));
        logger.info("[streamToCollect] ArrayList<String>: {}", arrayList);

        // 4.收集流中的数据到 HashSet
        HashSet<String> hashSet = buildStringStream(values).collect(Collectors.toCollection(HashSet::new));
        logger.info("[streamToCollect] HashSet<String>: {}", hashSet);

        logger.info("[streamToCollect] stop=============");
    }

    public void streamToArray() {
        logger.info("[streamToArray] start=============");
        String[] values = {"aaa", "bbb", "ccc", "bbb"};

        // 1.使用 toArray()无参
        Object[] objects = buildStringStream(values).toArray();
        for (Object o : objects) {
            // 此处无法使用.length() 等方法
            logger.info("[streamToArray] data: {}", o);
        }

        // 2.使用有参返回指定类型数组
        // 无参不好的一点就是返回的是 Object[] 类型,操作比较麻烦.想要拿到长度，Object是拿不到长度的
        String[] strings = buildStringStream(values).toArray(String[]::new);
        for (String str : strings) {
            logger.info("[streamToArray] data: {}, length: {}", str, str.length());
        }

        logger.info("[streamToArray] stop=============");
    }

    /**
     * 关于无限流的使用
     */
    public void iterate() {
        logger.info("[iterate] start=============");
        logger.info("[iterate] [int]seed 1，n -> n + 1，limit 5=============");
        Stream.iterate(1, n -> n + 1).limit(5).forEach(System.out::println);

        logger.info("[iterate] [string]seed 1，n -> n + 0，limit 5=============");
        Stream.iterate("1", n -> n + "0").limit(5).forEach(System.out::println);

        logger.info("[iterate] [string]seed 1，!\"1000\".equals(a), n -> n + 0，limit 5=============");
        Stream.iterate("1", (a) -> {
            if (!"1000".equals(a)) return a + "0";
            else return null;
        }).limit(5).forEach(System.out::println);

        logger.info("[iterate] stop=============");
    }

    /**
     * 通过数组构造Stream流<br>
     * Stream 流，无法复用，执行一次就到数据的末尾了，不能从头再读
     *
     * @param values
     * @return
     */
    private Stream<String> buildStringStream(String[] values) {
        return Stream.of(values);
    }

    /**
     * 通过List构造Stream<br>
     * Stream 流，无法复用，执行一次就到数据的末尾了，不能从头再读
     *
     * @param values
     * @return
     */
    private Stream<String> buildStringStream(List<String> values) {
        return values.stream();
    }

    private Stream<Integer> buildIntegerStream(List<Integer> values) {
        return values.stream();
    }
}
