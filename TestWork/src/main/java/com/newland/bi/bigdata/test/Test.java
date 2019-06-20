package com.newland.bi.bigdata.test;

/**
 * Test
 *
 * @author chenqixu
 */
public class Test {
    public static void main(String[] args) {
        System.out.println("Hello World.");
        String[] command = {"/usr/java/jdk1.8.0_161/bin/java", "-classpath", "/bi/user/cqx/java/PipeTest", "Test2"};
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process;
        int resultcode = 0;
        try {
            process = builder.start();
            resultcode = process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("got exception");
        }
        System.out.println("resultcode：" + resultcode);
        System.out.println(System.getenv().toString());
        System.out.println(System.getProperties().toString());
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }
}
