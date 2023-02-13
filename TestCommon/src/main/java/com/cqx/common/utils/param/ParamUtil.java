package com.cqx.common.utils.param;

import com.cqx.common.annotation.BeanDesc;
import com.cqx.common.bean.javabean.BaseBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * ParamUtil
 *
 * @author chenqixu
 */
public class ParamUtil {
    private static final Logger logger = LoggerFactory.getLogger(ParamUtil.class);
    private static final String CLASS = "class";

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

    /**
     * Map转Bean
     *
     * @param map
     * @param cls
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T setValueByMap(Map<String, String> map, Class<T> cls) throws Exception {
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
                    case "boolean":
                    case "java.lang.Boolean":
                        key_value = Boolean.valueOf(key_value.toString());
                        break;
                }
                setter.invoke(t, key_value);
                if (t instanceof BaseBean) {
                    try {
                        String desc = fieldDesc.get(key);
                        if (desc != null) ((BaseBean) t).setBeanDesc(desc, key_value.toString());
                    } catch (Exception e) {
                        throw new Exception(String.format("参数解析异常，key：%s，value：%s，异常信息：%s",
                                key, key_value, e.getMessage()), e);
                    }
                }
            }
        }
        return t;
    }

    /**
     * Bean转Map
     *
     * @param cls
     * @param bean
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> Map<String, String> beanToMap(Class<T> cls, Object bean) throws Exception {
        Map<String, String> map = new HashMap<>();
        BeanInfo beanInfo = Introspector.getBeanInfo(cls);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (Field field : cls.getDeclaredFields()) {
            BeanDesc beanDesc = field.getAnnotation(BeanDesc.class);
            if (beanDesc != null) {
                map.put(field.getName(), "");
            }
        }
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            Method getter = property.getReadMethod();
            if (!key.equals(CLASS)) {
                Object value = getter.invoke(bean);
                if (map.get(key) != null && value != null) map.put(key, value.toString());
            }
        }
        return map;
    }

    public static <T> T setNumberValDefault(Map param, String paramKey, T defaultValue) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        if (defaultValue == null) throw new NullPointerException(paramKey + "默认值不能为空！");
        Number value = (Number) param.get(paramKey);
        T t = defaultValue;
        if (value != null) {
            String className = defaultValue.getClass().getName();
            Class cls = defaultValue.getClass();
            //参数列表
            Class<?>[] parameterTypes = {String.class};
            //获取参数对应的构造方法
            Constructor<T> constructor = cls.getConstructor(parameterTypes);
            //根据类型设置参数
            switch (className) {
                case "java.lang.Long":
                    //带参构造
                    t = constructor.newInstance(String.valueOf(value.longValue()));
                    break;
                case "java.lang.Integer":
                    //带参构造
                    t = constructor.newInstance(String.valueOf(value.intValue()));
                    break;
                default:
                    break;
            }
        } else {
            logger.info("获取{}配置为空，使用默认值：{}", paramKey, defaultValue);
        }
        return t;
    }

    public static <T> T setValDefault(Map param, String paramKey, T defaultValue) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        if (defaultValue == null) throw new NullPointerException(paramKey + "默认值不能为空！");
        Object value = param.get(paramKey);
        T t = defaultValue;
        if (value != null) {
            String className = defaultValue.getClass().getName();
            Class cls = defaultValue.getClass();
            //参数列表
            Class<?>[] parameterTypes = {String.class};
            //获取参数对应的构造方法
            Constructor<T> constructor = cls.getConstructor(parameterTypes);
            //根据类型设置参数
            switch (className) {
                case "java.lang.Long":
                    //带参构造
                    t = constructor.newInstance(String.valueOf(((Number) value).longValue()));
                    break;
                case "java.lang.Integer":
                    //带参构造
                    t = constructor.newInstance(String.valueOf(((Number) value).intValue()));
                    break;
                case "java.lang.String":
                    t = constructor.newInstance((String) value);
                    break;
                case "java.lang.Boolean":
                    t = constructor.newInstance(String.valueOf(value));
                    break;
                default:
                    break;
            }
        } else {
            logger.info("获取{}配置为空，使用默认值：{}", paramKey, defaultValue);
        }
        return t;
    }

    public static String getStringVal(Map<?, ?> params, String name) {
        Object obj = params.get(name);
        if (obj != null) return obj.toString();
        else throw new NullPointerException(String.format("参数[%s]为空！", name));
    }
}
