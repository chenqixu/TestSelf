package com.cqx.pierce.clipboard;

import com.cqx.pierce.bean.ClipBoardValue;
import org.junit.Test;

public class ClipBoardTest {

    private ClipBoard clipBoard;

    @Test
    public void read() {
        clipBoard = new ClipBoard();
        clipBoard.setClipValue(ClipBoard.ClipBoardStatus.VM_SEND, new ClipBoardValue());
        clipBoard.read();
        System.out.println(clipBoard.getStatus());
        System.out.println(clipBoard.getClipBoardValue());
    }
}