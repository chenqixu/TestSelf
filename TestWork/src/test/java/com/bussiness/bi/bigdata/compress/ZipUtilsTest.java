package com.bussiness.bi.bigdata.compress;

import com.bussiness.bi.bigdata.compress.Zip4jUtils;
import com.bussiness.bi.bigdata.compress.ZipUtils;
import net.lingala.zip4j.exception.ZipException;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

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
}