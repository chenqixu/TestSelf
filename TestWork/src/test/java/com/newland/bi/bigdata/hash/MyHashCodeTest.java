package com.newland.bi.bigdata.hash;

import com.newland.bi.bigdata.bean.HashCodeFile;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyHashCodeTest {

    MyHashCode myHashCode = new MyHashCode(5);

    @Test
    public void printHashCode() {
        // 生产一批对象，周期分布不均匀
        String[] filenames = myHashCode.createFileName(204);
        List<HashCodeFile> hashCodeFileList = myHashCode.createData(filenames);
        // 分组
        Map<Integer, List<HashCodeFile>> modMap = new HashMap<>();
        for (HashCodeFile hashCodeFile : hashCodeFileList) {
            myHashCode.grouping(hashCodeFile, modMap);
        }
        // 查看是否能够平均hash
        myHashCode.printMap(modMap);
        // 查看分区下的数据是否能够平均hash
        String[] bodys = {"20190813120000", "20190813130000", "20190813140000"
                , "20190813150000", "20190813160000", "20190813170000"
                , "20190813180000", "20190813190000", "20190813200000"};
        for (String body : bodys) {
            myHashCode.filterCycle(body, modMap);
        }
    }
}