package com.cqx.thinking;

import org.junit.Test;

public class HanoiTest {

    @Test
    public void hanoi() {
        Hanoi hanoi = new Hanoi();
        hanoi.init();
        hanoi.peek(null);
//        for (int i = 0; i < 10; i++) {
//            hanoi.peek(null);
//        }
    }
}