package com.cqx.common.utils.xml;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Namespace;

import java.util.ArrayList;
import java.util.List;

/**
 * XMLParserBean
 *
 * @author chenqixu
 */
public class XMLParserElement {
    private String elementName;
    private String elementText;
    private Namespace elementNamespace;
    private List<XMLParserAttribute> attributeList;
    private List<XMLParserElement> childElementList;

    public XMLParserElement(Element element) {
        this.elementNamespace = element.getNamespace();
        this.elementName = element.getName();
        this.elementText = this.CDATA(element.getTextTrim());
        this.attributeList = new ArrayList<>();
        this.childElementList = new ArrayList<>();
        //加所有属性
        for (Object att_obj : element.attributes()) {
            if (att_obj instanceof Attribute) {
                this.attributeList.add(new XMLParserAttribute(((Attribute) att_obj)));
            }
        }
        //加所有子元素，会递归
        for (Object ele_obj : element.elements()) {
            if (ele_obj instanceof Element) {
                this.childElementList.add(new XMLParserElement((Element) ele_obj));
            }
        }
    }

    private String CDATA(String str) {
        return "<![CDATA[" + (str == null ? "" : str) + "]]>";
    }

    public String toXml() {
        StringBuilder sb = new StringBuilder();
        //名称
        sb.append("<")
                .append(this.elementName);
        //命名空间
        if (elementNamespace.getStringValue().contains(this.elementName)) {
            sb.append(" ")
                    .append("xmlns=\"")
                    .append(elementNamespace.getStringValue())
                    .append("\"");
        }
        //属性
        for (XMLParserAttribute xmlParserAttribute : this.attributeList) {
            sb.append(" ")
                    .append(xmlParserAttribute.getAttributeName())
                    .append("=\"")
                    .append(xmlParserAttribute.getAttributeValue())
                    .append("\" ");
        }
        sb.append(">");
        //子元素
        if (this.childElementList.size() > 0) {
            for (XMLParserElement xmlParserElement : this.childElementList) {
                sb.append(xmlParserElement.toXml());
            }
        } else {//内容
            sb.append(this.elementText);
        }
        //结尾
        sb.append("</")
                .append(this.elementName)
                .append(">");
        return sb.toString();
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public List<XMLParserAttribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<XMLParserAttribute> attributeList) {
        this.attributeList = attributeList;
    }

    public List<XMLParserElement> getChildElementList() {
        return childElementList;
    }

    public void setChildElementList(List<XMLParserElement> childElementList) {
        this.childElementList = childElementList;
    }

    public String getElementText() {
        return elementText;
    }

    public void setElementText(String elementText) {
        this.elementText = elementText;
    }
}
