package com.cqx.common.utils.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 剪贴板工具
 *
 * @author chenqixu
 */
public class ClipBoardUtil {
    private static final Logger logger = LoggerFactory.getLogger(ClipBoardUtil.class);

    /**
     * 从剪切板获取信息
     *
     * @return
     */
    public static ClipBoardValue getSysClipBoard(DataFlavor dataFlavor, ClipBoardValue clipBoardValue)
            throws IOException, UnsupportedFlavorException {
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 获取剪切板中的内容
        Transferable clipTf = sysClip.getContents(null);
        if (clipTf != null) {
            if (clipTf.isDataFlavorSupported(dataFlavor)) {
                if (dataFlavor.equals(DataFlavor.stringFlavor)) {//文本类型
                    clipBoardValue.setValue(clipTf.getTransferData(DataFlavor.stringFlavor));
                } else if (dataFlavor.equals(DataFlavor.javaFileListFlavor)) {//文件列表类型
                    clipBoardValue.setValue(clipTf.getTransferData(DataFlavor.javaFileListFlavor));
                }
            }
        }
        return clipBoardValue;
    }

    public static String getSysClipBoardText() throws IOException, UnsupportedFlavorException {
        ClipBoardValueString clipBoardValueString = new ClipBoardValueString();
        getSysClipBoard(DataFlavor.stringFlavor, clipBoardValueString);
        return clipBoardValueString.getValue();
    }

    public static void setSysClipBoardText(String string) {
        Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = new StringSelection(string);
        sysc.setContents(transferable, null);
    }

    public static List<File> getSysClipBoardFileList() throws IOException, UnsupportedFlavorException {
        ClipBoardValueFileList clipBoardValueFileList = new ClipBoardValueFileList();
        getSysClipBoard(DataFlavor.javaFileListFlavor, clipBoardValueFileList);
        return clipBoardValueFileList.getValue();
    }

    public static void setSysClipBoardFileList(final List<File> fileList) {
        Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{DataFlavor.javaFileListFlavor};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.javaFileListFlavor.equals(flavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                if (isDataFlavorSupported(flavor)) return fileList;
                throw new UnsupportedFlavorException(flavor);
            }
        };
        sysc.setContents(transferable, null);
    }

    interface ClipBoardValue<T> {
        T getValue();

        void setValue(T t);
    }

    static class ClipBoardValueString implements ClipBoardValue<String> {
        String value;

        @Override
        public String getValue() {
            return this.value;
        }

        @Override
        public void setValue(String value) {
            this.value = value;
        }
    }

    static class ClipBoardValueFileList implements ClipBoardValue<List<File>> {
        List<File> fileList = new ArrayList<>();

        @Override
        public List<File> getValue() {
            return this.fileList;
        }

        @Override
        public void setValue(List<File> fileList) {
            this.fileList = fileList;
        }
    }
}
