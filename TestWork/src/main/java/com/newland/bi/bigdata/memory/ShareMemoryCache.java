package com.newland.bi.bigdata.memory;

import com.cqx.common.utils.file.MyRandomAccessFile;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import com.newland.bi.bigdata.metric.MetricsUtil;
import com.newland.bi.bigdata.utils.string.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

/**
 * 共享内存缓存
 *
 * @author chenqixu
 */
public class ShareMemoryCache {

    private static final MyLogger logger = MyLoggerFactory.getLogger(ShareMemoryCache.class);
    private RandomAccessFile raFile;
    private FileChannel fc;
    private MappedByteBuffer mapBuf;
    private int mode;
    private FileLock fileLock;

    private String datafilename;
    private String idfilename;

    private RandomAccessFile datafile;
    private RandomAccessFile idfile;

    private RafMap rafMap;
    private MemoryTest memoryTest;
    private MyRandomAccessFile mydatafile;

    private ShareMemoryCache() {
    }

    public static ShareMemoryCache newbuilder() {
        return new ShareMemoryCache();
    }

    public static void main(String[] args) throws Exception {
        MetricsUtil ms = MetricsUtil.builder();
        ms.addTimeTag();
        String datapath = "D:\\Document\\Workspaces\\Git\\TestSelf\\TestWork\\src\\main\\resources\\data\\yyzs";
        String datafilename = "D:\\Document\\Workspaces\\Git\\TestSelf\\TestWork\\src\\main\\resources\\data\\yyzs\\memoryfile.txt";
        List<String> idfiles = MemoryTest.builder().getIdfiles();
//        idfiles.add("123");
//        idfiles.add("456");
        ShareMemoryCache shareMemoryCache = ShareMemoryCache
                .newbuilder()
                .setDatafilename(datafilename);
        shareMemoryCache.init();
//        shareMemoryCache.writeData(idfiles, 3);
//        shareMemoryCache.writeData(datapath, "567", 3, 10);
//        shareMemoryCache.writeData(datapath, "1234", 3, 10);
//        shareMemoryCache.getDataFromFile(datapath, "567", 10);
//        shareMemoryCache.getDataFromFile(datapath, "1234", 10);
        for (String _tmp : idfiles) {
            String result = shareMemoryCache.getDataFromFile(datapath, _tmp, 10);
            logger.info("read：{}", result);
        }
        logger.info("spend：{}", ms.getTimeOut());
        shareMemoryCache.closeAll();
    }

    public void init() throws FileNotFoundException {
        rafMap = new RafMap();
        memoryTest = MemoryTest.builder();
        if (StringUtils.isNotEmpty(datafilename)) {
            datafile = new RandomAccessFile(datafilename, MemoryCacheMode.READ_WRITE.getCode());
            mydatafile = new MyRandomAccessFile(datafilename);
        }
        if (StringUtils.isNotEmpty(idfilename))
            idfile = new RandomAccessFile(idfilename, MemoryCacheMode.READ_WRITE.getCode());
    }

    public String getData() throws IOException {
        int start = idfile.readInt();
        int end = idfile.readInt();
        int length = idfile.readInt();
        byte[] data = new byte[length];
        datafile.read(data, start, end);
        return new String(data);
    }

    public String getDataFromFile(String idfilename) throws IOException {
        String result;
        RandomAccessFile idfile = null;
        try {
            idfile = new RandomAccessFile(idfilename, MemoryCacheMode.READ_WRITE.getCode());
            int start = idfile.readInt();
            int end = idfile.readInt();
            int length = idfile.readInt();
            byte[] data = new byte[length];
            datafile.seek(start);
            datafile.read(data, 0, length);
            result = new String(data);
            logger.info("start：{}，end：{}，length：{}，result：{}", start, end, length, result);
        } finally {
            if (idfile != null)
                idfile.close();
        }
        return result;
    }

    public String getDataFromFile(String path, String derviceID, int dies) throws IOException {
        String result;
        int flag = getHashCodeDies(derviceID, dies);
        String newfilename = StringUtils.getEndsWithPath(path) + flag + ".txt";
        MyRandomAccessFile newraf = rafMap.get(newfilename, true);
        result = newraf.getData();
        int derviceindex = result.indexOf(derviceID);
        int end = result.indexOf(";", derviceindex);
        String find = result.substring(derviceindex + derviceID.length() + 1, end);
        int off = Integer.valueOf(find.substring(0, find.indexOf(",")));
        int len = Integer.valueOf(find.substring(find.indexOf(",") + 1));
        logger.info("result：{}" + result + "，index:" + derviceindex + "，end:" + end + "，find:" + find + "，off:" + off + "，len:" + len);
        result = mydatafile.read(off, len);
//        System.out.println("result:" + result);
        return result;
    }

