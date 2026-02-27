package com.bussiness.bi.bigdata.file.logfile;

import com.cqx.common.utils.excel.ExcelSheetList;
import com.cqx.common.utils.excel.ExcelUtils;
import com.cqx.common.utils.file.FileResult;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.log.LogBackUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 日志文件处理-方式1<br>
 * 找不到logback的配置文件，在VM Option中添加-Dlogback.configurationFile=D:\Document\Workspaces\Git\TestSelf\TestWork\src\main\resources\logback.xml
 *
 * @author chenqixu
 */
public class LogFileDeal {
    private final static Logger logger = LoggerFactory.getLogger(LogFileDeal.class);

    // 已完成
    private Map<String, ExcelFileBean> completeMap = new HashMap<>();
    // 遗留下来要到下个周期
    private Map<String, ExcelFileBean> unresolvedMap = new HashMap<>();
    // 机动事项
    private Map<String, ExcelFileBean> contingencyMap = new HashMap<>();
    // 日志的任务清单
    private Map<String, ExcelFileBean> logTaskListMap = new HashMap<>();
    // excel的任务清单
    private Map<String, ExcelFileBean> excelTaskListMap = new HashMap<>();
    // 更新清单
    private List<ExcelFileBean> updateTaskList = new ArrayList<>();

    // 最大序号
    private int maxSeqNum;
    // 日志的开始和结束时间
    private String logMonday;
    private String logSunday;
    // 星期和日期对应关系
    private Map<Integer, String> weekAndDay = new HashMap<>();

    /**
     * 入参1：类型，[1]日志解析，[2]excel解析，[3]日志解析+excel解析+excel更新<br>
     * 入参2：日志文件路径，配置在Program arguments<br>
     * 入参3：excel文件路径，配置在Program arguments
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // 日志配置文件加载分析
        LogBackUtil.printLoggerConfig();

        // 至少要2个参数
        if (args.length >= 2) {
            String type = args[0].trim();
            logger.info("[类型]{}", type);
            String filePath = args[1];
            logger.info("[文件输入路径]{}", filePath);
            LogFileDeal lfd = new LogFileDeal();
            switch (type) {
                case "1":
                    lfd.logDeal(filePath);
                    break;
                case "2":
                    lfd.excelDeal(filePath);
                    break;
                case "3":
                    if (args.length == 3) {
                        String excelPath = args[2];
                        logger.info("[excel输入路径]{}", excelPath);
                        lfd.logAndExcelDeal(filePath, excelPath);
                    } else {
                        logger.warn("类型是3，需要3个入参！");
                    }
                    break;
                default:
                    logger.warn("不认识的类型！");
                    break;
            }
        } else {
            logger.warn("至少需要2个入参！\n1）类型：1]日志解析，2]excel解析，3]日志解析+excel解析+excel更新\n2）日志文件路径 | excel文件路径\n3）[excel文件路径]");
        }
    }

    /**
     * 日志文件读取识别
     *
     * @param path
     */
    public void logDeal(String path) throws Exception {
        logger.info("====【处理日志文件】====");
        String startStr = "#        周一:";
        String endStr = "# 周一到周日日志结束标志";
        String keyStr = "### <周";
        String contingencyStr = "机动事项"; // contingency，意外、突发情况
        String startTimeStr = "日志开始时间：";
        String endTimeStr = "日志结束时间：";

        FileUtil fileUtil = new FileUtil();
        try {
            // 读取文件
            fileUtil.setReader(path);
            // 从"#        周一:"开始读取日志，读取到"# 周一到周日日志结束标志"为止
            // 扫描一下"### <周一>"、"### <周二>"、……
            fileUtil.read(new FileResult<String>() {
                AtomicBoolean hasRead = new AtomicBoolean(false);

                @Override
                public void run(String content) throws IOException {
                    // 获取开始和结束时间
                    if (content.startsWith(startTimeStr)) {
                        logMonday = content.replace(startTimeStr, "");
                    } else if (content.startsWith(endTimeStr)) {
                        logSunday = content.replace(endTimeStr, "");
                        // 日期推算
                        weekDeal();
                    }
                    // 判断是否开头
                    if (content.equals(startStr)) {
                        hasRead.set(true);
                    }
                    // 判断是否结束
                    if (content.equals(endStr)) {
                        hasRead.set(false);
                    }
                    // 判断是否能进行正文读取
                    if (hasRead.get()) {
                        if (content.startsWith(keyStr)) {
                            ExcelFileBean lfb = new ExcelFileBean(content);
                            logTaskListMap.put(lfb.getRealTaskName(), lfb);
                            if (content.contains(TaskStatusEnum.COMPLETED.getName())) {
                                completeMap.put(lfb.getRealTaskName(), lfb);
                            } else if (content.contains(contingencyStr)) {
                                contingencyMap.put(lfb.getRealTaskName(), lfb);
                            } else {
                                unresolvedMap.put(lfb.getRealTaskName(), lfb);
                            }
                        }
                    }
                }

                @Override
                public void tearDown() throws IOException {
                    // 剔除已完成
                    for (ExcelFileBean excelFileBean : completeMap.values()) {
                        contingencyMap.remove(excelFileBean.getRealTaskName());
                        unresolvedMap.remove(excelFileBean.getRealTaskName());
                    }
                }
            });

            logger.info("<!-- 列会自动生成");
            // 打印已完成
            logger.info("* 上周任务完成情况");
            logger.info("- 已完成任务数：{}", completeMap.size());
            List<LogFileBean> completeList = new ArrayList<>(completeMap.values());
            // 排序
//            Collections.sort(completeList);
            // 如果要倒序
//            Collections.reverse(completeList);
            // 按任务分类分组，再按星期排序
            completeList.sort(Comparator
                    .comparing(LogFileBean::getTaskType)
                    .thenComparing(LogFileBean::getWeekNum, Comparator.naturalOrder())
            );
            int seq = 1;
            for (LogFileBean lfb : completeList) {
//                logger.info("{}. {}完成：{}", seq++, lfb.getDealWeek(), lfb.getTaskName());
                logger.info("{}. {}", seq++, lfb.getTaskName());
            }
            logger.info("");

            // 打印待办
            logger.info("* 本周计划");
            logger.info("- 计划待办任务数：{}", unresolvedMap.size());
            List<LogFileBean> unresolvedList = new ArrayList<>(unresolvedMap.values());
//            Collections.sort(unresolvedList);
            // 按任务分类分组，再按星期排序
            unresolvedList.sort(Comparator
                    .comparing(LogFileBean::getTaskType)
                    .thenComparing(LogFileBean::getWeekNum, Comparator.naturalOrder())
            );

            seq = 1;
            for (LogFileBean lfb : unresolvedList) {
                logger.info("{}. {}，上周进展：{}，【{}】", seq++, lfb.getTaskName(), lfb.getTaskStatus(), lfb.getTaskPIC());
            }
            logger.info("");

            // 打印机动事项，看看是否要把机动事项转换为正式待办
            logger.info("- 机动事项待办任务数：{}", contingencyMap.size());
            List<LogFileBean> contingencyList = new ArrayList<>(contingencyMap.values());
            Collections.sort(contingencyList);
            seq = 1;
            for (LogFileBean lfb : contingencyList) {
                logger.info("{}. {}，上周进展：{}，【{}】", seq++, lfb.getTaskName(), lfb.getTaskStatus(), lfb.getTaskPIC());
            }
            logger.info("-->");
        } catch (Exception e) {
            throw e;
        } finally {
            fileUtil.closeRead();
        }
    }

