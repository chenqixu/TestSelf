package com.cqx.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * FutureTools
 *
 * @author chenqixu
 */
public class FutureTools {

    private static Logger logger = LoggerFactory.getLogger(FutureTools.class);
    private HdfsToolFactory hdfsToolFactory;

    public FutureTools(HdfsToolFactory hdfsToolFactory) {
        this.hdfsToolFactory = hdfsToolFactory;
    }

    public FutureTask submit() {
        CallableTools callableTools = new CallableTools();
        FutureTask futureTask = new FutureTask<>(callableTools);
        new Thread(futureTask).start();
        return futureTask;
    }

    class CallableTools implements Callable<Boolean> {

        private boolean flag = false;

        public void interrupt() {
            flag = true;
        }

        @Override
        public Boolean call() throws Exception {
            createLoop("step1", 100000);
            return null;
        }

        private void copyFile() throws IOException {
            String hdfsDst = "/tmp/test/dpi/a.txt";
            String localDst = "file:///d:/tmp/data/dpi/a.txt";
            hdfsToolFactory.copyFromLocalFile(localDst, hdfsDst);
        }

        private void createLoop(String step, int length) {
            for (int i = 0; i < length; i++) {
                if (Thread.currentThread().isInterrupted()) break;
                if (i % 9 == 0)
                    logger.info("{} {} {}", step, i, Thread.currentThread().isInterrupted());
            }
        }
    }

}
