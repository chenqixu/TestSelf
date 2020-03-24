package com.cqx.common.utils.agent;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Stack;

/**
 * 对象内存大小获取类
 *
 * @author chenqixu
 */
public class ObjectSizeFetcherAgent {
    private static final MyLogger logger = MyLoggerFactory.getLogger(ObjectSizeFetcherAgent.class);
    // instrumentation 是一个 java.lang.instrument.Instrumentation 的实例，由 JVM 自动传入
    private static Instrumentation instrumentation;

    /**
     * 这个方法先于主方法(main)执行
     *
     * @param args
     * @param inst
     */
    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

//    /**
//     * 直接计算当前对象占用空间大小，包括当前类及超类的基本类型实例字段大小、
//     * 引用类型实例字段引用大小、实例基本类型数组总占用空间、实例引用类型数组引用本身占用空间大小;
//     * 但是不包括超类继承下来的和当前类声明的实例引用字段的对象本身的大小、实例引用数组引用的对象本身的大小
//     *
//     * @param o 需要计算内存的对象
//     * @return 返回内存大小
//     */
//    public static long sizeOf(Object o) {
//        if (instrumentation == null) {
//            throw new IllegalStateException(
//                    "Can not access instrumentation environment.\n"
//                            + "Please check if jar file containing SizeOfAgent class is \n"
//                            + "specified in the java's \"-javaagent\" command line argument.");
//        }
//        return instrumentation.getObjectSize(o);
//    }
//
//    /**
//     * 递归计算当前对象占用空间总大小，包括当前类和超类的实例字段大小以及实例字段引用对象大小
//     *
//     * @param objP
//     * @return
//     * @throws IllegalAccessException
//     */
//    public static long fullSizeOf(Object objP) throws IllegalAccessException {
//        Set<Object> visited = new HashSet<>();
//        Deque<Object> toBeQueue = new ArrayDeque<>();
//        toBeQueue.add(objP);
//        long size = 0L;
//        while (toBeQueue.size() > 0) {
//            Object obj = toBeQueue.poll();
//            logger.info("poll：{}", obj);
//            //sizeOf的时候已经计基本类型和引用的长度，包括数组
//            size += skipObject(visited, obj) ? 0L : sizeOf(obj);
//            visited.add(obj);
//            Class<?> tmpObjClass = obj.getClass();
//            if (tmpObjClass.isArray()) {
//                //[I , [F 基本类型名字长度是2
//                if (tmpObjClass.getName().length() > 2) {//skip primitive type array
//                    for (int i = 0, len = Array.getLength(obj); i < len; i++) {
//                        Object tmp = Array.get(obj, i);
//                        if (tmp != null) {
//                            //非基本类型需要深度遍历其对象
//                            toBeQueue.add(Array.get(obj, i));
//                        }
//                    }
//                }
//            } else {
//                while (tmpObjClass != null) {
//                    //获取字段
//                    Field[] fields = tmpObjClass.getDeclaredFields();
//                    for (Field field : fields) {
//                        if (Modifier.isStatic(field.getModifiers())//静态不计
//                                || field.getType().isPrimitive()) {//基本类型不重复计
//                            continue;
//                        }
//
//                        field.setAccessible(true);
//                        Object fieldValue = field.get(obj);
//                        if (fieldValue == null) {
//                            continue;
//                        }
//                        toBeQueue.add(fieldValue);
//                    }
//                    tmpObjClass = tmpObjClass.getSuperclass();
//                }
//            }
//        }
//        return size;
//    }
//
//    /**
//     * String.intern的对象不计；计算过的不计，也避免死循环
//     *
//     * @param visited
//     * @param obj
//     * @return
//     */
//    private static boolean skipObject(Set<Object> visited, Object obj) {
//        if (obj instanceof String && obj == ((String) obj).intern()) {
//            return true;
//        }
//        return visited.contains(obj);
//    }

    /**
     *      * Returns object size without member sub-objects.
     *      *
     *      * @param o
     *      *            object to get size of
     *      * @return object size
     *     
     */
    public static long sizeOf(Object o) {
        if (instrumentation == null) {
            throw new IllegalStateException(
                    "Can not access instrumentation environment.\n"
                            + "Please check if jar file containing SizeOfAgent class is \n"
                            + "specified in the java's \"-javaagent\" command line argument.");
        }
        return instrumentation.getObjectSize(o);
    }

    /**
     * Calculates full size of object iterating over its hierarchy graph.
     *
     * @param obj             object to calculate size of
     * @return object size
     */
    public static long fullSizeOf(Object obj) {
        Map<Object, Object> visited = new IdentityHashMap<>();
        Stack<Object> stack = new Stack<>();
        long result = internalSizeOf(obj, stack, visited);
        while (!stack.isEmpty()) {
            result += internalSizeOf(stack.pop(), stack, visited);
        }
        visited.clear();
        return result;
    }

    private static boolean skipObject(Object obj, Map<Object, Object> visited) {
        if (obj instanceof String) {
            // skip interned string
            if (obj == ((String) obj).intern()) {
                return true;
            }
        }
        return (obj == null) // skip visited object
                || visited.containsKey(obj);
    }

    private static long internalSizeOf(Object obj, Stack<Object> stack,
                                       Map<Object, Object> visited) {
        if (skipObject(obj, visited)) {
            return 0;
        }
        visited.put(obj, null);
        long result = 0;
        // get size of object + primitive variables + member pointers
        result += sizeOf(obj);
        // process all array elements
        Class clazz = obj.getClass();
        if (clazz.isArray()) {
            if (clazz.getName().length() != 2) {// skip primitive type array
                int length = Array.getLength(obj);
                for (int i = 0; i < length; i++) {
                    stack.add(Array.get(obj, i));
                }
            }
            return result;
        }
        // process all fields of the object
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (!Modifier.isStatic(fields[i].getModifiers())) {
                    if (fields[i].getType().isPrimitive()) {
                        continue; // skip primitive fields
                    } else {
                        fields[i].setAccessible(true);
                        try {
                            // objects to be estimated are put to stack
                            Object objectToAdd = fields[i].get(obj);
                            if (objectToAdd != null) {
                                stack.add(objectToAdd);
                            }
                        } catch (IllegalAccessException ex) {
                            assert false;
                        }
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return result;
    }
}
