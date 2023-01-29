package com.cqx.finance.bean;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.common.utils.thread.BaseRunable;
import com.cqx.common.utils.thread.BaseRunableFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LinkedBeanTest {
    private static final Logger logger = LoggerFactory.getLogger(LinkedBeanTest.class);
    private StockCompany s1 = new StockCompany("000997", 15f);
    private volatile boolean quotaion = true;// up：true；down：false；
    private volatile int durationTime = 0;

    @Test
    public void add() {
        LinkedBean<StockOrderBean> linkedBean = new LinkedBean<>();
        linkedBean.add(new StockOrderBean(11.0f, 1000));
        SleepUtil.sleepMilliSecond(1);
        linkedBean.add(new StockOrderBean(10.0f, 1000));
        SleepUtil.sleepMilliSecond(1);
        linkedBean.add(new StockOrderBean(12.0f, 1000));
        SleepUtil.sleepMilliSecond(1);
        linkedBean.add(new StockOrderBean(13.0f, 1000));
        SleepUtil.sleepMilliSecond(1);
        linkedBean.add(new StockOrderBean(10.0f, 1000));

        logger.info("{}", linkedBean.getElements());
        StockOrderBean sc = linkedBean.peek();
        logger.info("{}", sc);
        logger.info("{}", linkedBean.getElements());
    }

    @Test
    public void price() {
        DecimalFormat df = new DecimalFormat("#0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        Random random = new Random(System.currentTimeMillis());
        float currentPrice = 15.00f;
        float currentPriceRN = 15.00f;
        for (int i = 0; i < 10; i++) {
            float rn = random.nextFloat();
            currentPrice += (rn * 100) / 1000f;
            currentPriceRN += Float.valueOf(String.format("%.2f", Math.round(rn * 100) / 1000f));
            // float={}, String.format={},
            logger.info("rn={}, 1={}, 2={}, 3={}"
                    , rn
                    , (rn * 100) / 1000f
                    , df.format((rn * 100) / 1000f)
//                    , Math.round(rn * 100) / 100f
//                    , Float.valueOf(String.format("%.2f", Math.round(rn * 100) / 1000f))
//                    , currentPriceRN
                    , df.format(currentPrice)
            );
        }
    }

    /**
     * 模拟行情切换
     *
     * @throws InterruptedException
     */
    @Test
    public void QuotationThreadTest() throws InterruptedException {
        BaseRunableFactory br = BaseRunableFactory.newInstance();
        br.addTask(new QuotationThread()).startTask();
        SleepUtil.sleepSecond(30);
        br.stopTask();
    }

    /**
     * 模拟开盘，交易
     *
     * @throws InterruptedException
     */
    @Test
    public void main() throws InterruptedException {
        LinkedBean<StockOrderBean> buyQueue = new LinkedBean<>();
        LinkedBean<StockOrderBean> sellQueue = new LinkedBean<>(true);
        BaseRunableFactory br = BaseRunableFactory.newInstance();
        br.addTask(new BuyThread(buyQueue))
                .addTask(new SellThread(sellQueue))
                .addTask(new FinalThread(buyQueue, sellQueue))
                .addTask(new RealTimeStock(buyQueue, sellQueue))
                .addTask(new QuotationThread())
                .startTask();
        SleepUtil.sleepSecond(100);
        br.stopTask();
    }

    /**
     * 实时大盘打印，输出买1到买5，卖1到卖5
     *
     * @param queue
     * @param num
     */
    private void printRealTimeStock(LinkedBean<StockOrderBean> queue, final int num) {
        queue.getElements(new LinkedBean.LinkedBeanCallBack<StockOrderBean>() {
            @Override
            public void callBack(TreeMap<Float, TreeMap<Long, StockOrderBean>> elements) {
                int count = 0;
                if (elements != null && elements.size() > 0) {
                    for (Map.Entry<Float, TreeMap<Long, StockOrderBean>> entry : elements.descendingMap().entrySet()) {
                        TreeMap<Long, StockOrderBean> treeMap = entry.getValue();
                        for (Map.Entry<Long, StockOrderBean> _entry : treeMap.entrySet()) {
                            count++;
                            logger.info("[实时大盘{}] [{} {}] {} {}", num, _entry.getValue().isType(), count, _entry.getValue().getHopePrice(), _entry.getValue().getCount());
                            if (count > 5) break;
                        }
                        if (count > 5) break;
                    }
                    logger.info("[实时大盘{}] 当前价格：{}，状态：{}", num, s1.getCurrentPrice()
                            , s1.isMaxDown() ? "跌停" : s1.isMaxUp() ? "涨停" : "正常交易");
                }
            }
        });
    }

    /**
     * 实时打印买1到买5，卖1到卖5
     */
    class RealTimeStock extends BaseRunable {
        LinkedBean<StockOrderBean> buyQueue;
        LinkedBean<StockOrderBean> sellQueue;
        AtomicInteger num = new AtomicInteger(0);

        RealTimeStock(LinkedBean<StockOrderBean> buyQueue, LinkedBean<StockOrderBean> sellQueue) {
            this.buyQueue = buyQueue;
            this.sellQueue = sellQueue;
        }

        @Override
        public void exec() throws Exception {
            int nowNum = num.incrementAndGet();
            printRealTimeStock(buyQueue, nowNum);
            printRealTimeStock(sellQueue, nowNum);
            SleepUtil.sleepMilliSecond(500);
        }
    }

    /**
     * 撮合交易系统
     */
    class FinalThread extends BaseRunable {
        LinkedBean<StockOrderBean> buyQueue;
        LinkedBean<StockOrderBean> sellQueue;

        FinalThread(LinkedBean<StockOrderBean> buyQueue, LinkedBean<StockOrderBean> sellQueue) {
            this.buyQueue = buyQueue;
            this.sellQueue = sellQueue;
        }

        @Override
        public void exec() throws Exception {
            StockOrderBean buy1 = buyQueue.peek();
            StockOrderBean sell1 = sellQueue.peek();
            // 买入价高于卖出价
            // 判断时间顺序
            // 判断能成交的数量
            if (buy1 != null && sell1 != null && buy1.getHopePrice() >= sell1.getHopePrice()) {
                int finalCount = 0;
                float finalPrice = 0f;
                if (buy1.getHopeTime() >= sell1.getHopeTime()) {
                    finalPrice = sell1.getHopePrice();
                } else {
                    finalPrice = buy1.getHopePrice();
                }
                if (buy1.getCount() > sell1.getCount()) {
                    // 卖1全卖掉了，买1还剩余
                    finalCount = sell1.getCount();
                    sellQueue.remove(sell1);
                    buy1.setCount(buy1.getCount() - finalCount);
                    logger.info("[系统撮合交易] 用户【{}】卖出，成交价：{}，成交量：{}"
                            , sell1.getCusName(), finalPrice, finalCount);
                    logger.info("[系统撮合交易] 用户【{}】买入，成交价：{}，成交量：{}，剩余挂单：{}，挂单买价：{}"
                            , buy1.getCusName(), finalPrice, finalCount, buy1.getCount(), buy1.getHopePrice());
                } else if (buy1.getCount() < sell1.getCount()) {
                    // 买1全买进了，卖1还没卖光
                    finalCount = buy1.getCount();
                    buyQueue.remove(buy1);
                    sell1.setCount(sell1.getCount() - finalCount);
                    logger.info("[系统撮合交易] 用户【{}】卖出，成交价：{}，成交量：{}，剩余挂单：{}，挂单卖价：{}"
                            , sell1.getCusName(), finalPrice, finalCount, sell1.getCount(), sell1.getHopePrice());
                    logger.info("[系统撮合交易] 用户【{}】买入，成交价：{}，成交量：{}"
                            , buy1.getCusName(), finalPrice, finalCount);
                } else {
                    // 买1和卖1的量相等
                    // 买1和卖1都完成
                    finalCount = sell1.getCount();
                    buyQueue.remove(buy1);
                    sellQueue.remove(sell1);
                    logger.info("[系统撮合交易] 用户【{}】卖出，成交价：{}，成交量：{}"
                            , sell1.getCusName(), finalPrice, finalCount);
                    logger.info("[系统撮合交易] 用户【{}】买入，成交价：{}，成交量：{}"
                            , buy1.getCusName(), finalPrice, finalCount);
                }
                // 设置实时成交价
                s1.setCurrentPrice(finalPrice);
            }
            SleepUtil.sleepMilliSecond(50);
        }
    }

    /**
     * 行情切换
     */
    class QuotationThread extends BaseRunable {
        TimeCostUtil tc = new TimeCostUtil();
        Random random = new Random(System.currentTimeMillis());

        @Override
        public void exec() throws Exception {
            if (durationTime == 0) {
                durationTime = random.nextInt(10000);
                quotaion = random.nextBoolean();
                logger.info("[行情切换] 第一次，持续时间：{} ms，行情：{}", durationTime, quotaion ? "买入" : "卖出");
            } else if (tc.tag(durationTime)) {
                durationTime = random.nextInt(10000);
                quotaion = random.nextBoolean();
                logger.info("[行情切换] 切换，持续时间：{} ms，行情：{}", durationTime, quotaion ? "买入" : "卖出");
            }
            SleepUtil.sleepMilliSecond(1);
        }
    }

    /**
     * 模拟买入
     */
    class BuyThread extends BaseRunable {
        Random random = new Random(System.currentTimeMillis());
        LinkedBean<StockOrderBean> buyQueue;

        BuyThread(LinkedBean<StockOrderBean> buyQueue) {
            this.buyQueue = buyQueue;
        }

        @Override
        public void exec() throws Exception {
            if (quotaion || random.nextBoolean()) {
                buy();
            }
            SleepUtil.sleepMilliSecond(100);
        }

        private void buy() {
            float currentPrice = s1.getCurrentPrice();
            float decimal = Math.round((currentPrice + random.nextFloat()) * 100) / 100f;
            // 不能超过涨停价格
            if (decimal > s1.getMaxUpPrice()) decimal = s1.getMaxUpPrice();
            int count = random.nextInt(10) * 1000;
            if (count == 0) count = 1000;
            StockOrderBean stockBean = new StockOrderBean("apple", StockOrderType.BUY, decimal, count);
            buyQueue.add(stockBean);
            logger.info("[用户]{} 申请买入价格：{}，申请买入数量：{}"
                    , stockBean.getCusName(), stockBean.getHopePrice(), stockBean.getCount());
        }
    }

    /**
     * 模拟卖出
     */
    class SellThread extends BaseRunable {
        Random random = new Random(System.currentTimeMillis());
        LinkedBean<StockOrderBean> sellQueue;

        SellThread(LinkedBean<StockOrderBean> sellQueue) {
            this.sellQueue = sellQueue;
        }

        @Override
        public void exec() throws Exception {
            if (!quotaion || random.nextBoolean()) {
                sell();
            }
            SleepUtil.sleepMilliSecond(100);
        }

        private void sell() {
            float currentPrice = s1.getCurrentPrice();
            float decimal = Math.round((currentPrice - random.nextFloat()) * 100) / 100f;
            // 不能超过跌停价格
            if (decimal < s1.getMaxDownPrice()) decimal = s1.getMaxDownPrice();
            int count = random.nextInt(10) * 1000;
            if (count == 0) count = 1000;
            StockOrderBean stockBean = new StockOrderBean("windows", StockOrderType.SELL, decimal, count);
            sellQueue.add(stockBean);
            logger.info("[用户]{} 申请卖出价格：{}，申请卖出数量：{}"
                    , stockBean.getCusName(), stockBean.getHopePrice(), stockBean.getCount());
        }
    }
}