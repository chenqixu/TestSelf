package com.cqx.calcite.parser.bean;

import java.util.List;

/**
 * I_clause
 *
 * @author chenqixu
 */
public interface I_clause<T> {
    T getVal();

    void setVal(T t);

    operationElement getParent();

    void setParent(operationElement parent);

    List<I_clause> getChilds();
}
