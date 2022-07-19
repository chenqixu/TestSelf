package com.bussiness.bi.bigdata.realtimemonitor.monitor;

/**
 * 坐标
 *
 * @author chenqixu
 */
public class Coordinate {
    private String Abscissa; // 横坐标
    private int Ordinate; // 纵坐标

    public Coordinate(String Abscissa, int Ordinate) {
        this.Abscissa = Abscissa;
        this.Ordinate = Ordinate;
    }

    public String getAbscissa() {
        return Abscissa;
    }

    public void setAbscissa(String abscissa) {
        Abscissa = abscissa;
    }

    public int getOrdinate() {
        return Ordinate;
    }

    public void setOrdinate(int ordinate) {
        Ordinate = ordinate;
    }
}
