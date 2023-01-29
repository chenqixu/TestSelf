package com.cqx.thinking;

import com.cqx.common.utils.system.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 汉诺塔
 *
 * @author chenqixu
 */
public class Hanoi {
    private static final Logger logger = LoggerFactory.getLogger(Hanoi.class);
    private HanoiBean one = new HanoiBean("第一根柱子");
    private HanoiBean two = new HanoiBean("第二根柱子");
    private HanoiBean three = new HanoiBean("第三根柱子");
    private List<HanoiBean> hanois = new ArrayList<>();

    public void init() {
        for (int i = 6; i > 0; i--) {
            one.add(i);
        }
        hanois.add(one);
        hanois.add(two);
        hanois.add(three);
    }

    public void peek(HanoiBean exclude) {
        SleepUtil.sleepMilliSecond(500);
        int num = 1;
        boolean flag = false;
        if (one.size() == 1) return;
        List<HanoiBean> loop = new ArrayList<>(hanois);
        loop.remove(exclude);
        for (HanoiBean hanoi : loop) {
            List<HanoiBean> copys = new ArrayList<>(hanois);
            copys.remove(hanoi);
            Integer current = hanoi.peek();
//            logger.info("第{}次循环，第一个值：{}，current：{}", num, current, hanoi);
            if (current != null) {
                for (HanoiBean copy : copys) {
                    Integer compare = copy.peek();
//                    logger.info("第{}次循环，第一个值：{}，compare：{}", num, compare, copy);
                    if (compare != null && compare > current) {
                        copy.add(hanoi.poll());
                        print();
                        peek(copy);
                        flag = true;
                        break;
                    } else if (compare == null) {
                        copy.add(hanoi.poll());
                        print();
                        peek(copy);
                        flag = true;
                        break;
                    }
                }
            }
            num++;
            if (flag) break;
        }
    }

    public void move() {

    }

    public void print() {
        logger.info("移动！当前排列：");
        logger.info("{}", one);
        logger.info("{}", two);
        logger.info("{}", three);
    }

    class HanoiBean {
        String name;
        LinkedBlockingDeque<Integer> column = new LinkedBlockingDeque<>();

        HanoiBean(String name) {
            this.name = name;
        }

        public int size() {
            return column.size();
        }

        @Override
        public String toString() {
            return String.format("name：%s，column：%s", name, column);
        }

        public String getName() {
            return name;
        }

        public Integer peek() {
            return column.peek();
        }

        public void add(int num) {
            column.addFirst(num);
        }

        public Integer poll() {
            return column.poll();
        }
    }
}
