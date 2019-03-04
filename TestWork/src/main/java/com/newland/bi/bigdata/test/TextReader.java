package com.newland.bi.bigdata.test;

import java.util.HashMap;
import java.util.Map;

/**
 * TextReader
 *
 * @author chenqixu
 */
public class TextReader extends AbsReader implements IReader {

    static {
        try {
            ReaderFactory.registerReader("text", TextReader.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TextReader() {
    }

    public TextReader(String msg) {
        System.out.println("TextReader：" + msg);
        throw new UnsupportedOperationException("TextReader UnsupportedOperationException.");
    }

    public static void main(String[] args) {
        Map<String, Class<? extends AbsReader>> classMap = new HashMap<>();
        classMap.put("TextReader", TextReader.class);
        try {
            TextReader.class.newInstance().generate("aaa");
//            classMap.get("TextReader").newInstance().generate("aaa");
//            ((AbsReader) classMap.get("TextReader")).builder("aaa");
//            TextReader textReader = new TextReader();
//            textReader.builder("aaa");
        } catch (UnsupportedOperationException e) {
            System.out.println("catch UnsupportedOperationException!");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("catch Exception!");
            e.printStackTrace();
        }
    }

    @Override
    public IReader generate(String msg) throws Exception {
        System.out.println("generate：" + msg);
        return new TextReader(msg);
    }
}
