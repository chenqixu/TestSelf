package com.cqx.pierce.clipboard;

import com.cqx.pierce.bean.ClipBoardValue;

/**
 * ClipBoardTool
 *
 * @author chenqixu
 */
public class ClipBoardTool {
    private static volatile ClipBoard clipBoard = new ClipBoard();

    public static ClipBoard read() {
        clipBoard.read();
        return clipBoard;
    }

    public static void setClipBoard(ClipBoard.ClipBoardStatus status, ClipBoardValue clipBoardValue) {
        if (clipBoard != null) {
            clipBoard.setClipValue(status, clipBoardValue);
        }
    }

    public static void setClipBoardStatus(ClipBoard.ClipBoardStatus status) {
        if (clipBoard != null) {
            clipBoard.setClipValue(status, new ClipBoardValue());
        }
    }
}
