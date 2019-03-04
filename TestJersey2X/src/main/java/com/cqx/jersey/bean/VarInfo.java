package com.cqx.jersey.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 环境变量
 *
 * @author chenqixu
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class VarInfo {
    /**
     * 变量id
     */
    private String varId;
    /**
     * 父变量id
     */
    private String varParentId;
    /**
     * 变量名称
     */
    private String varName;
    /**
     * 变量值
     */
    private String varValue;
    /**
     * 变量描述
     */
    private String varDesc;
    /**
     * 变量层级编号
     */
    private String levelId;
    /**
     * 变量中文名
     */
    private String varChsName;
}
