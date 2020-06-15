package com.cqx.common.utils.system;

import com.cqx.common.utils.file.FileCount;
import com.cqx.common.utils.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * JarUtil
 *
 * @author chenqixu
 */
public class JarUtil {
    private static final Logger logger = LoggerFactory.getLogger(JarUtil.class);
    private static Map<String, Class<?>> jarClasses = new HashMap<String, Class<?>>();

    /**
     * 加载.jar文件对象实体
     *
     * @param file 传入一个.jar文件,获取.jar文件对象实体
     * @return
     */
    public static void loadJar(File file) throws IOException, ClassNotFoundException {
        JarFile jarFile = null;
        try {
            if (file == null)
                return;
            URLClassLoader loader = new URLClassLoader(new URL[]{new URL(
                    "file:" + file.getAbsolutePath())}, Thread.currentThread().getContextClassLoader());
            jarFile = new JarFile(file);
            Enumeration<?> enum1 = jarFile.entries();
            while (enum1.hasMoreElements()) {
                JarEntry entry = (JarEntry) enum1.nextElement();
                if (entry.isDirectory())
                    continue;
                String jar = entry.getName();
                if (jar.endsWith(".class")) {
                    String clzname = jar.substring(0, jar.lastIndexOf(".class")).replace('/', '.');
                    Class<?> clz = loader.loadClass(clzname);
                    jarClasses.put(clzname, clz);
                } else if (jar.endsWith(".java")) {
                    InputStream is = loader.getResourceAsStream(jar);
                    logger.info("{}", is);
                    FileUtil fileUtil = new FileUtil();
                    fileUtil.setReader(is);
                    fileUtil.read(new FileCount() {
                        @Override
                        public void run(String content) {
                            logger.info("{}", content);
                        }
                    });
                    fileUtil.closeRead();
                    break;
                }
            }
        } finally {
            if (jarFile != null) jarFile.close();
        }
    }

    /**
     * @param file 传入一个.jar文件,获取.jar文件对象实体
     * @return
     */
    public static List<Class<?>> getClazzInstances(File file) {
        try {
            if (file == null)
                return null;
            URLClassLoader loader = new URLClassLoader(new URL[]{new URL(
                    "file:" + file.getAbsolutePath())}, Thread.currentThread()
                    .getContextClassLoader());
            List<Class<?>> classinstances = new ArrayList<Class<?>>();
            JarFile jarFile = new JarFile(file);
            Enumeration<?> enum1 = jarFile.entries();
            while (enum1.hasMoreElements()) {
                JarEntry entry = (JarEntry) enum1.nextElement();
                if (entry.isDirectory())
                    continue;
                String jar = entry.getName();
                if (jar.endsWith(".class")) {
                    String clzname = jar.substring(0, jar.lastIndexOf(".class")).replace('/', '.');
                    Class<?> clz = loader.loadClass(clzname);
                    classinstances.add(clz);
                    jarClasses.put(clzname, clz);
                }
            }
            if (jarFile != null) jarFile.close();
            return classinstances;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取属性文件实例
     *
     * @param suffix TODO
     */
    public static InputStream getInputStream(File file, String suffix) {
        InputStream is = null;
        try {
            if (file == null)
                return null;
            JarFile jarFile = new JarFile(file);
            Enumeration<?> enum1 = jarFile.entries();
            while (enum1.hasMoreElements()) {
                JarEntry entry = (JarEntry) enum1.nextElement();
                if (entry.isDirectory())
                    continue;
                String jar = entry.getName();
                if (jar.endsWith(suffix)) {
                    is = jarFile.getInputStream(entry);
                }
            }
            if (jarFile != null) jarFile.close();
            return is;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return is;
    }


    /**
     * 判断目录中是否已存在相同名字的文件
     */
    public static boolean existsSameJar(String path, String filename) {
        File file = new File(path);
        String jarPath = file.getPath() + "/" + filename;
        File jar = new File(jarPath);
        return jar.exists();
    }

    /**
     * 在传入路径中创建文件
     *
     * @param data
     * @param filename
     * @return
     */
    public static File createTempFile(byte[] data, String filename, String path) {
        FileOutputStream fos = null;
        path = path + "/temp";
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        try {
            fos = new FileOutputStream(f.getPath() + '/' + filename);
            fos.flush();
            fos.write(data);
            return new File(path + '/' + filename);
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
