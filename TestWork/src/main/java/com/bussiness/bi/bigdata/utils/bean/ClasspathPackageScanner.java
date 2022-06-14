package com.bussiness.bi.bigdata.utils.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * 自动扫描指定package下的javabean，生成4个部分内容
 * <pre>
 *     1、private Xx  xx;
 *     2、xxBean = (XxBean) context.getBean("XxBean");
 *     3、< bean id="XxBean" class="a.b.c.XxBean" / >
 *     4、xxBean.getA(); xxBean.setA(1); or xxBean.setA(false); or xxBean.setA(null);
 * </pre>
 *
 * @author chenqixu
 */
public class ClasspathPackageScanner {

    private static final Logger logger = LoggerFactory.getLogger(ClasspathPackageScanner.class);
    private String basePackage;
    private ClassLoader cl;

    /**
     * 初始化
     *
     * @param basePackage
     */
    public ClasspathPackageScanner(String basePackage) {
        this.basePackage = basePackage;
        this.cl = getClass().getClassLoader();
    }

    /**
     * 入口
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {
        ClasspathPackageScanner scan = new ClasspathPackageScanner(
                "com.newland.bi.bigdata.bean.javabean"
//                "com.cqx.common.bean.javabean"
//                "com.newland.bi.resourcemanage.model.javabean"
        );
        List<String> nameList = scan.getFullyQualifiedClassNameList();
        scan.printHead(nameList);
        scan.printsetUp(nameList);
        scan.printSpringBase(nameList);
        scan.printMethod(nameList);
    }

    /**
     * 获取指定包下的所有字节码文件的全类名
     */
    public List<String> getFullyQualifiedClassNameList() throws IOException {
        logger.info("开始扫描包{}下的所有类", basePackage);
        return doScan(basePackage, new ArrayList<String>());
    }

    /**
     * 扫描jar包，未完成
     *
     * @param filePath
     * @param basePackage
     * @param nameList
     * @return
     * @throws IOException
     */
    private List<String> doScanJar(String filePath, String basePackage, List<String> nameList) throws IOException {
        String splashPath = StringUtil.dotToSplash(basePackage);
        List<String> names = null; // contains the name of the class file. e.g., Apple.class will be stored as "Apple"
        if (isJarFile(filePath)) {// 先判断是否是jar包，如果是jar包，通过JarInputStream产生的JarEntity去递归查询所有类
            if (logger.isDebugEnabled()) {
                logger.debug("{} 是一个JAR包", filePath);
            }
            names = readFromJarFile(filePath, splashPath);
        }

        for (String name : names) {
            if (isClassFile(name)) {
                nameList.add(toFullyQualifiedName(name, basePackage));
            } else {
                doScanJar(filePath, basePackage + "." + name, nameList);
            }
        }
        return nameList;
    }

