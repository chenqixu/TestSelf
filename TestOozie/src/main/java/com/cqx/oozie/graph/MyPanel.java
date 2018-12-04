package com.cqx.oozie.graph;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

/**
 * 我的画图类
 *
 * @author chenqixu
 * @date 2018/12/3 23:44
 */
public class MyPanel extends JPanel {
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.red);
        Line2D l2 = new Line2D.Double(100, 100, 100, 300);
        g2.draw(l2);
    }
}
