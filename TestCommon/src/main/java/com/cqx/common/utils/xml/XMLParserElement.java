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
    private String attributeName;
    private List<XMLParserAttribute> attributeList;
    private List<XMLParserElement> childElementList;

    public XMLParserElement(Element element) {
        this.elementNamespace = element.getNamespace();
        this.elementName = element.getName();
        this.elementText = element.getTextTrim();
        this.attributeList = new ArrayList<>();
        this.childElementList = new ArrayList<>();
        //加所有属性
        for (Object att_obj : element.attributes()) {
            if (att_obj instanceof Attribute) {
                Attribute attribute = (Attribute) att_obj;
                this.attributeList.add(new XMLParserAttribute(attribute));
                if (attribute.getName().equals("name")) {
                    this.attributeName = attribute.getValue();
                }
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
            sb.append(this.getCDATAElementText());
        }
        //结尾
        sb.append("</")
                .append(this.elementName)
                .append(">");
        return sb.toString();
    }

    /**
     * 通过ID获取对应属性的内容
     *
     * @param id
     * @return
     */
    public String getAttributeValueByID(String id) {
        for (XMLParserAttribute xmlParserAttribute : this.attributeList) {
            if (xmlParserAttribute.getAttributeName().equals(id)) return xmlParserAttribute.getAttributeValue();
        }
        return null;
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

    public String getCDATAElementText() {
        return this.CDATA(elementText);
    }

    public Namespace getElementNamespace() {
        return elementNamespace;
    }

    public void setElementNamespace(Namespace elementNamespace) {
        this.elementNamespace = elementNamespace;
    }

    public String getAttributeName() {
        return attributeName;
    }
}
