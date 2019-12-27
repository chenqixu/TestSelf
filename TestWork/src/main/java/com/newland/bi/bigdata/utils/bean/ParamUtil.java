package com.newland.bi.bigdata.utils.bean;

import com.newland.bi.bigdata.annotation.BeanDesc;
import com.newland.bi.bigdata.bean.BaseBean;
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
            if (!key.equals(CLASS)) {
                setter.invoke(t, map.get(key));
                if (t instanceof BaseBean) {
                    String desc = fieldDesc.get(key);
                    if (desc != null) ((BaseBean) t).setBeanDesc(desc, map.get(key));
                }
            }
        }
        return t;
    }
}
