package com.cqx.common.utils.cmd;

import com.cqx.common.utils.OtherUtil;
import com.cqx.common.utils.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * RunUtil
 *
 * @author chenqixu
 */
public class RunUtil {
    private static final Logger logger = LoggerFactory.getLogger(RunUtil.class);
    private static final String fileSparator = File.separator;
    private String mainClass;
    private String classPath;
    private String javaHome;
    private List<String> params;

    public String[] getCommand() {
        classPath = FileUtil.endWith(classPath);
        String[] jars = FileUtil.listFile(classPath, ".jar");
        List<String> lists = new ArrayList<>();
        javaHome = FileUtil.endWith(javaHome);
        if (OtherUtil.isWindow()) {
            lists.add(javaHome + "bin" + fileSparator + "java.exe");
        } else {
            lists.add(javaHome + "bin" + fileSparator + "java");
        }
        StringBuilder sb = new StringBuilder();
        for (String jar : jars) {
            sb.append(classPath).append(jar);
            if (OtherUtil.isWindow()) {
                sb.append(";");
            } else {
                sb.append(":");
            }
        }
        lists.add("-classpath");
        lists.add(sb.toString());
        lists.add(mainClass);
        if (params != null && params.size() > 0) lists.addAll(params);
        String[] arr = new String[lists.size()];
        logger.debug("getCommand：{}", lists);
        return lists.toArray(arr);
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }
}
