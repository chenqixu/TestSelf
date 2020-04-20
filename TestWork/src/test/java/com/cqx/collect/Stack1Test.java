package com.cqx.collect;

import org.junit.Test;

public class Stack1Test {

    @Test
    public void stackTest() {
        new Stack1().stackTest();
    }

    @Test
    public void evaluateTest() {
        String t = "(1+((22+3)*(14*5)))";
        new Stack1().evaluate(t);
    }
}