package com.bussiness.bi.bigdata.parser.java;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * FunctionParserBean
 *
 * @author chenqixu
 */
public class FunctionParserBean {
    private String access_modifiers;
    private String function_name;
    private List<String> function_param_type;
    private String function_param;
    private String function_result;

    public String toString() {
        return JSON.toJSONString(this);
    }

    public String getAccess_modifiers() {
        return access_modifiers;
    }

    public void setAccess_modifiers(String access_modifiers) {
        this.access_modifiers = access_modifiers;
    }

    public String getFunction_name() {
        return function_name;
    }

    public void setFunction_name(String function_name) {
        this.function_name = function_name;
    }

    public List<String> getFunction_param_type() {
        return function_param_type;
    }

    public void setFunction_param_type(List<String> function_param_type) {
        this.function_param_type = function_param_type;
    }

    public String getFunction_result() {
        return function_result;
    }

    public void setFunction_result(String function_result) {
        this.function_result = function_result;
    }

    public String getFunction_param() {
        return function_param;
    }

    public void setFunction_param(String function_param) {
        this.function_param = function_param;
    }
}
