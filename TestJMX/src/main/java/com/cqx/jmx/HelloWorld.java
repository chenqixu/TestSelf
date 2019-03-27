package com.cqx.jmx;

public class HelloWorld implements HelloWorldMBean {
    private String greeting;
    private boolean paused;

    public HelloWorld(String greeting) {
        this.greeting = greeting;
    }

    public HelloWorld() {
        this.greeting = "hello world!";
    }

    @Override
    public String getGreeting() {
        return greeting;
    }

    @Override
    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    @Override
    public void printGreeting() {
        System.out.println(greeting);
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void pause(boolean paused) {
        this.paused = paused;
    }

    @Override
    public String exec(String cmd) {
        System.out.println("exec " + cmd);
        return "success";
    }
}
