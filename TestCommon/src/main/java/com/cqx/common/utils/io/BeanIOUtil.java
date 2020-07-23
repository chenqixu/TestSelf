package com.cqx.common.utils.io;

import com.alibaba.fastjson.JSON;
import com.cqx.common.utils.file.FileResult;
import com.cqx.common.utils.file.FileUtil;

import java.io.IOException;
import java.util.List;

/**
 * BeanIOUtil<br>
 * <pre>
 *     <I>simple code：</I>
 *     <b>write：</b>
 *         BeanIOUtil<Bean> beanIOUtil = new BeanIOUtil<>(file_name "UTF-8", Bean.class);
 *         beanIOUtil.start_save();
 *         beanIOUtil.saveBeanToFile(new Bean(xxx));
 *         beanIOUtil.stop_save();
 *     <b>read：</b>
 *         beanIOUtil.start_read();
 *         Bean bean = beanIOUtil.readFileToBean();
 *         beanIOUtil.stop_read();
 * </pre>
 *
 * @author chenqixu
 */
public class BeanIOUtil<T> {
    private Class<T> cls;
    private String file_name;
    private String file_code;
    private FileUtil fileUtil;

    public BeanIOUtil(Class<T> cls) {
        this(null, null, cls);
    }

    public BeanIOUtil(String file_name, String file_code, Class<T> cls) {
        this.file_name = file_name;
        this.file_code = file_code;
        this.fileUtil = new FileUtil();
        this.cls = cls;
    }

    public void start_save() throws IOException {
        fileUtil.createFile(file_name, file_code);
    }

    public void start_save(String file_name, String file_code) throws IOException {
        fileUtil.createFile(file_name, file_code);
    }

    public void stop_save() {
        fileUtil.closeWrite();
    }

    public void start_read() throws IOException {
        fileUtil.setReader(file_name, file_code);
    }

    public void start_read(String file_name, String file_code) throws IOException {
        fileUtil.setReader(file_name, file_code);
    }

    public void stop_read() {
        fileUtil.closeRead();
    }

    public void saveBeanToFile(T t) {
        String jsonString = JSON.toJSONString(t);
        fileUtil.write(jsonString);
        fileUtil.newline();
    }

    public void saveListBeanToFile(List<T> tList) {
        for (T t : tList) {
            saveBeanToFile(t);
        }
    }

    public T readFileToBean() throws IOException {
        List<T> tList = readFileToListBean();
        if (tList != null && tList.size() > 0) {
            return tList.get(0);
        }
        return null;
    }

    public List<T> readFileToListBean() throws IOException {
        FileResult<T> fileResult = new FileResult<T>() {
            @Override
            public void run(String content) {
                addFileresult(JSON.parseObject(content, cls));
            }
        };
        fileUtil.read(fileResult);
        return fileResult.getFileresult();
    }
}
