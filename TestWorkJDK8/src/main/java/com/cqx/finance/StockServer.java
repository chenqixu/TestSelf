package com.cqx.finance;

import com.alibaba.fastjson.JSON;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.common.utils.thread.BaseRunable;
import com.cqx.common.utils.thread.BaseRunableFactory;
import com.cqx.finance.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务端
 *
 * @author chenqixu
 */
public class StockServer {
    private static final Logger logger = LoggerFactory.getLogger(StockServer.class);
    private String filePath = "d:\\tmp\\data\\stock\\%s.txt";
    private ConcurrentHashMap<String, LinkedBean<StockOrderBean>> buyQueueMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, LinkedBean<StockOrderBean>> sellQueueMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, LinkedBean<StockOrderBean>> completedQueueMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, StockCompany> stockCompanyMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, FileUtil> fileUtilMap = new ConcurrentHashMap<>();
    private BaseRunableFactory taskFactory = BaseRunableFactory.newInstance();
    private volatile boolean quotaion = true;// up：true；down：false；
    private volatile int durationTime = 0;
    private DecimalFormat df = new DecimalFormat("#0.00");

    public StockServer() {
        this(null);
    }

    public StockServer(String filePath) {
        if (filePath != null && filePath.length() > 0) {
            this.filePath = filePath;
        }
        this.df.setRoundingMode(RoundingMode.HALF_UP);
    }

    public String getFileInfoPath() {
        return filePath.replace(".txt", "_info.txt");
    }

    /**
     * 上市
     *
     * @param stockCompany
     */
    public void addCompany(StockCompany stockCompany) {
        String _companyName = stockCompany.getCompanyName();
        stockCompanyMap.put(_companyName, stockCompany);
        buyQueueMap.put(_companyName, new LinkedBean<>());
        sellQueueMap.put(_companyName, new LinkedBean<>(true));
        completedQueueMap.put(_companyName, new LinkedBean<>());
        String fileName = "";
        try {
            FileUtil fileUtil = new FileUtil();
            fileUtilMap.put(_companyName, fileUtil);
            fileName = String.format(filePath, _companyName);
            fileUtil.createFile(fileName);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("创建[" + fileName + "]文件失败！");
        }
    }

    /**
     * 获取实时价格
     *
     * @param companyName
     * @return
     */
    public float queryCurrent(String companyName) {
        return stockCompanyMap.get(companyName).getCurrentPrice();
    }

    public float queryMaxUpPrice(String companyName) {
        return stockCompanyMap.get(companyName).getMaxUpPrice();
    }

    public float queryMaxDownPrice(String companyName) {
        return stockCompanyMap.get(companyName).getMaxDownPrice();
    }

    /**
     * 申请买入
     *
     * @param stockBean
     */
    public void applyBuy(StockOrderBean stockBean) {
        buyQueueMap.get(stockBean.getCompanyName()).add(stockBean);
        logger.info("[用户]{} 申请买入股票[{}] 买入价格：{}，申请买入数量：{}"
                , stockBean.getCusName(), stockBean.getCompanyName()
                , stockBean.getHopePrice(), stockBean.getCount());
    }

    /**
     * 申请卖出
     *
     * @param stockBean
     */
    public void applySell(StockOrderBean stockBean) {
        sellQueueMap.get(stockBean.getCompanyName()).add(stockBean);
        logger.info("[用户]{} 申请卖出股票[{}] 卖出价格：{}，申请卖出数量：{}"
                , stockBean.getCusName(), stockBean.getCompanyName()
                , stockBean.getHopePrice(), stockBean.getCount());
    }

    /**
     * 实时大盘打印，输出买1到买5，卖1到卖5
     *
     * @param queue
     * @param num
     */
    private void printRealTimeStock(StockCompany s1, LinkedBean<StockOrderBean> queue, final int num) {
        queue.getElements(new LinkedBean.LinkedBeanCallBack<StockOrderBean>() {
            @Override
            public void callBack(TreeMap<Float, TreeMap<Long, StockOrderBean>> elements) {
                int count = 0;
                if (elements != null && elements.size() > 0) {
                    for (Map.Entry<Float, TreeMap<Long, StockOrderBean>> entry : elements.descendingMap().entrySet()) {
                        TreeMap<Long, StockOrderBean> treeMap = entry.getValue();
                        for (Map.Entry<Long, StockOrderBean> _entry : treeMap.entrySet()) {
                            count++;
                            logger.info("[{} 实时大盘{}] [{} {}] {} {}", s1.getCompanyName(), num
                                    , _entry.getValue().isType(), count, _entry.getValue().getHopePrice()
                                    , _entry.getValue().getCount());
                            if (count > 5) break;
                        }
                        if (count > 5) break;
                    }
                    logger.info("[{} 实时大盘{}] 当前价格：{}，状态：{}", s1.getCompanyName(), num
                            , s1.getCurrentPrice(), s1.isMaxDown() ? "跌停" : s1.isMaxUp() ? "涨停" : "正常交易");
                }
            }
        });
    }

