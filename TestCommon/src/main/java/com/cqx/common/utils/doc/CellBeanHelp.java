package com.cqx.common.utils.doc;

import java.util.ArrayList;
import java.util.List;

/**
 * CellBeanHelp
 *
 * @author chenqixu
 */
public class CellBeanHelp {
    private static List<CellBean> lists;

    public CellBeanHelp newCellList() {
        lists = new ArrayList<>();
        return this;
    }

    public CellBeanHelp addCell(CellBean cellBean) {
        lists.add(cellBean);
        return this;
    }

    public List<CellBean> getCellList() {
        return lists;
    }
}
