package com.cqx.common.utils.xml;

import org.dom4j.Attribute;

/**
 * XMLParserAttributeBean
 *
 * @author chenqixu
 */
public class XMLParserAttribute {
    private String attributeName;
    private String attributeValue;

    public XMLParserAttribute(Attribute attribute) {
        this.attributeName = attribute.getName();
        this.attributeValue = attribute.getValue();
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }
}