    private float randomPrice(Random random, float currentPrice, StockOrderType type) {
        switch (type) {
            case BUY:
                currentPrice += (random.nextFloat() * 100) / 1000f;
                break;
            case SELL:
                currentPrice -= (random.nextFloat() * 100) / 1000f;
                break;
            default:
                break;
        }
        return Float.valueOf(df.format(currentPrice));
    }

    /**
     * 启动服务
     */
    public void start() {
        if (taskFactory != null) {
            for (StockCompany _sc : stockCompanyMap.values()) {
                String _companyName = _sc.getCompanyName();
                // 模拟买入
                taskFactory.addTask(new AnalogBuyThread(_companyName))
                        // 模拟卖出
                        .addTask(new AnalogSellThread(_companyName))
                        // 撮合交易系统
                        .addTask(new FinalThread(_sc))
                        // 实时大盘
                        .addTask(new RealTimeStock(_sc))
                        // 行情切换
                        .addTask(new QuotationThread())
                ;
            }
            taskFactory.startTask();
        }
    }

    /**
     * 停止服务
     *
     * @throws InterruptedException
     */
    public void stop() throws InterruptedException {
        if (taskFactory != null) {
            taskFactory.stopTask();
        }
        if (fileUtilMap != null && fileUtilMap.size() > 0) {
            for (FileUtil _fu : fileUtilMap.values()) {
                if (_fu != null) {
                    _fu.closeWrite();
                }
            }
        }
    }

    /**
     * 撮合交易系统
     */
    class FinalThread extends BaseRunable {
        LinkedBean<StockOrderBean> buyQueue;
        LinkedBean<StockOrderBean> sellQueue;
        LinkedBean<StockOrderBean> completedQueue;
        StockCompany s1;
        FileUtil fileUtil;
        List<StockData> stockDataList;

        FinalThread(StockCompany s1) {
            this.s1 = s1;
            this.buyQueue = buyQueueMap.get(s1.getCompanyName());
            this.sellQueue = sellQueueMap.get(s1.getCompanyName());
            this.fileUtil = fileUtilMap.get(s1.getCompanyName());
            this.stockDataList = new ArrayList<>();
        }

        @Override
        public void exec() throws Exception {
            StockOrderBean buy1 = buyQueue.peek();
            StockOrderBean sell1 = sellQueue.peek();
            // 买入价高于卖出价
            // 判断时间顺序
            // 判断能成交的数量
            if (buy1 != null && sell1 != null && buy1.getHopePrice() >= sell1.getHopePrice()) {
                // 成交量
                int finalCount = 0;
                // 成交价
                float finalPrice = 0f;
                // 成交时间
                long finalTime = System.currentTimeMillis();
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
                    logger.info("[系统撮合交易] 用户【{}】卖出【{}】，成交价：{}，成交量：{}"
                            , sell1.getCusName(), s1.getCompanyName(), finalPrice, finalCount);
                    logger.info("[系统撮合交易] 用户【{}】买入【{}】，成交价：{}，成交量：{}，剩余挂单：{}，挂单买价：{}"
                            , buy1.getCusName(), s1.getCompanyName(), finalPrice, finalCount, buy1.getCount(), buy1.getHopePrice());
                } else if (buy1.getCount() < sell1.getCount()) {
                    // 买1全买进了，卖1还没卖光
                    finalCount = buy1.getCount();
                    buyQueue.remove(buy1);
                    sell1.setCount(sell1.getCount() - finalCount);
                    logger.info("[系统撮合交易] 用户【{}】卖出【{}】，成交价：{}，成交量：{}，剩余挂单：{}，挂单卖价：{}"
                            , sell1.getCusName(), s1.getCompanyName(), finalPrice, finalCount, sell1.getCount(), sell1.getHopePrice());
                    logger.info("[系统撮合交易] 用户【{}】买入【{}】，成交价：{}，成交量：{}"
                            , buy1.getCusName(), s1.getCompanyName(), finalPrice, finalCount);
                } else {
                    // 买1和卖1的量相等
                    // 买1和卖1都完成
                    finalCount = sell1.getCount();
                    buyQueue.remove(buy1);
                    sellQueue.remove(sell1);
                    logger.info("[系统撮合交易] 用户【{}】卖出【{}】，成交价：{}，成交量：{}"
                            , sell1.getCusName(), s1.getCompanyName(), finalPrice, finalCount);
                    logger.info("[系统撮合交易] 用户【{}】买入【{}】，成交价：{}，成交量：{}"
                            , buy1.getCusName(), s1.getCompanyName(), finalPrice, finalCount);
                }
                // 设置实时成交价
                s1.setCurrentPrice(finalPrice);
                // 设置实时交易量
                s1.setCount(finalCount);
                stockDataList.add(new StockData(finalTime, finalPrice, finalCount));
            }
            SleepUtil.sleepMilliSecond(50);
        }

