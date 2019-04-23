package com.cqx.test;

import org.apache.spark.deploy.SparkSubmit;

/**
 * RemoteSubmit
 *
 * @author chenqixu
 */
public class RemoteSubmit {

    public static void main(String[] args) {
        new RemoteSubmit().submit();
    }

    public void submit() {
        System.out.print("test java submit spark!");
        String[] conf = {
                "--master", "yarn",
                "--class", "com.cqx.test.Test1",
                "--conf", "spark.default.parallelism=200",
                "--conf", "spark.network.timeout=600",
                "--conf", "spark.sql.shuffle.partitions=200",
                "--conf", "spark.executor.memoryOverhead=2024",
                "--conf", "spark.driver.memoryOverhead=2024",
                "--executor-cores", "6",
                "--num-executors", "5",
                "--executor-memory", "1g",
                "--driver-memory", "8g"
        };
        SparkSubmit.main(conf);
    }
}
