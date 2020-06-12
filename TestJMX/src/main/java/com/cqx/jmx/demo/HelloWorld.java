package com.cqx.jmx.demo;

public class HelloWorld implements HelloWorldMBean {
    private String greeting;
    private boolean paused;
    private CacheBean cacheBean;

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

    @Override
    public int size() {
        return cacheBean == null ? 0 : cacheBean.size();
    }

    public void setCacheBean(CacheBean cacheBean) {
        this.cacheBean = cacheBean;
    }
}
