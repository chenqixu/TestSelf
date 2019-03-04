package com.newland.bi.bigdata.file;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.TeeInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * PipeHandle
 *
 * @author chenqixu
 */
public class PipeHandle {

    public static final String FILESEPARATOR = System.getProperty("file.separator");
    public static final String FILENAMESUFFIX = ".txt";
    public static final String BAKFILENAMESUFFIX = ".bak";
    public static final int DEFAULT_BUFF_SIZE = 4 * 1024 * 1024; // 4M缓冲
    public static final String CHARSET = "UTF-8";
    public static final long SLEEP_TIME = 500;
    public static Logger log = LoggerFactory.getLogger(PipeHandle.class);
    private Map<String, File> fileMap;
    //内容队列
    private BlockingQueue<FileInfo> fileQueue = null;
    //用于同步
    private BlockingQueue<FileInfo> dealQueue = null;

    private CreateFileServer createFileServer;
    private ScanServer scanServer;
    private DealServerDeamon dealServerDeamon;

    private PipeHandle() {
        init();
    }

    public static PipeHandle builder() {
        return new PipeHandle();
    }

    public static void main(String[] args) throws IOException {
        PipeHandle pipeHandle = PipeHandle.builder();
        String createpath = args[0];
        int createrate = Integer.valueOf(args[1]);
        String copypath = args[2];
        pipeHandle.createFile(createpath, createrate);
        pipeHandle.pipeCopy(createpath, copypath);
        sleepSecond(3);
        pipeHandle.close();
        sleepSecond(1);
    }

    public static String appendFilePath(String path) {
        if (path.endsWith(FILESEPARATOR)) {
            return path;
        } else {
            return path + FILESEPARATOR;
        }
    }

    /**
     * 压抑异常的sleep
     *
     * @param timeout 几秒
     */
    public static void sleepSecond(long timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }

    public void init() {
        fileMap = new HashMap<>();
        fileQueue = new LinkedBlockingQueue<>();
        dealQueue = new LinkedBlockingQueue<>();
    }

    /**
     * 输入目录filepath，创建filecount个文件
     *
     * @param filepath
     * @param filecount
     * @throws IOException
     */
    public void createFile(String filepath, int filecount) throws IOException {
        createFileServer = new CreateFileServer(filepath, filecount);
        createFileServer.start();
    }

    public void close() {
        createFileServer.close();
        scanServer.close();
        dealServerDeamon.closeServer();
    }

    public void pipeCopy(String scanPath, String bakPath) {
        IScan iScan = new LocalScan();
        scanServer = new ScanServer(scanPath, iScan);
        scanServer.start();
        dealServerDeamon = new DealServerDeamon();
        dealServerDeamon.startServer(2, bakPath);
    }

    /**
     * 开并行，不停的使用管道把文件进行复制
     * 方式1：扫描文件目录，从本地获取文件
     * 方式2：扫描FTP服务器目录，从FTP服务器获取文件
     */
    interface IScan {
        List<FileInfo> listFile(String path) throws Exception;
    }

    static class FileInfo {
        private String filename;
        private String fullfilename;
        private InputStream inputStream;

        private FileInfo() {
        }

        public static FileInfo builder() {
            return new FileInfo();
        }

        public String getFilename() {
            return filename;
        }

        public FileInfo setFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public FileInfo setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public String getFullfilename() {
            return fullfilename;
        }

        public FileInfo setFullfilename(String fullfilename) {
            this.fullfilename = fullfilename;
            return this;
        }

        @Override
        public String toString() {
            return filename;
        }
    }

    class CreateFileServer {
        private boolean flag = true;
        private int filecount = 0;
        private String filepath;

        public CreateFileServer(String filepath, int filecount) {
            this.filepath = filepath;
            this.filecount = filecount;
        }

        private boolean checkStatus() {
            return this.filepath != null && this.filecount > 0;
        }