        @Override
        public void lastExec() throws Exception {
            // 写入成交数据
            fileUtil.write(JSON.toJSONString(stockDataList));
            // 写入info信息
            FileUtil infoFileUtil = null;
            try {
                infoFileUtil = new FileUtil();
                infoFileUtil.createFile(String.format(getFileInfoPath(), s1.getCompanyName()));
                infoFileUtil.write(JSON.toJSONString(s1));
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException("创建[" + getFileInfoPath() + "]文件失败！");
            } finally {
                if (infoFileUtil != null) infoFileUtil.closeWrite();
            }
        }
    }

    /**
     * 实时打印买1到买5，卖1到卖5
     */
    class RealTimeStock extends BaseRunable {
        LinkedBean<StockOrderBean> buyQueue;
        LinkedBean<StockOrderBean> sellQueue;
        AtomicInteger num = new AtomicInteger(0);
        StockCompany s1;

        RealTimeStock(StockCompany s1) {
            this.s1 = s1;
            this.buyQueue = buyQueueMap.get(s1.getCompanyName());
            this.sellQueue = sellQueueMap.get(s1.getCompanyName());
        }

        @Override
        public void exec() throws Exception {
            int nowNum = num.incrementAndGet();
            printRealTimeStock(s1, buyQueue, nowNum);
            printRealTimeStock(s1, sellQueue, nowNum);
            SleepUtil.sleepMilliSecond(500);
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
    class AnalogBuyThread extends BaseRunable {
        Random random = new Random(System.currentTimeMillis());
        String companyName;
        float maxUpPrice;

        AnalogBuyThread(String companyName) {
            this.companyName = companyName;
            this.maxUpPrice = queryMaxUpPrice(companyName);
        }

        @Override
        public void exec() throws Exception {
            if (quotaion || random.nextBoolean()) {
                analogBuy();
            }
            SleepUtil.sleepMilliSecond(100);
        }

        private void analogBuy() {
            float currentPrice = queryCurrent(companyName);
//            float decimal = Math.round((currentPrice + random.nextFloat()) * 100) / 100f;
            float decimal = randomPrice(random, currentPrice, StockOrderType.BUY);
            // 不能超过涨停价格
            if (decimal > maxUpPrice) decimal = maxUpPrice;
            int count = random.nextInt(10) * 1000;
            if (count == 0) count = 1000;
            StockOrderBean stockBean = new StockOrderBean("apple", companyName
                    , StockOrderType.BUY, decimal, count);
            // 申请买入
            applyBuy(stockBean);
            logger.info("[用户]{} 申请买入价格：{}，申请买入数量：{}"
                    , stockBean.getCusName(), stockBean.getHopePrice(), stockBean.getCount());
        }
    }

    /**
     * 模拟卖出
     */
    class AnalogSellThread extends BaseRunable {
        Random random = new Random(System.currentTimeMillis());
        String companyName;
        float maxDownPrice;

        AnalogSellThread(String companyName) {
            this.companyName = companyName;
            this.maxDownPrice = queryMaxDownPrice(companyName);
        }

        @Override
        public void exec() throws Exception {
            if (!quotaion || random.nextBoolean()) {
                analogSell();
            }
            SleepUtil.sleepMilliSecond(100);
        }

        private void analogSell() {
            float currentPrice = queryCurrent(companyName);
//            float decimal = Math.round((currentPrice - random.nextFloat()) * 100) / 100f;
            float decimal = randomPrice(random, currentPrice, StockOrderType.SELL);
            // 不能超过跌停价格
            if (decimal < maxDownPrice) decimal = maxDownPrice;
            int count = random.nextInt(10) * 1000;
            if (count == 0) count = 1000;
            StockOrderBean stockBean = new StockOrderBean("windows", companyName
                    , StockOrderType.SELL, decimal, count);
            // 申请卖出
            applySell(stockBean);
            logger.info("[用户]{} 申请卖出价格：{}，申请卖出数量：{}"
                    , stockBean.getCusName(), stockBean.getHopePrice(), stockBean.getCount());
        }
    }
}
