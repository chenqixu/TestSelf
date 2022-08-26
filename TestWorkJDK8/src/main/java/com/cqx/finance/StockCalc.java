package com.cqx.finance;

import com.cqx.finance.bean.StockOrderBean;

import java.util.HashMap;
import java.util.Map;

/**
 * StockCalc
 *
 * @author chenqixu
 */
public class StockCalc {

    private final int SELL_WEIGHT = 1;
    // 库存
    private Map<Float, StockOrderBean> storeMap = new HashMap<>();

    public static void main(String[] args) {
    }

    public static void createCoordinate() {
        // 坐标系转换, Coordinate
        // 坐标轴打印, Coordinate axis
        // 4 |             .
        // 3 |          .
        // 2 |    .   .
        // 1 | .
        // 0 ------------->
        //   0 1 2 3 4 5
        // 横向坐标轴
        // 纵向坐标轴
        // 坐标系：1,1 2,2 2,3 3,4 4,5
        // 精度
        // 根据坐标系生成数据
        // Abscissa 横坐标
        // Ordinate 纵坐标
        CoordinateUtil coordinateUtil = new CoordinateUtil();
        coordinateUtil.addCoordinate("2022", 1);
        coordinateUtil.addCoordinate("2023", 2);
        coordinateUtil.addCoordinate("2024", 3);
        coordinateUtil.addCoordinate("2025", 4);
        coordinateUtil.addCoordinate("2026", 5);
        // 横坐标轴
        String[] CoordinateAbscissaAxis = {"0", "2022", "2023", "2024", "2025", "2026", "2027"};
        // 纵坐标轴
        Integer[] CoordinateOrdinateAxis = {0, 1, 2, 3, 4, 5};
        // 一行一行生成打印数据

        String[][] datass = {{" ", " ", "·"}, {" ", "·", " "}};
        StockCalc.printLineChart(datass);
    }

    public static void printLineChart(String[][] datass) {
        for (String[] datas : datass) {
            StringBuilder sb = new StringBuilder();
            for (String data : datas) {
                sb.append(data).append(" ");
            }
            System.out.println(sb.toString());
        }
    }

    /**
     * 算法1
     */
    public void calc1() {
        boolean isShare = false;
        boolean isAppend = true;
        double price = 13.00d;
        double capital = 150000d;
        double tenPerBonus = 2.5d;// per 10 stock
        double shares = capital / price;
        double allShares = shares;
        double allBonus = 0d;
        double append = 60000d;
        for (int i = 1; i <= 30; i++) {
            double _allShares = allShares;
            double bonus = 0d;
            double bonusShares = 0d;
            // 送股不分红
            if (i % 2 == 0 && isShare) {
                // 10送2
                bonusShares = _allShares / 5;
            } else {// 分红不送股
                bonus = allShares / 10 * tenPerBonus;
                bonusShares = bonus / price;
            }
            if (isAppend) {
                bonus += append;
                bonusShares += append / price;
            }
            allShares += bonusShares;
            allBonus += bonus;
            System.out.println(String.format("[%s 年] 分红前的股数: %.2f" +
                            ", 分红后的股数: %.2f" +
                            ", 分红: %.2f" +
                            ", 分红能换多少股: %.2f" +
                            ", 总分红: %.2f" +
                            ", 总股本: %.2f" +
                            ", 年化收益率: %.2f"
                    , i, _allShares, allShares, bonus, bonusShares, allBonus, allShares * price
                    , (allShares - _allShares) * 100 / _allShares));
        }
    }

    /**
     * 算法2
     */
    public void calc2(float realPrice) {
        // 每天开启就挂单卖出

        // 实时监测
        // 先卖再买
        // 先循环当前库存，是否满足卖出条件
        for (Map.Entry<Float, StockOrderBean> entry : storeMap.entrySet()) {
            Float storeBuyPrice = entry.getKey();
            StockOrderBean stockBuyBean = entry.getValue();
            if (storeBuyPrice + SELL_WEIGHT >= realPrice) {

            }
        }
        // 根据当前价格，获取是否已经有库存
    }
}