    /**
     * <pre>
     *     读取当前excel内容，和输入的日志进行比对，然后更新excel
     *     1、更新任务，
     *     2、新增任务
     * </pre>
     *
     * @param path
     */
    public void excelDeal(String path) throws Exception {
        List<ExcelSheetList> list;
        ExcelUtils eu = new ExcelUtils();
        AtomicBoolean isFirst = new AtomicBoolean(true);
        try {
            list = eu.readExcel(path);
            if (list != null) {
                // 循环sheet
                for (ExcelSheetList excelSheetList : list) {
                    if (excelSheetList.getSheetName().equals("任务列表")) {
                        for (List<String> contents : excelSheetList.getSheetList()) {
                            // 跳过首行
                            if (isFirst.getAndSet(false)) {
                                continue;
                            }
                            ExcelFileBean efb = new ExcelFileBean(contents);
                            excelTaskListMap.put(efb.getRealTaskName(), efb);
                            logger.debug("{}", efb);
                            if (efb.getSeqNum() > maxSeqNum) maxSeqNum = efb.getSeqNum();
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw e;
        }
    }

    public void logAndExcelDeal(String logPath, String excelPath) throws Exception {
        logDeal(logPath);
        excelDeal(excelPath);
        logger.info("====【处理Excel】====");
        // 比对一下excel里任务清单 和 日志清单里不一样的地方
        logger.info("====【更新】====");
        // 循环excel，判断更新的部分
        // 更新的话，要补充任务完成时间
        for (ExcelFileBean lfb : excelTaskListMap.values()) {
            ExcelFileBean _lfb = logTaskListMap.get(lfb.getRealTaskName());
            compareLogFileBean(lfb, _lfb);
        }
        updateTaskList.sort(Comparator.comparingInt(ExcelFileBean::getSeqNum));
        for (ExcelFileBean efb : updateTaskList) {
            logger.info("{}", efb.toExcel());
        }

        logger.info("====【新增】====");
        // 循环日志，判断新增的部分
        // 新增的话，要补充任务开始时间
        for (ExcelFileBean lfb : logTaskListMap.values()) {
            if (excelTaskListMap.get(lfb.getRealTaskName()) == null) {
                // 补全开始时间
                lfb.setStartTime(weekAndDay.get(lfb.getWeekNum()));
                // 补全结束时间
                if (lfb.getTaskStatus().equals(TaskStatusEnum.COMPLETED.getName())) {
                    lfb.setEndTime(weekAndDay.get(lfb.getWeekNum()));
                }
                lfb.setSeqNum(++maxSeqNum);
                logger.info("{}", lfb.toExcel());
            }
        }
    }

    /**
     * 比较LogFileBean，比较o1和o2的不同，以o1作为蓝本
     *
     * @param o1
     * @param o2
     */
    public void compareLogFileBean(ExcelFileBean o1, ExcelFileBean o2) {
        if (o2 == null) {
            return;
        }
        boolean isUpdate = false;
        String prototypeRealTaskName = o1.getRealTaskName();// prototype 原型/蓝本
        logger.debug("==比较蓝本=={}", prototypeRealTaskName);
        // 先判断真实任务名是否相同
        if (!o1.getRealTaskName().equals(o2.getRealTaskName())) {
            logger.debug("【{}】真实任务名不同！[o1]{}，[o2]{}", prototypeRealTaskName
                    , o1.getRealTaskName(), o2.getRealTaskName());
            return;
        }
        if (!o1.getTaskType().equals(o2.getTaskType())) {
            logger.info("【{}】任务分类不同！[o1]{}，[o2]{}", prototypeRealTaskName
                    , o1.getTaskType(), o2.getTaskType());
            o1.setTaskType(o2.getTaskType());
            isUpdate = true;
        }
        if (!o1.getTaskStatus().equals(o2.getTaskStatus())) {
            logger.info("【{}】任务状态不同！[o1]{}，[o2]{}", prototypeRealTaskName
                    , o1.getTaskStatus(), o2.getTaskStatus());
            o1.setTaskStatus(o2.getTaskStatus());
            isUpdate = true;
        }
        if (!o1.getTaskPIC().equals(o2.getTaskPIC())) {
            logger.info("【{}】任务负责人不同！[o1]{}，[o2]{}", prototypeRealTaskName
                    , o1.getTaskPIC(), o2.getTaskPIC());
            o1.setTaskPIC(o2.getTaskPIC());
            isUpdate = true;
        }
        // 更新完成时间
        if (o2.getTaskStatus().equals(TaskStatusEnum.COMPLETED.getName())) {
            // 通过星期获得对应日期
            o1.setEndTime(weekAndDay.get(o2.getWeekNum()));
            isUpdate = true;
        }
        // 加入更新列表
        if (isUpdate) {
            updateTaskList.add(o1);
        }
    }

    /**
     * 通过周一和周末的日期进行推算，得出周二到周六的日期<br>
     * 已知周一和周日日期 (格式: yyyymmdd)
     */
    public void weekDeal() {
        // 定义日期格式器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        // 1. 将字符串解析为 LocalDate 对象
        LocalDate monday = LocalDate.parse(logMonday, formatter);
        LocalDate sunday = LocalDate.parse(logSunday, formatter);

        // 2. 验证输入日期是否合理（可选但推荐）
        if (!monday.getDayOfWeek().toString().equals("MONDAY")) {
            logger.warn("警告：提供的周一日期可能不正确。");
        }
        if (!sunday.getDayOfWeek().toString().equals("SUNDAY")) {
            logger.warn("警告：提供的周日日期可能不正确。");
        }
        if (monday.isAfter(sunday)) {
            logger.error("错误：周一日期不能在周日之后。");
            return;
        }

        // 3. 计算周二到周六的日期
        List<String> weekendDates = new ArrayList<>();
        // 从周一加1天到加5天
        for (int i = 1; i <= 5; i++) {
            LocalDate date = monday.plusDays(i);
            // 格式化为 yyyymmdd
            weekendDates.add(date.format(formatter));
        }

        // 4. 输出结果
        String[] weekdays = {"周二", "周三", "周四", "周五", "周六"};
        logger.info("已知周一: {}", logMonday);
        weekAndDay.put(1, logMonday);
        int weekStart = 2;
        for (int i = 0; i < weekendDates.size(); i++) {
            logger.info(weekdays[i] + ": " + weekendDates.get(i));
            weekAndDay.put(weekStart++, weekendDates.get(i));
        }
        logger.info("已知周日: {}", logSunday);
        weekAndDay.put(7, logSunday);

        logger.info("星期和日期对应关系：{}", weekAndDay);
    }
}
