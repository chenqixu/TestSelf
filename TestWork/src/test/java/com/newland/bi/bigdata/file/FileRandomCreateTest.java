package com.newland.bi.bigdata.file;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FileRandomCreateTest {

    private FileRandomCreate fileRandomCreate;
    private String path = "d:\\tmp\\data\\dpi\\dpi_ltedata\\streaminput\\";

    @Before
    public void setUp() {
        fileRandomCreate = new FileRandomCreate();
    }

    @Test
    public void create() throws Exception {
        Set<String> list = new HashSet<>();
        for (int i = 0; i < 1; i++) {
            list.add(fileRandomCreate.create());
        }
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String filename = it.next();
            fileRandomCreate.createFile(path + filename);
        }
    }
}