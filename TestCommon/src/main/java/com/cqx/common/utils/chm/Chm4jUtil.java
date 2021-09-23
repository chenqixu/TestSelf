package com.cqx.common.utils.chm;

import org.chm4j.ChmEntry;
import org.chm4j.ChmFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 解析chm工具
 * <pre>
 *   实际上，使用命令进行反编译，反编译成html可能更好
 *   命令：
 *       hh -decompile d:\tmp\chm\html\ d:\tmp\chm\01.chm
 *   使用的时候，需要下载chm4j-0.0.1-full.zip
 *   并使用Dev C++编译一个64位的dll
 *   然后放到$JAVA_HOME/bin下面
 * </pre>
 *
 * @author chenqixu
 */
public class Chm4jUtil {
    private static final Logger logger = LoggerFactory.getLogger(Chm4jUtil.class);

    public void read(String path) throws IOException {
        ChmFile cFile = new ChmFile(path);
        String outputDir = "d:\\tmp\\chm\\api\\";
        ChmEntry.Attribute attributes = ChmEntry.Attribute.ALL;
        ChmEntry[] entries = cFile.entries(attributes);
        for (ChmEntry entry : entries) {
            listChmEntry(outputDir, entry, attributes);
        }
    }

    private void printEntry(ChmEntry entry) {
        StringBuilder sb = new StringBuilder("Extract entry " + entry + "(");
        boolean first = true;
        for (ChmEntry.Attribute attribute : entry.getAttributes()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(attribute);
        }
        sb.append(")");
        logger.info(sb.toString());
    }

    private void listChmEntry(String output, ChmEntry entry, ChmEntry.Attribute attributes) throws IOException {
        printEntry(entry);
        File dest = new File(output, entry.getPath());
        if (entry.hasAttribute(ChmEntry.Attribute.DIRECTORY)) {
            if (!dest.isDirectory()) {
                if (!dest.mkdirs()) {
                    throw new IOException("failed to create directory : " + dest);
                }
            }
            for (ChmEntry e : entry.entries(attributes)) {
                listChmEntry(output, e, attributes);
            }
        } else {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = entry.getInputStream();
                out = new FileOutputStream(dest);
                int bufferSize = 1024;
                byte[] data = new byte[bufferSize];
                int nbRead;
                while ((nbRead = in.read(data)) > 0) {
                    out.write(data, 0, nbRead);
                    out.flush();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }
        }
    }
}
