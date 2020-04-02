package com.cqx.common.utils.param;

import com.cqx.common.annotation.BeanDesc;
import com.cqx.common.bean.javabean.BaseBean;
import org.slf4j.Logger;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * ParamUtil
 *
 * @author chenqixu
 */
public class ParamUtil {

    public static final String CLASS = "class";

    /**
     * 日志打印
     *
     * @param baseBean
     * @param logger
     */
    public static void info(BaseBean baseBean, Logger logger) {
        if (logger != null) {
            for (Map.Entry<String, String> entry : baseBean.listBeanDesc().entrySet()) {
                logger.info("==步骤【0】：参数打印，【参数名称】" + entry.getKey() + "，【参数内容】" + entry.getValue());
            }
        }
    }

    public <T> T setValueByMap(Map<String, String> map, Class<T> cls) throws Exception {
        if (map == null) throw new NullPointerException("输入参数Map为空");
        Map<String, String> fieldDesc = new HashMap<>();
        T t = cls.newInstance();
        for (Field field : cls.getDeclaredFields()) {
            BeanDesc beanDesc = field.getAnnotation(BeanDesc.class);
            if (beanDesc != null) {
                fieldDesc.put(field.getName(), beanDesc.value());
            }
        }
        BeanInfo beanInfo = Introspector.getBeanInfo(cls);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            Method setter = property.getWriteMethod();
            Method getter = property.getReadMethod();
            if (!key.equals(CLASS)) {
                //类型转换，bean里可能不是String
                Class<?> returnType = getter.getReturnType();
                Object key_value = map.get(key);
                switch (returnType.getName()) {
                    case "java.lang.String":
                        break;
                    case "int":
                    case "java.lang.Integer":
                        key_value = Integer.valueOf(key_value.toString());
                        break;
                    case "long":
                    case "java.lang.Long":
                        key_value = Long.valueOf(key_value.toString());
                        break;
                }
                setter.invoke(t, key_value);
                if (t instanceof BaseBean) {
                    String desc = fieldDesc.get(key);
                    if (desc != null) ((BaseBean) t).setBeanDesc(desc, map.get(key));
                }
            }
        }
        return t;
    }
}
