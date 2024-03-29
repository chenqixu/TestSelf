package com.cqx.common.utils.compress.zip;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * ZipUtils
 * <br>压缩单个文件
 * <br>压缩多个文件
 * <br>压缩加密
 * <br>压缩解密
 *
 * @author chenqixu
 */
public class ZipUtils {

    private static final int BUFFER_SIZE = 2 * 1024;

    /**
     * 递归压缩方法
     *
     * @param sourceFile       源文件
     * @param zos              zip输出流
     * @param name             压缩后的名称
     * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    private void compress(File sourceFile, ZipOutputStream zos, String name,
                          boolean KeepDirStructure) throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if (KeepDirStructure) {
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), KeepDirStructure);
                    }
                }
            }
        }

    }

    /**
     * 压缩
     *
     * @param srcDir           压缩文件夹路径
     * @param out              压缩文件输出流
     * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     */
    public void compress(String srcDir, OutputStream out, boolean KeepDirStructure) {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String unZip(InputStream is, String filename_rule) throws IOException {
        Map<String, String> muFile = unZip(is);
        for (Map.Entry<String, String> entry : muFile.entrySet()) {
            if (entry.getKey().contains(filename_rule)) {
                return entry.getValue();
            }
        }
        return "";
    }

    public Map<String, String> unZip(InputStream is) throws IOException {
        Map<String, String> map = new HashMap<>();
        ZipInputStream zis = null;
        ZipEntry zipEntry;
        if (is != null) zis = new ZipInputStream(is);
        while (zis != null && (zipEntry = zis.getNextEntry()) != null) {
            String filename = zipEntry.getName();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] byte_s = new byte[BUFFER_SIZE];
            int num;
            // 通过read方法来读取文件内容
            while ((num = zis.read(byte_s, 0, byte_s.length)) > -1) {
                byteArrayOutputStream.write(byte_s, 0, num);
            }
            byte[] byte_s_result = byteArrayOutputStream.toByteArray();
            // 将字节数组转化为字符串，UTF-8格式（容许中文）
            map.put(filename, new String(byte_s_result, StandardCharsets.UTF_8));
        }
        return map;
    }
}
