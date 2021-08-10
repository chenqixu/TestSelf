package com.cqx.common.bean.kafka;

import org.apache.avro.Schema;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 默认值的javabean
 *
 * @author chenqixu
 */
public class DefaultBean {
    private int default_int = 0;
    private long default_long = 0L;
    private float default_float = (float) 0;
    private double default_double = (double) 0;
    private String default_string = "";
    private boolean default_boolean = false;
    private ArrayList default_array = new ArrayList();
    private HashMap default_map = new HashMap();

    public Object getDefaultValue(Schema.Type type) {
        Object obj = null;
        switch (type) {
            case INT:
                obj = default_int;
                break;
            case FLOAT:
                obj = default_float;
                break;
            case DOUBLE:
                obj = default_double;
                break;
            case LONG:
                obj = default_long;
                break;
            case BOOLEAN:
                obj = default_boolean;
                break;
            case STRING:
                obj = default_string;
                break;
            case ARRAY:
                obj = default_array;
                break;
            case MAP:
                obj = default_map;
                break;
            case BYTES:
            case UNION:
            case ENUM:
            case NULL:
            case FIXED:
            case RECORD:
            default:
                break;
        }
        return obj;
    }

    public Object getValue(Schema.Type type, String val) {
        Object obj = null;
        switch (type) {
            case INT:
                obj = ((val != null && val.length() > 0) ? Integer.valueOf(val) : getDefaultValue(type));
                break;
            case FLOAT:
                obj = ((val != null && val.length() > 0) ? Float.valueOf(val) : getDefaultValue(type));
                break;
            case DOUBLE:
                obj = ((val != null && val.length() > 0) ? Double.valueOf(val) : getDefaultValue(type));
                break;
            case LONG:
                obj = ((val != null && val.length() > 0) ? Long.valueOf(val) : getDefaultValue(type));
                break;
            case BOOLEAN:
                obj = ((val != null && val.length() > 0) ? Boolean.valueOf(val) : getDefaultValue(type));
                break;
            case BYTES:
            case UNION:
            case MAP:
            case ENUM:
            case NULL:
            case ARRAY:
            case FIXED:
            case RECORD:
            case STRING:
            default:
                break;
        }
        return obj;
    }

    public void setDefault_int(int default_int) {
        this.default_int = default_int;
    }

    public void setDefault_long(long default_long) {
        this.default_long = default_long;
    }

    public void setDefault_float(float default_float) {
        this.default_float = default_float;
    }

    public void setDefault_double(double default_double) {
        this.default_double = default_double;
    }

    public void setDefault_string(String default_string) {
        this.default_string = default_string;
    }

    public void setDefault_boolean(boolean default_boolean) {
        this.default_boolean = default_boolean;
    }

    public void setDefault_array(ArrayList default_array) {
        this.default_array = default_array;
    }

    public void setDefault_map(HashMap default_map) {
        this.default_map = default_map;
    }
}
