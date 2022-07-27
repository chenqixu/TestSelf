package com.cqx.services.work;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 工作工厂
 *
 * @author chenqixu
 */
public class WorkFactory {
    private static List<WorkInf> works = new ArrayList<>();

    static {
        loadInitialWorks();
    }

    public static void registerWork(WorkInf workInf) {
        works.add(workInf);
    }

    private static void loadInitialWorks() {
        ServiceLoader<WorkInf> loadedWorks = ServiceLoader.load(WorkInf.class);
        Iterator<WorkInf> worksIterator = loadedWorks.iterator();
        try {
            while (worksIterator.hasNext()) {
                WorkInf work = worksIterator.next();
                System.out.println("加载到: " + work);
            }
        } catch (Throwable t) {
            // Do nothing
        }
    }

    public static String getWork(String tag) {
        for (WorkInf work : works) {
            String str = work.doWork(tag);
            if (str != null) return str;
        }
        return null;
    }
}
