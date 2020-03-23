package com.cqx.yaoqi.work;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ThreadManagermentTest {

    private static final MyLogger logger = MyLoggerFactory.getLogger(ThreadManagermentTest.class);

    @Test
    public void exec() {
        Map<String, String> map = new HashMap<>();
        map.put("a", "1");
        map.put("b", "2");
        map.put("c", "2");
        map.put("d", "2");
        map.put("e", "1");
        Iterator<String> iterator = map.values().iterator();
        while (iterator.hasNext()) {
            String bookWork = iterator.next();
            if (bookWork.equals("2"))
                iterator.remove();
        }
        logger.info(map.toString());
    }

    @Test
    public void scanLocalFile() {
        logger.info(new ThreadManagerment().scanLocalFile().toString());
    }
}