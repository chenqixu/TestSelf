package com.cqx.common.utils.compress.tar;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.z.ZCompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * tar包工具
 *
 * @author chenqixu
 */
public class TarUtil {
    private static final Logger logger = LoggerFactory.getLogger(TarUtil.class);
    private static final int BUFFERSIZE = 2048;

    public static TarUtil builder() {
        return new TarUtil();
    }

    /**
     * 解压*.z文件
     *
     * @param file    z包文件
     * @param outPath z包下解压后文件存放路径
     * @return 解压后文件名
     */
    public String unZFile(File file, String outPath) {
        ZCompressorInputStream zIn = null;
        try {
            FileInputStream fin = new FileInputStream(file);
            BufferedInputStream in = new BufferedInputStream(fin);
            // 若获取z文件名，最好使用lastIndexOf，不要使用indexOf，比如filename.z
            String name = file.getName().substring(0, file.getName().lastIndexOf("."));
            zIn = new ZCompressorInputStream(in);
            String newFileName = outPath + File.separator + name;
            saveToFile(newFileName, zIn);
            return newFileName;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                Objects.requireNonNull(zIn).close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * tar文件解压
     *
     * @param filePath 要解压的文件
     */
    public List<String> unTarFile(String filePath) {
        return unTarFile(filePath, null);
    }

    /**
     * tar文件解压
     *
     * @param file 要解压的文件
     */
    public List<String> unTarFile(File file) {
        return unTarFile(file, null);
    }

    /**
     * tar文件解压
     *
     * @param filePath 要解压的文件
     * @param fileRule 规则，查找特定的文件
     */
    public List<String> unTarFile(String filePath, String fileRule) {
        File file = new File(filePath);
        return unTarFile(file, file.getParent() + File.separator, fileRule);
    }

    /**
     * tar文件解压
     *
     * @param file     要解压的文件
     * @param fileRule 规则，查找特定的文件
     */
    public List<String> unTarFile(File file, String fileRule) {
        return unTarFile(file, file.getParent() + File.separator, fileRule);
    }


    public List<String> unTarFile(File file, String outPath, String fileRule) {
        return unTarFile(file, outPath, fileRule, null, false);
    }

    public List<String> unTarFile(String filePath, String outPath, String fileRule, String rename, boolean isDelete) {
        File file = new File(filePath);
        return unTarFile(file, file.getParent() + File.separator, fileRule, rename, isDelete);
    }

    /**
     * tar文件解压
     *
     * @param file     要解压的文件
     * @param outPath  输出路径
     * @param fileRule 规则，查找特定的文件
     * @param rename   重命名，只有有规则的情况下才适用
     * @param isDelete 是否删除文件
     */
    public List<String> unTarFile(File file, String outPath, String fileRule, String rename, boolean isDelete) {
        String sourceFileName = file.getName();
        TarArchiveInputStream is = null;
        // 存储tar包下所有z文件名
        List<String> zFileNames = new ArrayList<>();
        try {
            is = new TarArchiveInputStream(new FileInputStream(file));
            while (true) {
                TarArchiveEntry entry = is.getNextTarEntry();
                if (entry == null) {
                    break;
                }
                String entryName = entry.getName();
                zFileNames.add(entryName);
                // 有规则，查找特定的文件
                if (fileRule != null && fileRule.length() > 0) {
                    if (!entry.isDirectory() && entryName.contains(fileRule)) {
                        saveToFile(outPath + rename, is);
                        break;
                    }
                } else {// 无规则
                    if (entry.isDirectory()) {
                        // 如果是目录，就在当前文件夹创建一个同名目录
                        boolean mkdir = new File(outPath + entryName).mkdirs();
                        logger.info("路径：{} 创建目录结果：{}", outPath + entryName, mkdir);
                    } else {
                        saveToFile(outPath + entryName, is);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                Objects.requireNonNull(is).close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (isDelete && file.exists()) {
            boolean delete = file.delete();
            logger.info("文件：{} 删除结果：{}", sourceFileName, delete);
        }
        // 返回tar包下处理过的文件名
        return zFileNames;
    }

    private void saveToFile(String savePath, InputStream is) throws IOException {
        FileOutputStream os = null;
        try {
            File f = new File(savePath);
            // 创建父级路径
            if (!f.getParentFile().exists()) {
                boolean mkdir = f.getParentFile().mkdirs();
                logger.info("路径：{} 创建目录结果：{}", f.getParentFile().getName(), mkdir);
            }
            // 文件为空就创一个空文件
            if (!f.exists()) {
                boolean create = f.createNewFile();
                logger.info("文件：{} 创建空文件结果：{}", f.getName(), create);
            }
            os = new FileOutputStream(f);
            byte[] bs = new byte[BUFFERSIZE];
            int len;
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            os.flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            Objects.requireNonNull(os).close();
        }
    }
}
