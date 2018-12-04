package com.cqx.oozie.graph;

import javax.swing.*;
import java.awt.*;

/**
 * 我的窗口测试类
 *
 * @author chenqixu
 * @date 2018/12/3 23:50
 */
public class MyFrame extends JFrame {

    public MyFrame() {
        Container panel = getContentPane();//主布局容器
        panel.setLayout(new BorderLayout());

        MyPanel myPanel = new MyPanel();
        panel.add(myPanel, BorderLayout.CENTER);

        setSize(400, 400);//设置窗口宽和高
        setLocation(400, 200);//设置窗口初始化位置
        setVisible(true);//设置窗口可见
        requestFocus();//请求焦点
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new MyFrame();
    }
}
