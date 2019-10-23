package com.newland.bi.bigdata.hash;

import com.newland.bi.bigdata.bean.HashCodeFile;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MyHashCode
 *
 * @author chenqixu
 */
public class MyHashCode {

    public static final String SPLIT_STR = "_";
    private int mod;

    public MyHashCode() {
    }

    public MyHashCode(int mod) {
        this.mod = mod;
    }

    public void printHashCode(Object obj) {
        System.out.println(obj.hashCode());
    }

    public String[] createFileName(int num) {
        String[] result = new String[num];
        String head = "S04002";
        String[] bodys = {"20190813120000", "20190813130000", "20190813140000"
                , "20190813150000", "20190813160000", "20190813170000"
                , "20190813180000", "20190813190000", "20190813200000"};
        Random random = new Random();
        Map<String, AtomicInteger> map = new HashMap<>();
        for (int i = 0; i < num; i++) {
            String body = bodys[random.nextInt(bodys.length)];
            AtomicInteger atomicInteger = map.get(body);
            if (atomicInteger == null) {
                atomicInteger = new AtomicInteger();
                map.put(body, atomicInteger);
            }
            int end = atomicInteger.getAndIncrement();
            result[i] = head + SPLIT_STR + body + SPLIT_STR + end;
        }
        return result;
    }

    public List<HashCodeFile> createData(String[] filenames) {
        List<HashCodeFile> hashCodeFileList = new ArrayList<>();
        for (String filename : filenames) {
            hashCodeFileList.add(new HashCodeFile(filename));
        }
        return hashCodeFileList;
    }

    public void grouping(HashCodeFile hashCodeFile, Map<Integer, List<HashCodeFile>> modMap) {
        // 文件分组
        int _mod = Math.abs(hashCodeFile.getFileName().hashCode()) % mod;
        hashCodeFile.setDataIndex(_mod);
        List<HashCodeFile> _modHashCodeFile = modMap.get(_mod);
        if (_modHashCodeFile == null) {
            _modHashCodeFile = new ArrayList<>();
            modMap.put(_mod, _modHashCodeFile);
        }
        _modHashCodeFile.add(hashCodeFile);
    }

    public Map<Integer, List<HashCodeFile>> filterCycle(String cycle, Map<Integer, List<HashCodeFile>> modMap) {
        Map<Integer, List<HashCodeFile>> resultModMap = new HashMap<>();
        for (List<HashCodeFile> hashCodeFileList : modMap.values()) {
            for (HashCodeFile hashCodeFile : hashCodeFileList) {
                if (hashCodeFile.getCycle().contains(cycle)) {
                    grouping(hashCodeFile, resultModMap);
                }
            }
        }
        printMap(resultModMap);
        return resultModMap;
    }

    public void printMap(Map<Integer, List<HashCodeFile>> modMap) {
        // 查看是否能够平均hash
        for (Map.Entry<Integer, List<HashCodeFile>> entry : modMap.entrySet()) {
            System.out.println(entry.getKey() + "，" + entry.getValue().size());
        }
    }
}
