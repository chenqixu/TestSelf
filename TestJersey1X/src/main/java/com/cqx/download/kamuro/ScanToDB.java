package com.cqx.download.kamuro;

import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.jdbc.DBBean;
import com.cqx.common.utils.jdbc.DBType;
import com.cqx.common.utils.jdbc.JDBCUtil;
import com.cqx.download.kamuro.bean.ComicBookBean;
import com.cqx.download.kamuro.bean.ComicBookImgBean;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 扫描并入库
 *
 * @author chenqixu
 */
public class ScanToDB {
    public static final String TYPE = "TYPE";
    public static final String ID = "ID";
    private static final Logger logger = LoggerFactory.getLogger(ScanToDB.class);
    private String basePath;
    private int imgCnt = 0;
    private Map<String, AtomicInteger> atomicImgCnt = new HashMap<>();
    private FileUtil fileUtil = new FileUtil();
    private JDBCUtil jdbcUtil;

    public ScanToDB(String basePath, DBBean dbBean) {
        this.basePath = basePath;
        this.jdbcUtil = new JDBCUtil(dbBean);
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            logger.warn("需要传入参数，使用逗号分隔！如：[201909,201910]");
            System.exit(-1);
        }
        String basePath = "X:\\Reader\\web\\res\\comic\\kamuro";
        DBBean dbBean = new DBBean();
        dbBean.setDbType(DBType.DERBY_NET);
        dbBean.setTns("jdbc:derby://localhost:1527/d:/tmp/data/derby/sample");
        dbBean.setUser_name("admin");
        dbBean.setPass_word("admin");
        ScanToDB scanToDB = new ScanToDB(basePath, dbBean);
        String[] months;
        months = args[0].split(",", -1);
        for (String tmp : months) {
            scanToDB.scanBook(tmp);
        }
        scanToDB.close();
    }

    public void close() {
        if (jdbcUtil != null) {
            jdbcUtil.close();
        }
    }

    /**
     * 按月扫描并入库
     *
     * @param monthPath
     * @throws IOException
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws IntrospectionException
     * @throws InvocationTargetException
     */
    public void scanBook(String monthPath) throws IOException, SQLException
            , IllegalAccessException, IntrospectionException, InvocationTargetException {
        List<ComicBookBean> comicBookBeans = new ArrayList<>();
        // 目录结构：月份/书/图
        // 先扫描出书，再扫描出图即可
        String realMonthPath = basePath + File.separator + monthPath;
        File file = new File(realMonthPath);
        String[] bookPaths = file.list();
        if (bookPaths != null) {
            for (String bookPath : bookPaths) {
                // 从末尾往前倒着找，找到非数字为止
                Map<String, String> typeID = getTypeAndID(bookPath);
                ComicBookBean comicBookBean = scanImg(monthPath, bookPath, typeID.get(TYPE), typeID.get(ID));
                if (comicBookBean != null) {
                    comicBookBeans.add(comicBookBean);
                }
            }
            logger.info("cnt：{}，atomicImgCnt：{}", imgCnt, atomicImgCnt.size());
            String fields = "month_name,book_name,book_desc,title_img_name";
            // 清理
            jdbcUtil.executeUpdate("delete from comic_book where month_name='" + monthPath + "'");
            // 插入
            jdbcUtil.executeBatch(
                    String.format("insert into comic_book(%s) values(?,?,?,?)", fields)
                    , comicBookBeans
                    , ComicBookBean.class
                    , fields
            );
        } else {
            logger.warn("{}没有扫描到文件！", realMonthPath);
        }
    }

    /**
     * 按书本扫描并入库
     *
     * @param monthPath
     * @param bookPath
     * @param type
     * @param id
     * @return
     * @throws IOException
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws IntrospectionException
     * @throws InvocationTargetException
     */
    public ComicBookBean scanImg(String monthPath, String bookPath, String type, String id)
            throws IOException, SQLException, IllegalAccessException
            , IntrospectionException, InvocationTargetException {
        ComicBookBean comicBookBean = null;
        String realBookPath = basePath + File.separator + monthPath + File.separator + bookPath;
        File file = new File(realBookPath);
        logger.info("bookPath：{}", realBookPath);
        if (file.isDirectory()) {
            List<ComicBookImgBean> comicBookImgBeans = new ArrayList<>();
            String first_img_name = null;
            for (String imgPath : Objects.requireNonNull(file.list())) {
                String realImgPath = realBookPath + File.separator + imgPath;
                if (imgPath.endsWith("jpg")) {
                    if (imgPath.startsWith("1.")) {
                        first_img_name = imgPath;
                        logger.info("first_img_name：{}", first_img_name);
                    }
                    String imgMD5 = DigestUtils.md5Hex(new FileInputStream(realImgPath));
                    logger.info("monthPath：{}，bookPath：{}，imgPath：{}，imgMD5：{}"
                            , monthPath, bookPath, imgPath, imgMD5);
                    ComicBookImgBean comicBookImgBean = new ComicBookImgBean(
                            monthPath
                            , bookPath
                            , type
                            , id
                            , imgPath
                            , imgMD5
                    );
                    comicBookImgBeans.add(comicBookImgBean);
                    imgCnt++;
                    AtomicInteger atomicInteger = atomicImgCnt.get(imgMD5);
                    if (atomicInteger == null) {
                        atomicInteger = new AtomicInteger(0);
                        atomicImgCnt.put(imgMD5, atomicInteger);
                    }
                    atomicInteger.incrementAndGet();
                } else if (imgPath.contains("readme.txt")) {
                    List<String> contents = fileUtil.read(realImgPath, "UTF-8");
                    if (contents.size() > 0) {
                        String desc = contents.get(0);
                        logger.info("{}", desc);
                        comicBookBean = new ComicBookBean(
                                monthPath
                                , bookPath
                                , desc
                                , "1.jpg"
                        );
                    }
                }
            }
            String fields = "month_name,book_name,book_type_name,book_id,img_name,img_md5";
            // 清理
            jdbcUtil.executeUpdate("delete from comic_book_img where month_name='" + monthPath + "' and book_name='" + bookPath + "'");
            // 插入
            jdbcUtil.executeBatch(
                    String.format("insert into comic_book_img(%s) values(?,?,?,?,?,?)", fields)
                    , comicBookImgBeans
                    , ComicBookImgBean.class
                    , fields
            );
            if (first_img_name != null && comicBookBean != null) {
                comicBookBean.setTitle_img_name(first_img_name);
            }
        }
        return comicBookBean;
    }

    /**
     * 获取类型和ID，从末尾往前倒着找，找到非数字为止
     *
     * @param bookName
     * @return
     */
    private Map<String, String> getTypeAndID(String bookName) {
        Map<String, String> map = new HashMap<>();
        int index = -1;
        char[] tmps = bookName.toCharArray();
        for (int i = tmps.length - 1; i >= 0; i--) {
            if (!Character.isDigit(tmps[i])) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            map.put(TYPE, bookName.substring(0, index + 1));
            map.put(ID, bookName.substring(index + 1));
            logger.info("bookName：{}，type：{}，id：{}", bookName, map.get(TYPE), map.get(ID));
        }
        return map;
    }
}
