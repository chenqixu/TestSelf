package com.cqx.logs;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 按小时滚动日志，指定保存的时间，删除超出时间的日志
 *
 * @ClassName: CustomLogAppender
 * @Description: TODO
 * @author: yangwenlin
 * @date: 2020年3月3日 下午9:11:00
 */
public class CustomLogAppender extends FileAppender {

    static final int TOP_OF_TROUBLE = -1;
    static final int TOP_OF_MINUTE = 0;
    static final int TOP_OF_HOUR = 1;
    static final int HALF_DAY = 2;
    static final int TOP_OF_DAY = 3;
    static final int TOP_OF_WEEK = 4;
    static final int TOP_OF_MONTH = 5;
    static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
    Date now = new Date();
    SimpleDateFormat sdf;
    SimpleDateFormat newSdf = new SimpleDateFormat("'.'yyyy-MM-dd");
    RollingCalendar rc = new RollingCalendar();
    int checkPeriod = -1;
    private String datePattern = "'.'yyyy-MM-dd";
    private String scheduledFilename;
    /**
     * 最多保存天数，默认两天
     */
    private int maxBackupIndex = 2;
    private long nextCheck = System.currentTimeMillis() - 1L;

    public CustomLogAppender() {
    }

    public CustomLogAppender(Layout layout, String filename, String datePattern) throws IOException {
        super(layout, filename, true);
        this.datePattern = datePattern;
        this.activateOptions();
    }

    public String getDatePattern() {
        return this.datePattern;
    }

    public void setDatePattern(String pattern) {
        this.datePattern = pattern;
    }

    public void activateOptions() {
        super.activateOptions();
        if (this.datePattern != null && this.fileName != null) {
            this.now.setTime(System.currentTimeMillis());
            this.sdf = new SimpleDateFormat(this.datePattern);
            int type = this.computeCheckPeriod();
            this.printPeriodicity(type);
            this.rc.setType(type);
            File file = new File(this.fileName);
            this.scheduledFilename = this.fileName + this.sdf.format(new Date(file.lastModified()));
        } else {
            LogLog.error("Either File or DatePattern options are not set for appender [" + this.name + "].");
        }

    }

    void printPeriodicity(int type) {
        switch (type) {
            case 0:
                LogLog.debug("Appender [" + this.name + "] to be rolled every minute.");
                break;
            case 1:
                LogLog.debug("Appender [" + this.name + "] to be rolled on top of every hour.");
                break;
            case 2:
                LogLog.debug("Appender [" + this.name + "] to be rolled at midday and midnight.");
                break;
            case 3:
                LogLog.debug("Appender [" + this.name + "] to be rolled at midnight.");
                break;
            case 4:
                LogLog.debug("Appender [" + this.name + "] to be rolled at start of week.");
                break;
            case 5:
                LogLog.debug("Appender [" + this.name + "] to be rolled at start of every month.");
                break;
            default:
                LogLog.warn("Unknown periodicity for appender [" + this.name + "].");
        }

    }

