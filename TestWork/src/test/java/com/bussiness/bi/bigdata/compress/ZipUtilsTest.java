package com.bussiness.bi.bigdata.compress;

import com.cqx.common.utils.compress.zip.Zip4jUtils;
import com.cqx.common.utils.compress.zip.ZipUtils;
import com.cqx.common.utils.file.FileUtil;
import net.lingala.zip4j.exception.ZipException;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

public class ZipUtilsTest {

    private ZipUtils zipUtils;

    @Before
    public void setUp() {
        zipUtils = new ZipUtils();
    }

    @Test
    public void compress() {
        String srcDir = "d:\\tmp\\data\\zip\\source\\";
        OutputStream out = null;
        try {
            out = new FileOutputStream("d:\\tmp\\data\\zip\\compress\\a.zip");
            boolean KeepDirStructur = true;
            zipUtils.compress(srcDir, out, KeepDirStructur);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void compress4j() {
        String src = "d:\\tmp\\data\\zip\\source\\";
        String dest = "d:\\tmp\\data\\zip\\compress\\a.zip";
        String password = "123456";
        String result = Zip4jUtils.zip(src, dest, password);
        System.out.println(result);
    }

    @Test
    public void uncompress4j() throws ZipException {
        String zip = "d:\\tmp\\data\\zip\\compress\\a.zip";
        String dest = "d:\\tmp\\data\\zip\\uncompress\\";
        String password = "123456";
        Zip4jUtils.unzip(zip, dest, password);
    }

    @Test
    public void findX9Conn() throws Exception {
        int all_cnt = 0;
        int is_x9_cnt = 0;
        for (File file : FileUtil.listFilesEndWith("d:\\tmp\\data\\oracle_blob\\flowtask\\", ".zip")) {
            all_cnt++;
            try (FileInputStream fis = new FileInputStream(file)) {
                String xml = zipUtils.unZip(fis, "node");
//                boolean isX9 = xml.contains("cbass_");
                boolean isX9 = xml.contains("dmbass");
                if (isX9) is_x9_cnt++;
                System.out.println(String.format("[fileName]%s [is cbass_]%s", file.getName(), isX9));
            }
        }
        System.out.println(String.format("[all_cnt]%s [is_x9_cnt]%s", all_cnt, is_x9_cnt));
    }
}