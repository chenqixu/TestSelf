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
public class ClassUtil<T extends Annotation, K> {
    private static final Logger logger = LoggerFactory.getLogger(ClassUtil.class);

    /**
     * 获取类加载器
     *
     * @return
     */
    public static ClassLoader getClassLoader() {
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
    private static Class<?> loadClass(String className, boolean isInitialized) {
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
     * 构建对象
     *
     * @param cls
     * @return
     */
    public K generate(Class cls) {
        K obj = null;
        try {
            obj = (K) cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }
        return obj;
    }

    /**
     * 加载指定包下的所有类
     *
     * @param packageName
     * @return
     */
    public Set<Class<?>> getClassSet(String packageName, Class<T> annotationClazz) {
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
                        addClass(classSet, packagePath, packageName, annotationClazz);
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
                                        doAddClass(classSet, className, annotationClazz);
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
     * 扫描package路径，添加符合条件的Class到SET集合
     *
     * @param classSet
     * @param packagePath
     * @param packageName
     */
    private void addClass(Set<Class<?>> classSet, String packagePath, String packageName, Class<T> annotationClazz) {
        File[] files = new File(packagePath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class") || file.isDirectory());
            }
        });
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                if (file.isFile()) {
                    String className = fileName.substring(0, fileName.lastIndexOf("."));
                    if (StringUtils.isNotEmpty(packageName)) {
                        className = packageName + "." + className;
                        logger.debug("扫描到 className: {}", className);
                    }
                    // 添加
                    doAddClass(classSet, className, annotationClazz);
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
                    addClass(classSet, subPackagePath, subPackageName, annotationClazz);
                }
            }
        }
    }

    /**
     * 增加类到集合
     *
     * @param classSet
     * @param className
     */
    private void doAddClass(Set<Class<?>> classSet, String className, Class<T> annotationClazz) {
        //类加载
        Class<?> cls = loadClass(className, false);
        //尝试通过注解获取对象
        T body = cls.getAnnotation(annotationClazz);
        //对象不为空即属于这个注解
        if (body != null) {
            classSet.add(cls);
            logger.debug("增加 {} 类到集合", className);
        }
    }
}
