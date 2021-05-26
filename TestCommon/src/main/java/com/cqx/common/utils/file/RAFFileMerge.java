package com.cqx.common.utils.file;

import com.cqx.common.utils.serialize.ISerialization;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.thread.BaseRunable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * RAFFileMerge
 *
 * @author chenqixu
 */
public class RAFFileMerge<T extends Comparable<? super T>> implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(RAFFileMerge.class);
    private final Object atoLock = new Object();
    private final int maxIndex = 5;
    private int index = 0;
    private String filePath;
    private String fileName;
    private long singleFileMaxLength;
    private List<RAFFileMangerCenter<T>> fileList = new ArrayList<>();
    private RAFFileMangerCenter<T> readRaf;
    private boolean isReadOnly;
    private ISerialization<T> iSerialization;

    public RAFFileMerge(ISerialization<T> iSerialization, String filePath, String fileName, long singleFileMaxLength) throws IOException {
        this(iSerialization, filePath, fileName, singleFileMaxLength, false);
    }

    public RAFFileMerge(ISerialization<T> iSerialization, String filePath, String fileName, long singleFileMaxLength, boolean isReadOnly) throws IOException {
        this.iSerialization = iSerialization;
        this.filePath = filePath;
        this.fileName = fileName;
        this.singleFileMaxLength = singleFileMaxLength;
        this.isReadOnly = isReadOnly;
        // 生成第一个文件，并加入列表
        generateFile();
    }

    /**
     * 小文件合并为大文件，在合并过程中需要先对内容排序
     *
     * @param file
     * @param isDelete
     * @throws IOException
     */
    public void merge(RAFFileMangerCenter<T> file, boolean isDelete) throws IOException {
        // 从列表中获取最新文件，并写入，写入的时候需要判断是否进行文件分割
        RAFFileMangerCenter<T> last = getLastFile();
        // 从头读取
        file.seekToBegin();
        List<T> tList = new ArrayList<>();
        RAFBean<T> rafBean;
        // 读到内存
        while (true) {
            if ((rafBean = file.readDeserialize()) != null) {
                if (rafBean.isEnd()) {
                    break;
                } else {
                    tList.add(rafBean.getT());
                }
            } else {
                logger.warn("合并文件异常，读到空值！");
            }
        }
        // 对本次处理对象进行排序
        Collections.sort(tList);
        for (T t : tList) {
            int ret = last.write(t);
            // 需要文件分割
            if (ret == 2) {
                generateFile();
                last = getLastFile();
                last.write(t);
            }
        }
        if (isDelete) {
            file.close();
            logger.info("需要删除的文件：{}，删除结果：{}", file.getFile_name(), file.del());
        }
    }

    /**
     * 从大文件中读取记录
     *
     * @param timeOut
     * @return
     */
    public List<T> read(long timeOut) {
        if (readRaf == null) {
            getReadFile();
        }
        List<T> content = null;
        if (readRaf != null) {
            ReadRunable readRunable = new ReadRunable();
            Thread thread = new Thread(readRunable);
            thread.start();
            SleepUtil.sleepMilliSecond(timeOut);
            readRunable.stop();
            try {
                thread.join();
            } catch (InterruptedException e) {
                //
            }
            content = readRunable.get();
        }
        return content;
    }

    private void getReadFile() {
        synchronized (atoLock) {
            RAFFileMangerCenter raf = (fileList.size() > 0 ? fileList.get(0) : null);
            // new一个表示从头读，这里使用读写分离模式
            if (raf != null) {
                try {
                    readRaf = new RAFFileMangerCenter<>(iSerialization, raf.getFile_name());
                } catch (FileNotFoundException e) {
                    throw new NullPointerException(e.getMessage());
                }
            }
        }
    }

    private void changeReadRAFFile() throws IOException {
        synchronized (atoLock) {
            if (fileList.size() > 1) {
                fileList.remove(0);
                readRaf.close();
                logger.info("文件读取完成：{}，删除：{}", readRaf.getFile_name(), readRaf.del());
                getReadFile();
            } else {// 需要回滚到END_TAG
                readRaf.seekToEndTag();
            }
        }
    }

    private RAFFileMangerCenter<T> getLastFile() {
        RAFFileMangerCenter<T> raf = null;
        synchronized (atoLock) {
            if (fileList.size() > 0) raf = fileList.get(fileList.size() - 1);
        }
        return raf;
    }

    private void generateFile() throws IOException {
        synchronized (atoLock) {
            // 关闭上个文件
            RAFFileMangerCenter<T> last = getLastFile();
            if (last != null) last.close();
            if (index > maxIndex) index = 0;
            String newFileName = filePath + fileName + index++;
            fileList.add(new RAFFileMangerCenter<>(iSerialization, newFileName, singleFileMaxLength));
            logger.info("新文件：{}", newFileName);
        }
    }

    @Override
    public void close() {
        if (readRaf != null) {
            try {
                readRaf.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        for (RAFFileMangerCenter raf : fileList) {
            try {
                raf.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            if (!isReadOnly)// 只读，不删大文件
                logger.info("删除大文件：{}，删除结果：{}", raf.getFile_name(), raf.del());
        }
    }

    private class ReadRunable extends BaseRunable {
        List<T> results = new ArrayList<>();

        @Override
        public void exec() throws Exception {
            RAFBean<T> msg;
            try {
                if ((msg = readRaf.readDeserialize()) != null) {
                    if (msg.isEnd()) {
                        // 切换文件
                        changeReadRAFFile();
                        return;
                    }
                    results.add(msg.getT());
                }
            } catch (Exception e) {
                logger.error(readRaf.getFile_name() + "，" + e.getMessage(), e);
            }
        }

        public List<T> get() {
            return results;
        }
    }
}
