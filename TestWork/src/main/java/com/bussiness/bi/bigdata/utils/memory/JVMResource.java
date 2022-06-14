package com.bussiness.bi.bigdata.utils.memory;

import java.lang.management.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * JVM信息
 *
 * @author chenqixu
 */
public class JVMResource {

    public static void main(String[] args) {
        new JVMResource().printSummary();
    }

    private NumberFormat fmtI = new DecimalFormat("###,###", new DecimalFormatSymbols(Locale.ENGLISH));
    private NumberFormat fmtD = new DecimalFormat("###,##0.000", new DecimalFormatSymbols(Locale.ENGLISH));

    private final int Kb = 1024;

    public void printSummary() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        ThreadMXBean threads = ManagementFactory.getThreadMXBean();
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        ClassLoadingMXBean cl = ManagementFactory.getClassLoadingMXBean();
        System.out.printf("jvmName:%s %s %s%n", runtime.getVmName(), "version", runtime.getVmVersion());
        System.out.printf("jvmJavaVer:%s%n", System.getProperty("java.version"));
        System.out.printf("jvmVendor:%s%n", runtime.getVmVendor());
        System.out.printf("jvmUptime:%s%n", toDuration(runtime.getUptime()));
        System.out.printf("threadsLive:%d%n", threads.getThreadCount());
        System.out.printf("threadsDaemon:%d%n", threads.getDaemonThreadCount());
        System.out.printf("threadsPeak:%d%n", threads.getPeakThreadCount());
        System.out.printf("threadsTotal:%d%n", threads.getTotalStartedThreadCount());
        System.out.printf("heapCurr:%d%n", mem.getHeapMemoryUsage().getUsed() / Kb);
        System.out.printf("heapMax:%d%n", mem.getHeapMemoryUsage().getMax() / Kb);
        System.out.printf("heapCommitted:%d%n", mem.getHeapMemoryUsage().getCommitted() / Kb);
        System.out.printf("osName:%s %s %s%n", os.getName(), "version", os.getVersion());
        System.out.printf("osArch:%s%n", os.getArch());
        System.out.printf("osCores:%s%n", os.getAvailableProcessors());
        System.out.printf("clsCurrLoaded:%s%n", cl.getLoadedClassCount());
        System.out.printf("clsLoaded:%s%n", cl.getTotalLoadedClassCount());
        System.out.printf("clsUnloaded:%s%n", cl.getUnloadedClassCount());

    }

    protected String printSizeInKb(double size) {
        return fmtI.format((long) (size / 1024)) + " kbytes";
    }

    protected String toDuration(double uptime) {
        uptime /= 1000;
        if (uptime < 60) {
            return fmtD.format(uptime) + " seconds";
        }
        uptime /= 60;
        if (uptime < 60) {
            long minutes = (long) uptime;
            String s = fmtI.format(minutes) + (minutes > 1 ? " minutes" : " minute");
            return s;
        }
        uptime /= 60;
        if (uptime < 24) {
            long hours = (long) uptime;
            long minutes = (long) ((uptime - hours) * 60);
            String s = fmtI.format(hours) + (hours > 1 ? " hours" : " hour");
            if (minutes != 0) {
                s += " " + fmtI.format(minutes) + (minutes > 1 ? " minutes" : " minute");
            }
            return s;
        }
        uptime /= 24;
        long days = (long) uptime;
        long hours = (long) ((uptime - days) * 24);
        String s = fmtI.format(days) + (days > 1 ? " days" : " day");
        if (hours != 0) {
            s += " " + fmtI.format(hours) + (hours > 1 ? " hours" : " hour");
        }
        return s;
    }
}
