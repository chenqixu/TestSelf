package com.cqx.common.utils.system;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类工具
 *
 * @author chenqixu
 */
public class ClassUtil {

    private static final Logger logger = LoggerFactory.getLogger(ClassUtil.class);

    /**
     * 获取类加载器
     *
     * @return
     */
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 获取当前加载类下的配置文件路径
     *
     * @param resourceName
     * @return
     */
    public URL getResource(String resourceName) {
        return getClassLoader().getResource(resourceName);
    }

    /**
     * 加载类
     * 需要提供类名与是否初始化的标志，
     * 初始化是指是否执行静态代码块
     *
     * @param className
     * @param isInitialized 为提高性能设置为false
     * @return
     */
    public Class<?> loadClass(String className, boolean isInitialized) {
        Class<?> cls;
        try {
            cls = Class.forName(className, isInitialized, getClassLoader());
            //Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.error("加载类失败 loadClass->{}", e);
            throw new RuntimeException(e);
        }
        return cls;
    }

    /**
     * 加载指定包下的所有类
     *
     * @param packageName
     * @return
     */
    public <A extends Annotation> Set<Class<?>> getClassSet(String packageName, Class<A> annotationClass) {
        Set<Class<?>> classSet = new HashSet<>();
        try {
            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    if (protocol.equals("file")) {
                        // 转码
                        String packagePath = URLDecoder.decode(url.getFile(), "UTF-8");
                        // String packagePath =url.getPath().replaceAll("%20", "");
                        // 添加
                        addClass(classSet, packagePath, packageName, annotationClass);
                    } else if (protocol.equals("jar")) {
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        if (jarURLConnection != null) {
                            JarFile jarFile = jarURLConnection.getJarFile();
                            if (jarFile != null) {
                                Enumeration<JarEntry> jarEntries = jarFile.entries();
                                while (jarEntries.hasMoreElements()) {
                                    JarEntry jarEntry = jarEntries.nextElement();
                                    String jarEntryName = jarEntry.getName();
                                    if (jarEntryName.endsWith(".class")) {
                                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf("."))
                                                .replaceAll("/", ".");
                                        doAddClass(classSet, className, annotationClass);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("查找包下的类失败{}", e);
        }
        return classSet;
    }

    /**
     * 添加文件到SET集合
     *
     * @param classSet
     * @param packagePath
     * @param packageName
     */
    private <A extends Annotation> void addClass(Set<Class<?>> classSet, String packagePath, String packageName, Class<A> annotationClass) {
        File[] files = new File(packagePath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class") || file.isDirectory());
            }
        });
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) {
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                if (StringUtils.isNotEmpty(packageName)) {
                    className = packageName + "." + className;
                    logger.info("className: {}", className);
                }
                // 添加
                doAddClass(classSet, className, annotationClass);
            } else {
                // 子目录
                String subPackagePath = fileName;
                if (StringUtils.isNotEmpty(packagePath)) {
                    subPackagePath = packagePath + "/" + subPackagePath;
                }
                String subPackageName = fileName;
                if (StringUtils.isNotEmpty(packageName)) {
                    subPackageName = packageName + "." + subPackageName;
                }
                addClass(classSet, subPackagePath, subPackageName, annotationClass);
            }
        }
    }

    private <A extends Annotation> void doAddClass(Set<Class<?>> classSet, String className, Class<A> annotationClass) {
        Class<?> cls = loadClass(className, false);
        A body = cls.getAnnotation(annotationClass);
        if (body != null) classSet.add(cls);
    }
}