    /**
     * doScan函数
     *
     * @param basePackage
     * @param nameList
     * @return
     * @throws IOException
     */
    private List<String> doScan(String basePackage, List<String> nameList) throws IOException {
        String splashPath = StringUtil.dotToSplash(basePackage);
        URL url = cl.getResource(splashPath);   //file:/D:/WorkSpace/java/ScanTest/target/classes/com/scan
        String filePath = StringUtil.getRootPath(url);
        List<String> names = null; // contains the name of the class file. e.g., Apple.class will be stored as "Apple"
        if (isJarFile(filePath)) {// 先判断是否是jar包，如果是jar包，通过JarInputStream产生的JarEntity去递归查询所有类
            if (logger.isDebugEnabled()) {
                logger.debug("{} 是一个JAR包", filePath);
            }
            names = readFromJarFile(filePath, splashPath);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("{} 是一个目录", filePath);
            }
            names = readFromDirectory(filePath);
        }
        for (String name : names) {
            if (isClassFile(name)) {
                nameList.add(toFullyQualifiedName(name, basePackage));
            } else {
                doScan(basePackage + "." + name, nameList);
            }
        }
        return nameList;
    }

    /**
     * 打印private Xx  xx;
     *
     * @param nameList
     */
    private void printHead(List<String> nameList) {
        System.out.println("// printHead");
        for (String n : nameList) {
            try {
                Class cls = Class.forName(n);
                String simpleName = cls.getSimpleName();
                System.out.println(String.format("private %s %s;",
                        simpleName, simpleName.toLowerCase()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }

    /**
     * 打印xxBean = (XxBean) context.getBean("XxBean");
     *
     * @param nameList
     */
    private void printsetUp(List<String> nameList) {
        System.out.println("// printsetUp");
        for (String n : nameList) {
            try {
                Class cls = Class.forName(n);
                String simpleName = cls.getSimpleName();
                System.out.println(String.format("%s = (%s) context.getBean(\"%s\");",
                        simpleName.toLowerCase(), simpleName, simpleName));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }

    /**
     * 打印< bean id="XxBean" class="a.b.c.XxBean" / >
     *
     * @param nameList
     */
    private void printSpringBase(List<String> nameList) {
        System.out.println("// printSpringBase");
        for (String n : nameList) {
            try {
                Class cls = Class.forName(n);
                String simpleName = cls.getSimpleName();
                System.out.println(String.format("<bean id=\"%s\" class=\"%s\"></bean>",
                        simpleName, n));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }

    /**
     * 打印xxBean.getA(); xxBean.setA(1); or xxBean.setA(false); or xxBean.setA(null);
     *
     * @param nameList
     */
    private void printMethod(List<String> nameList) {
        System.out.println("// printMethod");
        for (String n : nameList) {
            try {
                System.out.println("// " + n);
                Class cls = Class.forName(n);
                String simpleName = cls.getSimpleName().toLowerCase();
                BeanInfo beanInfo = Introspector.getBeanInfo(cls);
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor property : propertyDescriptors) {
                    String key = property.getName();
                    if (!key.equals("class")) {
                        Method setter = property.getWriteMethod();
                        if (setter != null) {
                            Class<?>[] parameterTypes = setter.getParameterTypes();
                            if (parameterTypes != null && parameterTypes.length == 1) {
                                String parameterTypeName = parameterTypes[0].getName().toLowerCase();
                                if (parameterTypeName.contains("int")) {
                                    System.out.println(String.format("%s.%s(1);", simpleName, setter.getName()));
                                } else if (parameterTypeName.contains("long")) {
                                    System.out.println(String.format("%s.%s(1L);", simpleName, setter.getName()));
                                } else if (parameterTypeName.contains("boolean")) {
                                    System.out.println(String.format("%s.%s(false);", simpleName, setter.getName()));
                                } else {
                                    System.out.println(String.format("%s.%s(null);", simpleName, setter.getName()));
                                }
                            }
                        }
                        Method getter = property.getReadMethod();
                        if (getter != null) {
                            System.out.println(String.format("%s.%s();", simpleName, getter.getName()));
                        }
                    }
                }
                System.out.println();
            } catch (ClassNotFoundException | IntrospectionException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }

    /**
     * 打印导入
     *
     * @param nameList
     */
    private void printImport(List<String> nameList) {
        for (String name : nameList) {
            if (!name.contains("$")) System.out.println(String.format("import %s;", name));
        }
    }

    /**
     * 打印所有方法
     *
     * @param cls
     */
    private void printAllMethod(Class cls) {
        String simpleName = cls.getSimpleName().toLowerCase();
        System.out.println("@Test");
        System.out.println(String.format("public void %sTest() {", simpleName));
        System.out.println("try {");
        System.out.println(String.format("%s %s = new %s();", cls.getSimpleName(), simpleName, cls.getSimpleName()));
        for (Method m : cls.getDeclaredMethods()) {
            String methodName = m.getName();
            Class<?>[] parameterTypes = m.getParameterTypes();
            StringBuilder sb = new StringBuilder();
            for (Class param : parameterTypes) {
                String parameterTypeName = param.getName().toLowerCase();
                if (parameterTypeName.contains("int")) {
                    sb.append("1,");
                } else if (parameterTypeName.contains("long")) {
                    sb.append("1L,");
                } else if (parameterTypeName.contains("boolean")) {
                    sb.append("false,");
                } else {
                    sb.append("null,");
                }
            }
            if (sb.length() > 0) {
                sb.delete(sb.length() - 1, sb.length());
                System.out.println(String.format("%s.%s(%s);", simpleName, methodName, sb.toString()));
            } else {
                System.out.println(String.format("%s.%s();", simpleName, methodName));
            }
        }
        System.out.println("} catch (Exception e) {}");
        System.out.println("}");
    }

    /**
     * 打印所有方法
     *
     * @param nameList
     */
    private void printAllMethod(List<String> nameList) {
        System.out.println("// printMethod");
        for (String n : nameList) {
            try {
                if (!n.contains("$")) {
                    System.out.println("// " + n);
                    Class cls = Class.forName(n);
                    printAllMethod(cls);
                }
                System.out.println();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }

    /**
     * 拼接扫描结果
     *
     * @param shortName
     * @param basePackage
     * @return
     */
    private String toFullyQualifiedName(String shortName, String basePackage) {
        StringBuilder sb = new StringBuilder(basePackage);
        sb.append('.');
        sb.append(StringUtil.trimExtension(shortName));
        //打印出结果
        logger.debug("打印出结果：{}", sb.toString());
        return sb.toString();
    }

    /**
     * 从JAR包中读取类
     *
     * @param jarPath
     * @param splashedPackageName
     * @return
     * @throws IOException
     */
    private List<String> readFromJarFile(String jarPath, String splashedPackageName) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("从JAR包中读取类: {}", jarPath);
        }
        List<String> nameList = new ArrayList<String>();
        try (JarInputStream jarIn = new JarInputStream(new FileInputStream(jarPath))) {
            JarEntry entry = jarIn.getNextJarEntry();
            while (null != entry) {
                String name = entry.getName();
                if (name.startsWith(splashedPackageName) && isClassFile(name)) {
                    String _splashedPackageName = splashedPackageName;
                    if (!_splashedPackageName.endsWith("/")) {
                        _splashedPackageName = _splashedPackageName + "/";
                    }
                    nameList.add(name.replaceFirst(_splashedPackageName, ""));
                }
                entry = jarIn.getNextJarEntry();
            }
        }

        return nameList;
    }

    /**
     * 从目录中读取
     *
     * @param path
     * @return
     */
    private List<String> readFromDirectory(String path) {
        File file = new File(path);
        String[] names = file.list();

        if (null == names) {
            return null;
        }

        return Arrays.asList(names);
    }

    /**
     * 判断是否是类
     *
     * @param name
     * @return
     */
    private boolean isClassFile(String name) {
        return name.endsWith(".class");
    }

    /**
     * 判断是否是jar包
     *
     * @param name
     * @return
     */
    private boolean isJarFile(String name) {
        return name.endsWith(".jar");
    }
}
