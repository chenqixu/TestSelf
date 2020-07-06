package com.cqx.common.utils.system;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CommonURLClassLoader
 *
 * @author chenqixu
 */
public class CommonURLClassLoader extends URLClassLoader {

    private static final String oldClass = "ClassUtil";
    private static final String newClass = "ClassLoaderUtil";
    private static Map<String, String> appClass = new HashMap<>();
    private final ConcurrentHashMap<String, Object> locksMap = new ConcurrentHashMap<>();

    public CommonURLClassLoader(ClassLoader parent) {
        super(((URLClassLoader) parent).getURLs(), parent);
    }

    private void getSuperclass(Class cls) {
        if (cls != null) {
            appClass.put(cls.getName(), "");
            Class<?> superclass = cls.getSuperclass();
            if (superclass != null && !superclass.getName().contains("java.lang")) {
                getSuperclass(superclass);
            }
        }
    }

    public void setAppClass(Class cls) {
        getSuperclass(cls);
        System.out.println(appClass);
    }

    private Class defineClassFromClassFile(String className, byte[] classFile)
            throws ClassFormatError {
        return defineClass(className, classFile, 0, classFile.length);
    }

    private Class<?> replaceClass(String name)
            throws ClassNotFoundException {
        InputStream is = getResourceAsStream(name.replace('.', '/') + ".class");
        if (is == null) {
            throw new ClassNotFoundException();
        }
        ClassWriter classWriter = new CommonClassWriter(0);
        try {
            ClassReader classReader = new ClassReader(is);
            classReader.accept(classWriter, 0);
        } catch (IOException e) {
            throw new ClassNotFoundException();
        }
        Class c = defineClassFromClassFile(name, classWriter.toByteArray());
        return c;
    }

    private Object getLock(String name) {
        Object lock = new Object();
        Object oldLock = locksMap.putIfAbsent(name, lock);
        if (oldLock == null) {
            oldLock = lock;
        }
        return oldLock;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
//        System.out.println(String.format("name：%s", name));
        Object lock = getLock(name);
        synchronized (lock) {
            Class c = findLoadedClass(name);
            try {
                if (c == null) {
                    if (appClass.get(name) != null) {
                        // 将App.class中常量池（constant pool）中类A的名字的字符串改为类B的名字
                        c = replaceClass(name);
                        if (resolve) {
                            resolveClass(c);
                        }
                        return c;
                    }
                }
            } catch (ClassNotFoundException e) {
            }
        }
        return super.loadClass(name, resolve);
    }

    private static class CommonClassWriter extends ClassWriter {

        public CommonClassWriter(int flags) {
            super(flags);
        }

        @Override
        public int newUTF8(final String value) {
//            System.out.println(String.format("newUTF8：%s，%s", value, value.contains(oldClass)));
            if (value.contains(oldClass)) {
                String new_value = value.replace(oldClass, newClass);
//                System.out.println(String.format("new_value：%s", new_value));
                return super.newUTF8(new_value);
            }
            return super.newUTF8(value);
        }
    }
}
