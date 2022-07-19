package com.bussiness.bi.bigdata.realtimemonitor.monitor;

import java.util.ArrayList;
import java.util.List;

/**
 * 坐标工具
 *
 * @author chenqixu
 */
public class CoordinateUtil {
    private List<Coordinate> list = new ArrayList<>();

    public void addCoordinate(String Abscissa, int Ordinate) {
        list.add(new Coordinate(Abscissa, Ordinate));
    }

    public List<Coordinate> getList() {
        return list;
    }
}