        public void start() {
            if (checkStatus())
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        log.info("start CreateFileServer，" + this);
                        int i = 0;
                        while (flag) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                log.error(e.getMessage(), e);
                            }
                            int count = filecount;
                            while (count-- > 0) {
                                String filename = appendFilePath(filepath) + i + FILENAMESUFFIX;
                                log.debug("CreateFileServer：{}", filename);
                                File file = new File(filename);
                                if (!file.exists()) {
                                    try {
                                        file.createNewFile();
                                        OutputStream fileOutputStream = null;
                                        try {
                                            fileOutputStream = new FileOutputStream(file);
                                            fileOutputStream.write("123".getBytes());
                                        } finally {
                                            if (fileOutputStream != null)
                                                fileOutputStream.close();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                i++;
                            }
                        }
                    }
                }).start();
        }

        public void close() {
            this.flag = false;
        }
    }

    class LocalScan implements IScan {
        @Override
        public List<FileInfo> listFile(String path) {
            List<FileInfo> inputStreamList = new ArrayList<>();
            for (String fileName : new File(path).list()) {
                String filepath = appendFilePath(path);
                String fullFileName = filepath + fileName;
                File file = new File(fullFileName);
                log.debug(String.format("LocalScan file：%s，exists：%s，isFile：%s",
                        fullFileName, String.valueOf(file.exists()), String.valueOf(file.isFile())));
                if (file.exists() && file.isFile()) {
                    try {
                        inputStreamList.add(
                                FileInfo.builder()
                                        .setFilename(fileName)
                                        .setFullfilename(fullFileName)
                                        .setInputStream(new FileInputStream(file)));
                    } catch (FileNotFoundException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            return inputStreamList;
        }
    }

    class FtpScan implements IScan {
        @Override
        public List<FileInfo> listFile(String path) {
            return null;
        }
    }

    class ScanServer {
        private String path;
        private IScan iScan;
        private boolean flag = true;

        public ScanServer(String path, IScan iScan) {
            this.path = path;
            this.iScan = iScan;
        }

        private boolean checkStatus() {
            return this.path != null && this.iScan != null;
        }

        public void start() {
            if (checkStatus()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        log.info("start ScanServer，" + this);
                        while (flag) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                log.error(e.getMessage(), e);
                            }
                            if (dealQueue.size() == 0)
                                try {
                                    for (FileInfo fileInfo : iScan.listFile(path)) {
                                        fileQueue.put(fileInfo);
                                        dealQueue.put(fileInfo);
                                        log.info("fileQueue.put：{}", fileInfo);
                                    }
                                } catch (Exception e) {
                                    log.error(e.getMessage(), e);
                                }
                        }
                    }
                }).start();
            } else {
                log.info("ScanServer status is not ok!");
            }
        }

        public void close() {
            this.flag = false;
        }
    }

    class DealServerDeamon {

        private List<DealServer> dealServerList = new ArrayList<>();

        public void startServer(int serverCnt, String bakPath) {
            if (serverCnt > 0 && bakPath != null && bakPath.length() > 0) {
                for (int i = 0; i < serverCnt; i++) {
                    DealServer dealServer = new DealServer(bakPath);
                    dealServerList.add(dealServer);
                    dealServer.start();
                }
            }
        }

        public void closeServer() {
            for (DealServer dealServer : dealServerList) {
                dealServer.close();
            }
        }
    }

    class DealServer {
        private String bakPath;
        private boolean flag = true;

        public DealServer(String bakPath) {
            this.bakPath = bakPath;
        }

        private boolean checkStatus() {
            return this.bakPath != null;
        }

        public void start() {
            if (checkStatus()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        log.info("start DealServer，" + this);
                        while (flag) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                log.error(e.getMessage(), e);
                            }
                            FileInfo fileInfo;
                            while ((fileInfo = fileQueue.poll()) != null) {
                                BufferedReader br = null;
                                try {
                                    String bakFullFileName = appendFilePath(bakPath) + fileInfo.getFilename();
                                    PipeCopy pipeCopy = new PipeCopy();
                                    br = pipeCopy.copy(fileInfo.getInputStream(), bakFullFileName);
                                    String tmp;
                                    while ((tmp = br.readLine()) != null) {
                                        log.debug(tmp);
                                    }
                                } catch (IOException e) {
                                    log.error(e.getMessage(), e);
                                } finally {
                                    if (br != null) {
                                        try {
                                            br.close();
                                            //操作完成，删除源文件
                                            File sourceFile = new File(fileInfo.getFullfilename());
                                            if (sourceFile.exists() && sourceFile.isFile()) {
                                                boolean delresutl = sourceFile.delete();
                                                log.info("delete source file：{}，result：{}", sourceFile, delresutl);
                                                if (delresutl)
                                                    dealQueue.poll();
                                            }
                                        } catch (IOException e) {
                                            log.error(e.getMessage(), e);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }).start();
            } else {
                log.info("DealServer status is not ok!");
            }
        }

        public void close() {
            this.flag = false;
        }
    }

    /**
     * 管道复制处理类
     */
    class PipeCopy {

        public BufferedReader copy(InputStream bakIn, final String bakFullFileName) throws IOException {
            // 使用管道把输出流out2转为输入流pipeIn
            final PipedInputStream pipeIn = new PipedInputStream();
            PipedOutputStream out2 = new PipedOutputStream(pipeIn);
            // 将数据从输入流bakIn快速传到输出流out2中
            InputStream in = new BufferedInputStream(new TeeInputStream(bakIn, out2, true));
            // 管道实现输出流转输入流，线程等待输入流pipeIn，并实现本地备份
            new Thread(new Runnable() {
                public void run() {
                    try {
                        log.info("######开始准备本地备份：{}.", bakFullFileName);
                        File backFile = new File(bakFullFileName);
                        // 等待输入流读取数据
                        FileUtils.copyInputStreamToFile(pipeIn, backFile);
                        log.info("######本地备份完成：{}.", bakFullFileName);
                    } catch (IOException e) {
                        log.error("本地备份复制文件io异常!", e);
                    } finally {
                        try {
                            pipeIn.close();
                        } catch (IOException e) {
                            log.error("本地备份关闭管道io异常!", e);
                        }
                    }
                }
            }).start();
            return new BufferedReader(new InputStreamReader(in, CHARSET), DEFAULT_BUFF_SIZE);
        }
    }
}
