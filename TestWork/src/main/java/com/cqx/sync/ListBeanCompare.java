package com.cqx.sync;

import com.cqx.annotation.BeanCompare.NOT_PK_KEY;
import com.cqx.annotation.BeanCompare.PK_KEY;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ListBeanCompare
 *
 * @author chenqixu
 */
public class ListBeanCompare<T> {
    private List<T> t1;
    private List<T> t2;
    private Class<T> cls;

    public ListBeanCompare(Class<T> cls) {
        this.cls = cls;
    }

    public void setT1(List<T> t1) {
        this.t1 = t1;
    }

    public void setT2(List<T> t2) {
        this.t2 = t2;
    }

    public void compare() throws Exception {
        if (t1 != null && t2 != null) {
            // 根据声明获取PK_KEY字段
            Object obj = cls.newInstance();
            for (Field field : cls.getDeclaredFields()) {
                PK_KEY pk = field.getAnnotation(PK_KEY.class);
                if (pk != null) {
                    System.out.println("PK:" + field.getName());
                }
                NOT_PK_KEY not_pk = field.getAnnotation(NOT_PK_KEY.class);
                if (not_pk != null) {
                    System.out.println("NOT_PK:" + field.getName());
                }
            }
            BeanInfo beanInfo = Introspector.getBeanInfo(cls);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                Method reader = property.getReadMethod();
                System.out.println("key:" + key);
            }
            // 根据声明获取非PK_KEY字段
            // 根据PK_KEY进行比较
            // List转Map
            Map<Object, T> t1Map = new HashMap<>();
            for (T t : t1) {
//                t1Map.put(,t);
            }
            // 相同
            // t1有，t2没有
            // t2有，t1没有
        }
    }
}
