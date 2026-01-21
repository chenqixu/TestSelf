package com.bussiness.bi.bigdata.file;

import com.cqx.common.utils.file.FileResult;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 年终总结-日志数据处理
 *
 * @author chenqixu
 */
public class YearEndSummary {
    private final static Logger logger = LoggerFactory.getLogger(YearEndSummary.class);

    public static void main(String[] args) {
        YearEndSummary yearEndSummary = new YearEndSummary();
        yearEndSummary.fileDeal("D:\\Document\\BaiduNetdiskWorkspace\\个人日志\\work_log\\2025\\");
    }

    public void fileDeal(String path) {
        int fileCnt = 0;
        final String startStr = "#        周一:";
        final String endStr = "# 公司日志结束标志";
        List<LogFile> logFiles = new ArrayList<>();
        FileUtil fileUtil = new FileUtil();
        // 文件后缀是md，需要排除文件名中带有"述职"
        for (File file : FileUtil.listFilesEndWith(path, "md", "述职")) {
            logger.info("扫描到文件={}", file.getPath());
            fileCnt++;
            LogFile logFile = new LogFile();
            logFile.setPath(file.getPath());
            logFile.setFileName(file.getName());
            try {
                fileUtil.setReader(file.getPath());
                fileUtil.read(new FileResult<String>() {
                    AtomicBoolean startFlag = new AtomicBoolean(false);

                    @Override
                    public void run(String content) throws IOException {
                        if (content.startsWith(startStr)) {
                            startFlag.set(true);
                        } else if (content.startsWith(endStr)) {
                            startFlag.set(false);
                        }
                        if (startFlag.get()) {
                            logFile.addFileContent(content);
                        }
                    }
                });
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                fileUtil.closeRead();
            }
            logger.info("文件处理完成，{}", logFile);
            logFiles.add(logFile);
        }
        logger.info("文件个数={}", fileCnt);

        // 排序
        Collections.sort(logFiles);

        // 分季度处理
        TimeCostUtil tc = new TimeCostUtil();
        for (int i = 1; i < 5; i++) {
            tc.start();
            FileUtil writer = new FileUtil();
            String writeName = "d:\\Document\\BaiduNetdiskWorkspace\\个人日志\\work_log\\2025\\述职报告材料-日志汇总-第%s季度.txt";
            try {
                writer.createFile(String.format(writeName, i));

                // 处理
                for (LogFile _logFile : logFiles) {
                    if (_logFile.getQuarter() == i) {
                        logger.info("第{}季度，开始处理文件={}", i, _logFile.getFileName());
                        if (_logFile.getFileLine() > 0) {
                            writer.write(String.format("==[%s]开始==", _logFile.getFileName()));
                            writer.newline();
                            for (String _content : _logFile.getFileContent()) {
                                // 跳过"请假"关键字
                                if (!_content.contains("请假")) {
                                    writer.write(_content);
                                    writer.newline();
                                }
                            }
                            writer.write(String.format("==[%s]结束==", _logFile.getFileName()));
                            writer.newline();
                        }
                    }
                }
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            } finally {
                writer.closeWrite();
                logger.info("处理完成，总耗时{} ms", tc.stopAndGet());
            }
        }
    }

    class LogFile implements Comparable<LogFile> {
        String path;
        String fileName;
        int nameNum;
        int quarter;
        List<String> fileContent = new ArrayList<>();

        @Override
        public String toString() {
            return String.format("fileName=%s, fileLine=%s", getFileName(), getFileLine());
        }

        public int getNameNum() {
            return this.nameNum;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
            String tmp = fileName.replace("年", "");
            tmp = tmp.replace("月", "");
            tmp = tmp.replace("第", "");
            tmp = tmp.replace("周日志.md", "");
            tmp = tmp.replace("一", "1");
            tmp = tmp.replace("二", "2");
            tmp = tmp.replace("三", "3");
            tmp = tmp.replace("四", "4");
            tmp = tmp.replace("五", "5");
            this.quarter = (Integer.valueOf(tmp.substring(4, 6)) - 1) / 3 + 1;
            this.nameNum = Integer.valueOf(tmp);
        }

        public List<String> getFileContent() {
            return fileContent;
        }

        public int getFileLine() {
            return fileContent.size();
        }

        public void addFileContent(String fileContent) {
            this.fileContent.add(fileContent);
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getQuarter() {
            return quarter;
        }

        /**
         * o放后面，表示按ASC进行排序（正序）
         *
         * @param o
         * @return
         */
        @Override
        public int compareTo(@NotNull LogFile o) {
            return Long.compare(getNameNum(), o.getNameNum());
        }
    }
}
