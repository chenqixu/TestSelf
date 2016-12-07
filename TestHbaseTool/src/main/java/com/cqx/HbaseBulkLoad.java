package com.cqx;

public class HbaseBulkLoad {
	public static void main(String[] args) {
		final HBaseTool ht = new HbaseExport();
		// 解析参数
		HbaseInputBean hib = ht.parseArgs(args);
		// 入库
		ht.BulkLoad(hib);
		/*
		 * 在jvm中增加一个关闭的钩子，当jvm关闭的时候，会执行系统中已经设置的所有通过方法addShutdownHook添加的钩子，
		 * 当系统执行完这些钩子后，jvm才会关闭。所以这些钩子可以在jvm关闭的时候进行内存清理、对象销毁等操作
		 * */
		Runtime.getRuntime().addShutdownHook(
			new Thread("relase-shutdown-hook") {
				@Override
				public void run() {
					// 释放连接池资源
					ht.relaseEnd();
				}
			}
		);
	}
}
