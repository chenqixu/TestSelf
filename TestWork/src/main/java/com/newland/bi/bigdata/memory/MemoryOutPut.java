package com.newland.bi.bigdata.memory;

import org.apache.solr.common.SolrInputDocument;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MemoryOutPut {

    public static void main(String[] args) {
        listTest();
    }

    public static void listTest() {
        Runtime r = Runtime.getRuntime();
        r.gc();
        long startMem = r.freeMemory(); // 开始时的剩余内存
        List<SolrInputDocument> docs = null;
        docs = new LinkedList<SolrInputDocument>();
        for (int i = 0; i < 100000; i++) {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "0000|20181129213417|13779972882|LTE");
            doc.addField("msisdn", "13779972882");
            docs.add(doc);
        }
        long orz = startMem - r.freeMemory(); // 剩余内存 现在 - 开始 = o 的大小
        System.out.println("orz：" + orz);
    }

    /**
     * 溢出测试
     */
    public static void outTest() {
        // Set<Person> set = new HashSet<Person>();
        // Person p1 = new Person("唐僧","pwd1",25);
        // Person p2 = new Person("孙悟空","pwd2",26);
        // Person p3 = new Person("猪八戒","pwd3",27);
        // System.out.println(p3.hashCode());
        // set.add(p1);
        // set.add(p2);
        // set.add(p3);
        // System.out.println("总共有:"+set.size()+" 个元素!"); //结果：总共有:3 个元素!
        // p3.setAge(2); //修改p3的年龄,此时p3元素对应的hashcode值发生改变
        // System.out.println(p3.hashCode());
        //
        // // set.remove(p3); //此时remove不掉，造成内存泄漏
        //
        // set.add(p3); //重新添加，居然添加成功
        // System.out.println("总共有:"+set.size()+" 个元素!"); //结果：总共有:4 个元素!
        // for (Person person : set){
        // System.out.println(person.hashCode());
        // }

        Map<Key, String> map = new HashMap<Key, String>(1000);

        int counter = 0;
        while (true) {
            // creates duplicate objects due to bad Key class
            map.put(new Key("dummyKey"), "value");
            counter++;
            if (counter % 1000 == 0) {
                System.out.println("map size: " + map.size());
                System.out.println("Free memory after count " + counter + " is " + getFreeMemory() + "MB");
                sleep(1000);
            }
        }
    }

    /**
     * delay for a given period in milli seconds
     *
     * @param sleepFor
     */
    public static void sleep(long sleepFor) {
        try {
            Thread.sleep(sleepFor);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * get available memory in MB
     *
     * @return
     */
    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory() / (1024 * 1024);
    }

    /**
     * inner class key without hashcode() or equals() -- bad implementation
     */
    static class Key {
        private String key;

        public Key(String key) {
            this.key = key;
        }
    }
}
