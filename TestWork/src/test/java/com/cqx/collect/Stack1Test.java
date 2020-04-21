package com.cqx.collect;

import org.junit.Test;

public class Stack1Test {

    @Test
    public void stackTest() {
        new Stack1().stackTest();
    }

    @Test
    public void evaluateTest() {
        String t = "1+(2*(1+((2+3)*(4*5))))";
        new Stack1().evaluate(t);
    }
}