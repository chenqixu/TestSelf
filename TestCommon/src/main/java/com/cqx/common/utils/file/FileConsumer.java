package com.cqx.common.utils.file;

import com.cqx.common.utils.thread.BaseCallable;
import com.cqx.common.utils.thread.ExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件消费者
 *
 * @author chenqixu
 */
public class FileConsumer implements Closeable {
    /**
     * 每次最大获取值
     */
    public static final String MAX_POLL_SIZE = "max.poll.size";
    private static final Logger logger = LoggerFactory.getLogger(FileConsumer.class);
    private ExecutorFactory<FileRecord> executorFactory;
    private IRead iRead;
    private Map params;

    /**
     * BufferedReaderMode模式
     * <p>
     * defaultParams参数
     */
    public FileConsumer() {
        this("BufferedReaderMode", defaultParams());
    }

    /**
     * BufferedReaderMode模式
     *
     * @param params 自定义参数
     */
    public FileConsumer(Map params) {
        this("BufferedReaderMode", params);
    }

    /**
     * defaultParams参数
     *
     * @param model 模式 [BufferedReaderMode|BufferedReaderMode]
     */
    public FileConsumer(String model) {
        this(model, defaultParams());
    }

    /**
     * 根据模式、自定义参数进行构造
     *
     * @param model  模式 [BufferedReaderMode|BufferedReaderMode]
     * @param params 自定义参数
     */
    public FileConsumer(String model, Map params) {
        executorFactory = ExecutorFactory.newInstance(1);
        this.params = params;
        if (model.equals("BaseRandomAccessFileMode")) {
            iRead = new BaseRandomAccessFileMode();
        } else {
            iRead = new BufferedReaderMode();
        }
    }

    /**
     * 提供默认参数
     * <p>
     *
     * @return
     * @see FileConsumer#MAX_POLL_SIZE
     */
    public static Map defaultParams() {
        Map defaultParams = new HashMap();
        defaultParams.put(MAX_POLL_SIZE, 4000);
        return defaultParams;
    }

    /**
     * 初始化
     *
     * @param fileName 文件名
     * @throws IOException
     */
    public void init(String fileName) throws IOException {
        if (iRead != null) {
            iRead.init(fileName, params);
        }
    }

    /**
     * 消费
     *
     * @param timeout
     * @return
     */
    public List<FileRecord> poll(long timeout) {
        if (executorFactory.hasTask()) {
            executorFactory.submit(timeout);
            return executorFactory.get();
        }
        return new ArrayList<>();
    }

    /**
     * 提交偏移量
     *
     * @param offset
     * @return
     * @throws IOException
     */
    public boolean commit(long offset) throws IOException {
        if (iRead != null) {
            logger.info("commit：{}", offset);
            return iRead.commit(offset);
        }
        return false;
    }

    /**
     * 资源释放
     */
    @Override
    public void close() {
        executorFactory.stop();
        if (iRead != null) {
            iRead.close();
        }
    }

    /**
     * 文件读取接口
     * <pre>
     *     需要实现
     *     void init(String fileName, Map params);
     *     boolean commit(long offset);
     *     void close();
     *     三个方法
     * </pre>
     */
    interface IRead {
        void init(String fileName, Map params) throws IOException;

        boolean commit(long offset) throws IOException;

        void close();
    }

    /**
     * 随机到达文件模式，可以移动文件位置
     */
    class BaseRandomAccessFileMode implements IRead {
        private BaseRandomAccessFile baseRandomAccessFile;
        private TagFile tagFile;

        @Override
        public void init(String fileName, Map params) throws IOException {
            baseRandomAccessFile = new BaseRandomAccessFile(fileName);
            String tagFileName = fileName + ".tag";
            // 判断tag文件是否存在，如果存在，则读取数据并移动到上次commit位置
            if (FileUtil.isExists(tagFileName)) {
                tagFile = new TagFile(tagFileName);
                long tag = tagFile.readTag();
                baseRandomAccessFile.seek(tag);
                logger.info("seek to tag：{}", tag);
            } else {
                tagFile = new TagFile(tagFileName);
            }
            // 读取文件任务
            executorFactory.add(new BaseCallable<FileRecord>() {
                @Override
                public FileRecord exec() throws Exception {
                    String value = baseRandomAccessFile.readLine();
                    if (value == null) {
                        return null;
                    } else {
                        return new FileRecord(baseRandomAccessFile.getFilePointer(), value);
                    }
                }
            }, params);
        }

        @Override
        public boolean commit(long offset) throws IOException {
            return tagFile.writeTag(offset);
        }

        @Override
        public void close() {
            if (baseRandomAccessFile != null) {
                try {
                    baseRandomAccessFile.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (tagFile != null) {
                tagFile.close();
            }
        }
    }

    /**
     * Reader读取模式，只能从头开始
     */
    class BufferedReaderMode implements IRead {
        private BufferedReader reader;
        private TagFile tagFile;
        private long lineCnt = 0L;
        private long lineSkip = 0L;

        @Override
        public void init(String fileName, Map params) throws IOException {
            setReader(fileName, "UTF-8");
            String tagFileName = fileName + ".tag";
            // 判断tag文件是否存在，如果存在，则读取数据并移动到上次commit位置
            if (FileUtil.isExists(tagFileName)) {
                tagFile = new TagFile(tagFileName);
                lineSkip = tagFile.readTag();
                logger.info("lineSkip：{}", lineSkip);
            } else {
                tagFile = new TagFile(tagFileName);
            }
            // 读取文件任务
            executorFactory.add(new BaseCallable<FileRecord>() {
                @Override
                public FileRecord exec() throws Exception {
                    String value = reader.readLine();
                    if (value == null) {
                        return null;
                    } else {
                        lineCnt++;
                        if (lineSkip > 0) {
                            lineSkip--;
                            return null;
                        } else {
                            return new FileRecord(lineCnt, value);
                        }
                    }
                }
            }, params);
        }

        @Override
        public boolean commit(long offset) throws IOException {
            return tagFile.writeTag(offset);
        }

        @Override
        public void close() {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (tagFile != null) {
                tagFile.close();
            }
        }

        private void setReader(String fileName, String read_code) throws FileNotFoundException, UnsupportedEncodingException {
            setReader(new FileInputStream(new File(fileName)), read_code);
        }

        private void setReader(InputStream is, String read_code) throws UnsupportedEncodingException {
            setReader(new BufferedReader(new InputStreamReader(is, read_code)));
        }

        private void setReader(BufferedReader reader) {
            this.reader = reader;
        }
    }
}