    public void writeData(String path, String derviceID, int msglen, int dies) throws IOException {
        String msg = memoryTest.random(msglen);
//        String indexMsg = derviceID + "," + msg;
        String indexMsg = derviceID;
        int length = msg.length();
        int flag = getHashCodeDies(derviceID, dies);
        String newfilename = StringUtils.getEndsWithPath(path) + flag + ".txt";
        MyRandomAccessFile newraf = rafMap.get(newfilename);
        indexMsg = indexMsg + "," + mydatafile.getIndex() + "," + length + ";";
        // 写数据文件
        mydatafile.write(msg.getBytes(), length);
        // 写索引文件，格式：id,off,len;，如：567,0,3;
        newraf.write(indexMsg.getBytes(), indexMsg.length());
    }

    public void writeData(List<String> idfilenames, int msglen) throws IOException {
        int start = 0;
        int end = 0;

        for (String idfilename : idfilenames) {
            String msg = memoryTest.random(msglen);
            int length = msg.length();
            end = start + length;
            logger.info("start：{}，end：{}，length：{}，msg：{}", start, end, length, msg);
            RandomAccessFile idfile = null;
            try {
                idfile = new RandomAccessFile(idfilename, MemoryCacheMode.READ_WRITE.getCode());
                idfile.writeInt(start);
                idfile.writeInt(end);
                idfile.writeInt(length);
            } finally {
                if (idfile != null)
                    idfile.close();
            }
            datafile.write(msg.getBytes(), 0, length);
            start = end;
        }
    }

    /**
     * 产生随机文件名
     *
     * @param len
     * @return
     */
    public List<String> createRandomFileName(String path, int len) {
        List<String> files = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            files.add(StringUtils.getEndsWithPath(path) + memoryTest.random(32) + ".txt");
        }
        return files;
    }

    public void cleanfile(String filepath) {
        File file = new File(filepath);
        for (File _file : file.listFiles()) {
            _file.deleteOnExit();
        }
    }

    public void closeAll() {
        if (datafile != null) {
            try {
                datafile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (idfile != null) {
            try {
                idfile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (rafMap != null) {
            try {
                rafMap.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mydatafile != null) {
            try {
                mydatafile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createRandomAccessFile(String filename
            , MemoryCacheMode memoryCacheMode) throws IOException {
        // 获得一个只读的随机存取文件对象
        raFile = new RandomAccessFile(filename, memoryCacheMode.getCode());
        // 获得相应的文件通道
        fc = raFile.getChannel();
        logger.info("fc：{}", fc);
        // 获得文件的独占锁，该方法不产生堵塞，立刻返回
        fileLock = fc.tryLock();
        logger.info("fileLock：{}，sShared：{}，isValid：{}", fileLock, fileLock.isShared(), fileLock.isValid());
        fileLock.release();
//        // 取得文件的实际大小，以便映像到共享内存
//        int size = (int) fc.size();
//        // 获得共享内存缓冲区
//        mapBuf = fc.map(memoryCacheMode.getMapMode(), 0, size);
//        // 获取头部消息：存取权限
//        mode = mapBuf.getInt();
    }

    public void read() {
//        byte[] buff = new byte[1024];
//        int hasRead = 0;
        String text;
        try {
            while ((text = raFile.readLine()) != null) {
                //打印读取的内容,并将字节转为字符串输入
//                System.out.println(new String(buff, 0, hasRead));
                logger.info("{}", new String(text.getBytes("ISO-8859-1"), "UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String msg) {
        try {
            raFile.writeBytes(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (raFile != null) {
            try {
                raFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fc != null) {
            try {
                fc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileLock != null) {
            try {
                if (fileLock.isValid()) {
                    fileLock.close();
                    logger.info("fileLock.close");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean StartWrite() {
        if (mode == 0) { // 标志为0，则表示可写
            mode = 1; // 置标志为1，意味着别的应用不可写该共享内存
            mapBuf.flip();
            mapBuf.putInt(mode); // 写如共享内存的头部信息
            return true;
        } else {
            return false; // 指明已经有应用在写该共享内存，本应用不可写该共享内存
        }
    }

    public boolean StopWrite() {
        mode = 0; // 释放写权限
        mapBuf.flip();
        mapBuf.putInt(mode); // 写入共享内存头部信息
        return true;
    }

    public ShareMemoryCache setDatafilename(String datafilename) {
        this.datafilename = datafilename;
        return this;
    }

    public ShareMemoryCache setIdfilename(String idfilename) {
        this.idfilename = idfilename;
        return this;
    }

    public int getHashCodeDies(String derviceID, int dies) {
        return Math.abs(derviceID.hashCode()) % dies;
    }

}
