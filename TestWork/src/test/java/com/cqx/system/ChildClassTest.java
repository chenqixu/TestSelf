package com.cqx.system;

import com.cqx.common.utils.system.CommonURLClassLoader;
import com.cqx.common.utils.system.ReflectionUtil;
import org.junit.Test;

public class ChildClassTest {
    // -Djava.system.class.loader=com.cqx.common.utils.system.CommonURLClassLoader
    @Test
    public void run() throws Exception {
        CommonURLClassLoader testClassLoader = new CommonURLClassLoader(ClassLoader.getSystemClassLoader());
        testClassLoader.setAppClass(ChildClass.class);
//        Class<?> clsP = testClassLoader.loadClass(ParentClass.class.getName());
        Class<?> clsC = testClassLoader.loadClass(ChildClass.class.getName());
//        Class clsP = Class.forName(ParentClass.class.getName(), true, testClassLoader);
//        Class clsC = Class.forName(ChildClass.class.getName(), true, testClassLoader);
//        Class clsP = Class.forName(ParentClass.class.getName());

        Object p = clsC.newInstance();
        ReflectionUtil.invokeMethod(p, "setParams", null, null);
//        System.out.println("find：" + m.getName());
//        for (Method method : clsC.getDeclaredMethods()) {
//            System.out.println(method.getName());
//            if (method.getName().equals("setParams")) {
//                method.invoke(p);
//            }
//        }
    }
}