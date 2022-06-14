package com.bussiness.bi.bigdata.parser.java;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FunctionParserTest {
    private static final MyLogger logger = MyLoggerFactory.getLogger(FunctionParserTest.class);

    @Test
    public void run() throws Exception {
        String filename = "D:\\Document\\Workspaces\\Git\\FujianBI\\edc-addressquery\\edc-addressquery-svc\\src\\main\\java\\com\\newland\\bi\\common\\jdbc\\BaseDao.java";
        FunctionParser functionParser = new FunctionParser(filename);
        functionParser.run();
    }

    @Test
    public void listTest() {
        List<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        list.add("789");
        list.remove("456");
        logger.info("{}", list);
    }
}