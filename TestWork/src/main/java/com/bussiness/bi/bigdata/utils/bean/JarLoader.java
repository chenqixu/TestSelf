package com.bussiness.bi.bigdata.utils.bean;

import java.io.*;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * <p>Title: LoonFramework</p>
 * <p>Description:JarLoader，用于jar包的外部操作</p>
 *
 * @author chenqixu
 */
public class JarLoader extends ClassLoader {
    //资源缓存
    public static Hashtable resources = new Hashtable();
    public static JarLoader loader = new JarLoader();

    public static Class load(byte[] resource) throws Exception {
        // 主函数所在类全称
        String mainClassName = "";
        //class资源及实体缓存
        ArrayList<String> classNames = new ArrayList<>();
        ArrayList<String> allClassNames = new ArrayList<>();
        ArrayList<byte[]> classBuffers = new ArrayList<>();
        // 存储依赖类
        HashMap depends = new HashMap();
        // 将byte[]转为JarInputStream
        JarInputStream jar = new JarInputStream(new ByteArrayInputStream(
                resource));
        Manifest manifest = jar.getManifest();
        // 当Main-Class被声明时,获得主函数所在类全称
        if (manifest != null) {
            mainClassName = manifest.getMainAttributes().getValue("Main-Class");
        }
        // 依次获得对应JAR文件中封装的各个被压缩文件的JarEntry
        JarEntry entry;
        while ((entry = jar.getNextJarEntry()) != null) {
            // 当找到的entry为class时
            if (entry.getName().toLowerCase().endsWith(".class")) {
                // 将类路径转变为类全称
                String name = entry.getName().substring(0,
                        entry.getName().length() - ".class".length()).replace(
                        '/', '.');
                // 加载该类
                byte[] data = getResourceData(jar);
                // 缓存类名及数据
                classNames.add(name);
                allClassNames.add(name);
                classBuffers.add(data);
            } else {
                // 非class结尾但开头字符为'/'时
                if (entry.getName().charAt(0) == '/') {
                    resources.put(entry.getName(), getResourceData(jar));
                    // 否则追加'/'后缓存
                } else {
                    resources.put("/" + entry.getName(), getResourceData(jar));
                }
            }
        }
        //当获得的main-class名不为空时
        while (classNames.size() > 0) {
            //获得类路径全长
            int n = classNames.size();
            for (int i = classNames.size() - 1; i >= 0; i--) {
                try {
                    System.out.println("classNames : " + classNames.get(i) +
                            "，classBuffers.length : " + classBuffers.get(i).length);
                    //查询指定类
                    loader.defineClass(classNames.get(i),
                            classBuffers.get(i), 0,
                            classBuffers.get(i).length);
                    //获得类名
                    String pkName = classNames.get(i);
                    if (pkName.lastIndexOf('.') >= 0) {
                        pkName = pkName
                                .substring(0, pkName.lastIndexOf('.'));
                        if (loader.getPackage(pkName) == null) {
                            loader.definePackage(pkName, null, null, null,
                                    null, null, null, null);
                        }
                    }
                    //查询后删除缓冲
                    classNames.remove(i);
                    classBuffers.remove(i);
                } catch (NoClassDefFoundError e) {
                    depends.put(classNames.get(i), e.getMessage()
                            .replaceAll("/", "."));
                    e.printStackTrace();
                } catch (UnsupportedClassVersionError e) {
                    //jre版本错误提示
                    throw new UnsupportedClassVersionError(classNames.get(i)
                            + ", " + System.getProperty("java.vm.name") + " "
                            + System.getProperty("java.vm.version") + ")");
                }
            }
            if (n == classNames.size()) {
                for (int i = 0; i < classNames.size(); i++) {
                    System.err.println("NoClassDefFoundError:"
                            + classNames.get(i));
                    String className = classNames.get(i);
                    while (depends.containsKey(className)) {
                        className = (String) depends.get(className);
                    }
                }
                break;
            }
        }
        try {
            //加载
            Thread.currentThread().setContextClassLoader(loader);
            // 获得指定类,查找其他类方式相仿
//            return Class.forName(mainClassName, true, loader);
            // 加载所有类
            for (String clsName : allClassNames) {
                if (!clsName.contains("$")) {
                    System.out.println("Class.forName " + clsName);
                    Class.forName(clsName, true, loader);
                }
            }
            return null;
        } catch (ClassNotFoundException e) {
            String className = mainClassName;
            while (depends.containsKey(className)) {
                className = (String) depends.get(className);
            }
            throw new ClassNotFoundException(className);
        }
    }

    /**
     * 获得指定路径文件的byte[]形式
     *
     * @param name
     * @return
     */
    final static public byte[] getDataSource(String name) {
        FileInputStream fileInput;
        try {
            fileInput = new FileInputStream(new File(name));
        } catch (FileNotFoundException e) {
            fileInput = null;
        }
        BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
        return getDataSource(bufferedInput);
    }

    /**
     * 获得指定InputStream的byte[]形式
     *
     * @param name
     * @return
     */
    final static public byte[] getDataSource(InputStream is) {
        if (is == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] arrayByte = null;
        try {
            byte[] bytes = new byte[8192];
            bytes = new byte[is.available()];
            int read;
            while ((read = is.read(bytes)) >= 0) {
                byteArrayOutputStream.write(bytes, 0, read);
            }
            arrayByte = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                    byteArrayOutputStream = null;
                }
                if (is != null) {
                    is.close();
                    is = null;
                }

            } catch (IOException e) {
            }
        }
        return arrayByte;
    }

    /**
     * 获得指定JarInputStream的byte[]形式
     *
     * @param jar
     * @return
     * @throws IOException
     */
    final static private byte[] getResourceData(JarInputStream jar)
            throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int size;
        while (jar.available() > 0) {
            size = jar.read(buffer);
            if (size > 0) {
                data.write(buffer, 0, size);
            }
        }
        return data.toByteArray();
    }

    /**
     * 执行指定class类
     *
     * @param clz
     * @param methodName
     * @param args
     */
    public static void callVoidMethod(Class clz, String methodName,
                                      String[] args) {
        Class[] arg = new Class[1];
        arg[0] = args.getClass();
        try {
            Method method = clz.getMethod(methodName, arg);
            Object[] inArg = new Object[1];
            inArg[0] = args;
            method.invoke(clz, inArg);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * 重载的getResource,检查是否重复包含
     */
    public URL getResource(String name) {
        if (resources.containsKey("/" + name)) {
            try {
                return new URL("file:///" + name);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return super.getResource(name);
    }

    /**
     * 重载的getResourceAsStream,检查是否重复包含
     */
    public InputStream getResourceAsStream(String name) {
        if (name.charAt(0) == '/') {
            name = name.substring(1);
        }
        if (resources.containsKey("/" + name)) {
            return new ByteArrayInputStream((byte[]) resources.get("/" + name));
        }
        return super.getResourceAsStream(name);
    }

}