    int computeCheckPeriod() {
        RollingCalendar rollingCalendar = new RollingCalendar(gmtTimeZone, Locale.getDefault());
        Date epoch = new Date(0L);
        if (this.datePattern != null) {
            for (int i = 0; i <= 5; ++i) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.datePattern);
                simpleDateFormat.setTimeZone(gmtTimeZone);
                String r0 = simpleDateFormat.format(epoch);
                rollingCalendar.setType(i);
                Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
                String r1 = simpleDateFormat.format(next);
                if (r0 != null && r1 != null && !r0.equals(r1)) {
                    return i;
                }
            }
        }

        return -1;
    }

    void rollOver() throws IOException {
        if (this.datePattern == null) {
            this.errorHandler.error("Missing DatePattern option in rollOver().");
        } else {
            String datedFilename = this.fileName + this.sdf.format(this.now);
            if (!this.scheduledFilename.equals(datedFilename)) {
                this.closeFile();
                File target = new File(this.scheduledFilename);
                if (target.exists()) {
                    target.delete();
                }

                File file = new File(this.fileName);
                boolean result = file.renameTo(target);
                if (result) {
                    LogLog.debug(this.fileName + " -> " + this.scheduledFilename);
                } else {
                    LogLog.error("Failed to rename [" + this.fileName + "] to [" + this.scheduledFilename + "].");
                }
                // 删除过期文件
                if (maxBackupIndex > 0) {
                    File folder = new File(file.getParent());
                    List<String> maxBackupIndexDates = getMaxBackupIndexDates();
                    for (File ff : folder.listFiles()) {
                        // 遍历目录，将日期不在备份范围内的日志删掉
                        if (ff.getName().startsWith(file.getName()) && !ff.getName().equals(file.getName())) {
                            // 获取文件名带的日期时间戳
                            String marked = ff.getName().substring(file.getName().length());
                            String markedDate = marked.substring(0, marked.lastIndexOf("-"));
                            if (!maxBackupIndexDates.contains(markedDate)) {
                                result = ff.delete();
                            }
                            if (result) {
                                LogLog.debug(ff.getName() + " -> deleted ");
                            } else {
                                LogLog.error("Failed to deleted old DayRollingFileAppender file :" + ff.getName());
                            }
                        }
                    }
                }
                try {
                    this.setFile(this.fileName, true, this.bufferedIO, this.bufferSize);
                } catch (IOException var6) {
                    this.errorHandler.error("setFile(" + this.fileName + ", true) call failed.");
                }

                this.scheduledFilename = datedFilename;
            }

        }
    }

    /**
     * 根据maxBackupIndex配置的保存天数，获取要保留log文件的日期范围集合
     *
     * @return list<' fileName + yyyy-MM-dd '>
     */
    List<String> getMaxBackupIndexDates() {
        List<String> result = new ArrayList<String>();
        if (maxBackupIndex > 0) {
            for (int i = 0; i < maxBackupIndex; i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(System.currentTimeMillis()));
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                // 注意MILLISECOND,毫秒也要置0...否则错了也找不出来的
                calendar.add(Calendar.DATE, -i);
                result.add(newSdf.format(calendar.getTime()));
            }
        }
        return result;
    }

    protected void subAppend(LoggingEvent event) {
        long n = System.currentTimeMillis();
        if (n >= this.nextCheck) {
            this.now.setTime(n);
            this.nextCheck = this.rc.getNextCheckMillis(this.now);

            try {
                this.rollOver();
            } catch (IOException var5) {
                if (var5 instanceof InterruptedIOException) {
                    Thread.currentThread().interrupt();
                }

                LogLog.error("rollOver() failed.", var5);
            }
        }

        super.subAppend(event);
    }

    public int getMaxBackupIndex() {
        return maxBackupIndex;
    }

    public void setMaxBackupIndex(int maxBackupIndex) {
        this.maxBackupIndex = maxBackupIndex;
    }
}

//这个类是org.apache.log4j里的私有类，外类无法引用，所以我在这里完全copy了出来
class RollingCalendar extends GregorianCalendar {
    //private static final long serialVersionUID = -3560331770601814177L;
    int type = -1;

    RollingCalendar() {
    }

    RollingCalendar(TimeZone tz, Locale locale) {
        super(tz, locale);
    }

    void setType(int type) {
        this.type = type;
    }

    public long getNextCheckMillis(Date now) {
        return this.getNextCheckDate(now).getTime();
    }

    public Date getNextCheckDate(Date now) {
        this.setTime(now);
        switch (this.type) {
            case 0:
                this.set(13, 0);
                this.set(14, 0);
                this.add(12, 1);
                break;
            case 1:
                this.set(12, 0);
                this.set(13, 0);
                this.set(14, 0);
                this.add(11, 1);
                break;
            case 2:
                this.set(12, 0);
                this.set(13, 0);
                this.set(14, 0);
                int hour = this.get(11);
                if (hour < 12) {
                    this.set(11, 12);
                } else {
                    this.set(11, 0);
                    this.add(5, 1);
                }
                break;
            case 3:
                this.set(11, 0);
                this.set(12, 0);
                this.set(13, 0);
                this.set(14, 0);
                this.add(5, 1);
                break;
            case 4:
                this.set(7, this.getFirstDayOfWeek());
                this.set(11, 0);
                this.set(12, 0);
                this.set(13, 0);
                this.set(14, 0);
                this.add(3, 1);
                break;
            case 5:
                this.set(5, 1);
                this.set(11, 0);
                this.set(12, 0);
                this.set(13, 0);
                this.set(14, 0);
                this.add(2, 1);
                break;
            default:
                throw new IllegalStateException("Unknown periodicity type.");
        }

        return this.getTime();
    }
}