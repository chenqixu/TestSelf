package com.cqx.finance.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * LinkedBean
 *
 * @author chenqixu
 */
public class LinkedBean<T extends BeanComparable> {
    private static final Logger logger = LoggerFactory.getLogger(LinkedBean.class);
    private final Object lock = new Object();
    // 先按price排序，里面再按时间排序
    // 排序是从小到大
    // price是1维，这里简称dm1
    // 时间是2维，这里简称dm2
    private TreeMap<Float, TreeMap<Long, T>> priceElementsMap;
    private TreeMap<Float, LinkedList<T>> priceMap = new TreeMap<>();

    public LinkedBean() {
        this(false);
    }

    /**
     * 构造函数，是否倒序
     *
     * @param isReverseOrder 是否倒序
     */
    public LinkedBean(boolean isReverseOrder) {
        if (isReverseOrder) {
            this.priceElementsMap = new TreeMap<>(Comparator.reverseOrder());
        } else {
            this.priceElementsMap = new TreeMap<>();
        }
    }

    public void add(T t) {
        synchronized (lock) {
            TreeMap<Long, T> timeElementsMap = priceElementsMap.get(t.getHopePrice());
            if (timeElementsMap == null) {
                timeElementsMap = new TreeMap<>();
                priceElementsMap.put(t.getHopePrice(), timeElementsMap);
            }
            timeElementsMap.put(t.getHopeTime(), t);
        }
    }

    public T poll() {
        synchronized (lock) {
            // 取出最大的数据
            java.util.Map.Entry<Float, TreeMap<Long, T>> lastDm1Entry = priceElementsMap.lastEntry();
            if (lastDm1Entry != null) {
                TreeMap<Long, T> lastDm1 = lastDm1Entry.getValue();
                if (lastDm1 != null) {
                    int lastEntrySize = lastDm1.size();
                    if (lastEntrySize == 1) {
                        priceElementsMap.remove(lastDm1Entry.getKey());
                        return lastDm1.pollLastEntry().getValue();
                    } else if (lastEntrySize > 1) {
                        return lastDm1.pollLastEntry().getValue();
                    }
                }
            }
            return null;
        }
    }

    public T peek() {
        synchronized (lock) {
            // 取出最大的数据
            java.util.Map.Entry<Float, TreeMap<Long, T>> lastDm1Entry = priceElementsMap.lastEntry();
            if (lastDm1Entry != null) {
                TreeMap<Long, T> lastDm1 = lastDm1Entry.getValue();
                if (lastDm1 != null) {
                    return lastDm1.lastEntry().getValue();
                }
            }
            return null;
        }
    }

    public void remove(T t) {
        synchronized (lock) {
            TreeMap dm2 = priceElementsMap.get(t.getHopePrice());
            if (dm2.size() == 1) {
                priceElementsMap.remove(t.getHopePrice());
            } else {
                dm2.remove(t.getHopeTime());
            }
        }
    }

    public int size() {
        synchronized (lock) {
            return priceElementsMap.size();
        }
    }

    public TreeMap<Float, TreeMap<Long, T>> getElements() {
        synchronized (lock) {
            return priceElementsMap;
        }
    }

    public void getElements(LinkedBeanCallBack<T> callBack) {
        synchronized (lock) {
            callBack.callBack(priceElementsMap);
        }
    }

    @Deprecated
    public void add1(T t) {
        LinkedList<T> list = priceMap.get(t.getHopePrice());
        if (list == null) {
            list = new LinkedList<>();
            list.add(t);
            priceMap.put(t.getHopePrice(), list);
        } else {
            addElements(list, t);
        }
        logger.info("priceMap：{}", priceMap);
    }

    @Deprecated
    private void addElements(LinkedList<T> elements, T t) {
        if (elements.size() == 0) {
            elements.add(t);
        } else {
            quickSort(elements, t, elements.size() / 2);
        }
        logger.info("elements增加后：{}", elements);
    }

    @Deprecated
    private void quickSort(LinkedList<T> elements, T t, int index) {
        T tmp = elements.get(index);
        int ret = tmp.compareTo(t);
        logger.info("index: {}, compare: {}", index, ret);
        if (index == 0 || index == (elements.size() - 1)) {
            logger.info("不找了！");
            // 找到的比插入的小
            if (ret < 0) {
                elements.add(t);
            } else if (ret > 0) {// 找到的比插入的大
                elements.addFirst(t);
            } else {// 找到的和插入相等
                logger.warn("待处理！！");
            }
        } else {
            logger.info("继续找……");
            // 找到的比插入的小
            if (ret < 0) {
                int new_index = (elements.size() - index) / 2 + index;
                if (new_index == index) new_index = elements.size() - 1;
                quickSort(elements, t, new_index);
            } else if (ret > 0) {// 找到的比插入的大
                quickSort(elements, t, index / 2);
            } else {// 找到的和插入相等
                logger.warn("待处理！！");
            }
        }
    }

    public interface LinkedBeanCallBack<T> {

        void callBack(TreeMap<Float, TreeMap<Long, T>> elements);
    }
}
