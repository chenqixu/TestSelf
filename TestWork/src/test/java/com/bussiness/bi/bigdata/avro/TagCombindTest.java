package com.bussiness.bi.bigdata.avro;

import com.bussiness.bi.bigdata.avro.TagCombind;
import org.junit.Test;

import java.io.IOException;

public class TagCombindTest {

    @Test
    public void readFile() throws IOException {
        new TagCombind().readFile(
                "d:\\Work\\实时\\ADB\\标签大宽表\\1.txt"
                , "\\|"
                , "I:\\Document\\Workspaces\\Git\\TestSelf\\TestWork\\src\\main\\avro\\tag_combind.avsc");
    }

    @Test
    public void readFile2() throws IOException {
        new TagCombind().readFile(
                "d:\\Work\\实时\\ADB\\标签大宽表\\2.txt"
                , "&"
                , "I:\\Document\\Workspaces\\Git\\TestSelf\\TestWork\\src\\main\\avro\\tag_combind.avsc");
    }
}