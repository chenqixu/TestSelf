package com.cqx.common.utils.jdbc;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;

import java.io.Serializable;
import java.util.*;

/**
 * BeanUtil
 *
 * @author chenqixu
 */
public class BeanUtil implements Cloneable, Serializable {

    private Object obj;
    private String fields;
    private LinkedHashMap properties;
    private LinkedHashMap<String, Object> fieldsMap;
    private List<String> fieldsType;

    public Object generateObject(LinkedHashMap properties) {
        this.properties = properties;
        fieldsMap = new LinkedHashMap<>();
        fieldsType = new ArrayList<>();
        BeanGenerator generator = new BeanGenerator();
        Set keySet = properties.keySet();
        StringBuffer sb = new StringBuffer();
        for (Iterator i = keySet.iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            generator.addProperty(key, (Class) properties.get(key));
            String _key = key.toLowerCase();
            fieldsMap.put(_key, "");
            fieldsType.add(_key);
            sb.append(_key + ",");
        }
        obj = generator.create();
        sb.delete(sb.length() - 1, sb.length());
        fields = sb.toString();
        return obj;
    }

    public Object getValue(Object obj, String property) {
        BeanMap beanMap = BeanMap.create(obj);
        return beanMap.get(property);
    }

    public Object getValue(String property) {
        return getValue(obj, property);
    }

    public void setValue(Object obj, String property, Object value) {
        BeanMap beanMap = BeanMap.create(obj);
        String _property = property.toLowerCase();
        beanMap.put(_property, value);
        fieldsMap.put(_property, value);
    }

    public void setValue(String property, Object value) {
        setValue(obj, property, value);
    }

    public Object getObj() {
        return obj;
    }

    public Class getObjClass() {
        return obj.getClass();
    }

    public String getFields() {
        return fields;
    }

    public LinkedHashMap<String, Object> getFieldsMap() {
        return fieldsMap;
    }

    public List<String> getFieldsType() {
        return fieldsType;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BeanUtil newBeanUtil = (BeanUtil) super.clone();
        newBeanUtil.obj = newBeanUtil.generateObject(properties);
        return newBeanUtil;
//        ByteArrayOutputStream byteOut = null;
//        ObjectOutputStream objOut = null;
//        ByteArrayInputStream byteIn = null;
//        ObjectInputStream objIn = null;
//        try {
//            byteOut = new ByteArrayOutputStream();
//            objOut = new ObjectOutputStream(byteOut);
//            objOut.writeObject(this);
//            byteIn = new ByteArrayInputStream(byteOut.toByteArray());
//            objIn = new ObjectInputStream(byteIn);
//            return (BeanUtil) objIn.readObject();
//        } catch (IOException e) {
//            throw new RuntimeException("Clone Object failed in IO.", e);
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException("Class not found.", e);
//        } finally {
//            try {
//                byteIn = null;
//                byteOut = null;
//                if (objOut != null) objOut.close();
//                if (objIn != null) objIn.close();
//            } catch (IOException e) {
//            }
//        }
    }
}
