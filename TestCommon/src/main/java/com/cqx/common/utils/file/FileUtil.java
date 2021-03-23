package com.cqx.common.utils.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 文件工具
 *
 * @author chenqixu
 */
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
    private static final String valueSplit = "\\|";
    private static final int BUFF_SIZE = 4096;
    private static final String fileSparator = File.separator;
    private BufferedWriter writer;
    private BufferedReader reader;
    private OutputStream outputStream;

    public static File[] listFiles(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isDirectory()) {
            return file.listFiles();
        }
        return null;
    }

    public static String[] listFile(String path) {
        return listFile(path, null);
    }

    public static String[] listFile(String path, final String keyword) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            if (keyword != null && keyword.length() > 0) {
                logger.info("listFile use keyword：{}.", keyword);
                return file.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.contains(keyword);
                    }
                });
            } else {
                logger.info("listFile not use keyword.");
                return file.list();
            }
        } else {
            logger.warn("path：{}，file not exists：{} or file is not Directory：{}", path, file.exists(), file.isDirectory());
        }
        return new String[0];
    }

    public static String[] listFileEndWith(String path, final String endWith) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            return file.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(endWith);
                }
            });
        } else {
            logger.warn("path：{}，file not exists：{} or file is not Directory：{}", path, file.exists(), file.isDirectory());
        }
        return new String[0];
    }

    /**
     * 通过关键字和文件后缀来过滤文件
     *
     * @param path
     * @param keyword
     * @param endWith
     * @return
     */
    public static String[] listFile(String path, final String keyword, final String endWith) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            if (keyword != null && keyword.length() > 0) {
                logger.info("listFile use keyword：{}，endWith：{}.", keyword, endWith);
                return file.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.contains(keyword) && name.endsWith(endWith);
                    }
                });
            } else {
                logger.info("listFile not use keyword.");
                return file.list();
            }
        } else {
            logger.warn("path：{}，file not exists：{} or file is not Directory：{}", path, file.exists(), file.isDirectory());
        }
        return new String[0];
    }

    /**
     * 文件合并
     *
     * @param srcList
     * @param dst
     * @throws IOException
     */
    public static void mergeFile(List<String> srcList, String dst) throws IOException {
        OutputStream out = new FileOutputStream(dst);
        for (String src : srcList) {
            File file = new File(src);
            if (file.exists() && file.isFile()) {
                InputStream in = new FileInputStream(file);
                copyBytes(in, out);
            }
        }
        closeStream(out);
    }

    public static void copyBytes(InputStream in, OutputStream out) throws IOException {
        copyBytes(in, out, BUFF_SIZE, true);
    }

    public static void copyBytes(InputStream in, OutputStream out, int buffSize, boolean close) throws IOException {
        try {
            copyBytes(in, out, buffSize);
            if (close) {
                closeStream(in);
            }
        } catch (IOException e) {
            closeStream(in);
            closeStream(out);
            throw e;
        } finally {
            if (close) {
                closeStream(in);
            }
        }
    }

    public static void copyBytes(InputStream in, OutputStream out, int buffSize) throws IOException {
        byte[] buf = new byte[buffSize];

        for (int bytesRead = in.read(buf); bytesRead >= 0; bytesRead = in.read(buf)) {
            out.write(buf, 0, bytesRead);
        }
    }

    /**
     * 文件拷贝
     *
     * @param inputFile
     * @param outputFile
     * @throws IOException
     */
    public static void copyFile(String inputFile, String outputFile) throws IOException {
        if (isExists(inputFile)) {
            List<String> fileList = new ArrayList<>();
            fileList.add(inputFile);
            mergeFile(fileList, outputFile);
        } else {
            logger.warn("File {} is not found , please check !", inputFile);
        }
    }

    public static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
                stream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件
     *
     * @param src
     * @return
     */
    public static boolean del(String src) {
        if (isExists(src)) return new File(src).delete();
        else return false;
    }

    /**
     * 创建软链接
     *
     * @param sourceFilePath
     * @param linkPath
     * @throws IOException
     */
    public static void createSymbolicLink(String sourceFilePath, String linkPath) throws IOException {
        Path link = FileSystems.getDefault().getPath(linkPath);
        Path target = FileSystems.getDefault().getPath(sourceFilePath);

        // // 创建软链接时设置软链接的属性
//		PosixFileAttributes attrs = Files.readAttributes(target, PosixFileAttributes.class);
//		FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(attrs.permissions());
        Files.createSymbolicLink(link, target); // , attr
    }

    /**
     * 如果文件夹不以\结尾则加上
     *
     * @param path
     * @return
     */
    public static String endWith(String path) {
        if (path.endsWith(fileSparator)) return path;
        else return path + fileSparator;
    }

    /**
     * 创建路径，可以递归
     *
     * @param path
     */
    public static void CreateDir(String path) {
        if (path != null && path.length() > 0) {
            File file = new File(path);
            if (!(file.exists() && file.isDirectory())) {
                file.mkdirs();
            }
        }
    }

    /**
     * 获取类的资源文件路径
     *
     * @return
     */
    public static String getClassResourcePath() {
        return getClassResourcePath(null);
    }

    /**
     * 获取类的资源文件路径
     *
     * @param cs
     * @return
     */
    public static String getClassResourcePath(Class<?> cs) {
        String path = "";
        if (cs == null) {
            Object obj = new Object();
            URL classResource = obj.getClass().getResource("/");
            if (classResource != null) {
                path = classResource.getPath();
            }
        } else {
            URL classResource = cs.getResource("/");
            if (classResource != null) {
                path = classResource.getPath();
            }
        }
        // 如果不是test，而是java下的，配置文件会在classes的同级而不在里面
        String sourceTargetClass = "target/classes/";
        String sourceClass = "classes/";
        if (path.endsWith(sourceTargetClass)) path = path.substring(0, path.length() - sourceClass.length());
        return path;
    }

    /**
     * 文件是否存在
     *
     * @param fileName
     * @return
     */
    public static boolean isExists(String fileName) {
        return new File(fileName).exists();
    }

    /**
     * 重命名文件
     *
     * @param source
     * @param dist
     * @return
     */
    public static boolean rename(String source, String dist) {
        File sourcefile = new File(source);
        File distfile = new File(dist);
        if (sourcefile.exists() && sourcefile.isFile() && !distfile.exists()) {
            return sourcefile.renameTo(distfile);
        } else {
            return false;
        }
    }

    /**
     * 读取配置文件内容
     *
     * @param path
     * @return
     */
    public static String readConfFile(String path) {
        File readFile = new File(path);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(readFile), StandardCharsets.UTF_8))) {
            String _tmp;
            while ((_tmp = reader.readLine()) != null) {
                sb.append(_tmp);
            }
        } catch (IOException e) {
            logger.error("Read " + path + " IOException. Message:" + e.getMessage());
        }
        if (sb.length() > 0) {
            return sb.toString();
        } else {
            return null;
        }
    }

    public void getFile(String filename, String read_code)
            throws FileNotFoundException, UnsupportedEncodingException {
        File readFile = new File(filename);
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(readFile), read_code));
    }

    /**
     * 逐行处理
     *
     * @param iFileRead
     * @throws IOException
     */
    public void read(IFileRead iFileRead) throws IOException {
        String _tmp;
        while ((_tmp = reader.readLine()) != null) {
            iFileRead.run(_tmp);
        }
        //结束
        iFileRead.tearDown();
    }

    public void read(IFileRead iFileRead, int threadNum) throws IOException {
        BlockingQueue<String> contentQueue = new LinkedBlockingQueue<>();
        List<DealThread> threads = new ArrayList<>();
        //生产者线程
        ReaderThread readerThread = new ReaderThread(contentQueue);
        //启动生产者线程
        readerThread.start();
        //消费者线程
        for (int i = 0; i < threadNum; i++) {
            threads.add(new DealThread(readerThread, iFileRead, contentQueue));
        }
        //启动消费者线程
        for (Thread thread : threads) {
            thread.start();
        }
        //监控打印

        //等待处理
        try {
            readerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> read(String sql) {
        List<String> resultlist = new ArrayList<>();
        try {
            String _tmp;
            while ((_tmp = reader.readLine()) != null) {
                String[] values = _tmp.split(valueSplit);
                StringBuffer sb = new StringBuffer();
                sb.append(sql).append(" values(");
                for (int i = 0; i < values.length; i++) {
                    sb.append("'").append(values[i]).append("'");
                    if (i < values.length - 1) sb.append(",");
                }
                sb.append(")");
                resultlist.add(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultlist;
    }

    /**
     * 通过路径读取文件内容到List
     *
     * @param path
     * @param read_code
     * @return
     */
    public List<String> read(String path, String read_code) {
        File file = null;
        BufferedReader reader = null;
        List<String> sublist = new Vector<String>();
        try {
            file = new File(path);
            if (file.isFile()) {
                reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(file), read_code));
                String _tmp = null;
                while ((_tmp = reader.readLine()) != null) {
                    sublist.add(_tmp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sublist;
    }

    public void createFile(String filename, String write_code, boolean append)
            throws FileNotFoundException, UnsupportedEncodingException {
        File writeFile = new File(filename);
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writeFile, append), write_code));
    }

    public void createFile(String filename, String write_code)
            throws FileNotFoundException, UnsupportedEncodingException {
        createFile(filename, write_code, false);
    }

    public void createOutputStreamFile(String filename, boolean append) throws FileNotFoundException {
        File writeFile = new File(filename);
        outputStream = new FileOutputStream(writeFile, append);
    }

    public void createOutputStreamFile(String filename) throws FileNotFoundException {
        createOutputStreamFile(filename, false);
    }

    public void os_write(byte[] bytes) {
        if (outputStream != null) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void os_write(byte[] bytes, int off, int len) {
        if (outputStream != null) {
            try {
                outputStream.write(bytes, off, len);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void os_newline() {
        if (isWindow()) os_write("\r\n".getBytes());
        else os_write("\n".getBytes());
    }

    public void newline() {
        if (isWindow()) write("\r\n");
        else write("\n");
    }

    public void write(String str) {
        try {
            if (writer != null) {
                writer.write(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(String s, int off, int len) {
        try {
            if (writer != null) {
                writer.write(s, off, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeRead() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeWrite() {
        try {
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void closeOutputStream() {
        try {
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public void setReader(InputStream is, String read_code) throws UnsupportedEncodingException {
        setReader(new BufferedReader(new InputStreamReader(is, read_code)));
    }

    public void setReader(InputStream is) throws UnsupportedEncodingException {
        setReader(is, "UTF-8");
    }

    public void setReader(String fileName, String read_code) throws FileNotFoundException, UnsupportedEncodingException {
        setReader(new FileInputStream(new File(fileName)), read_code);
    }

    public void setReader(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
        setReader(fileName, "UTF-8");
    }

    public boolean isWindow() {
        String systemType = System.getProperty("os.name");
        if (systemType.toUpperCase().startsWith("WINDOWS")) {
            return true;
        } else {
            return false;
        }
    }

    class ReaderThread extends Monitor {
        private BlockingQueue<String> contentQueue;
        private boolean flag = false;

        public ReaderThread(BlockingQueue<String> contentQueue) {
            this.contentQueue = contentQueue;
        }

        public void run() {
            String _tmp;
            try {
                while ((_tmp = reader.readLine()) != null) {
                    contentQueue.put(_tmp);
                    count();
                }
                flag = true;
            } catch (Exception e) {
                //异常也视为完成，否则消费者线程不会停止
                flag = true;
                e.printStackTrace();
            }
        }

        public boolean isComplete() {
            return flag;
        }
    }

    class DealThread extends Monitor {
        private IFileRead iFileRead;
        private BlockingQueue<String> contentQueue;
        private ReaderThread readerThread;

        public DealThread(ReaderThread readerThread, IFileRead iFileRead, BlockingQueue<String> contentQueue) {
            this.readerThread = readerThread;
            this.iFileRead = iFileRead;
            this.contentQueue = contentQueue;
        }

        public void run() {
            String str;
            while (!readerThread.isComplete()) {
                while ((str = contentQueue.poll()) != null) {
                    try {
                        iFileRead.run(str);
                        count();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    abstract class Monitor extends Thread {
        private AtomicLong cnt = new AtomicLong(0L);

        public abstract void run();

        protected void count() {
            cnt.incrementAndGet();
        }

        public long getCnt() {
            return cnt.get();
        }
    }

    class MonitorThread extends Thread {
        private List<Monitor> monitors;

        public MonitorThread(List<Monitor> monitors) {
            this.monitors = monitors;
        }

        public void run() {

        }
    }
}
