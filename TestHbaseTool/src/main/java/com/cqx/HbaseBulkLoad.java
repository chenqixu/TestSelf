package com.cqx;

public class HbaseBulkLoad {
	public static void main(String[] args) {
		final HBaseTool ht = new HbaseExport();
		// ��������
		HbaseInputBean hib = ht.parseArgs(args);
		// ���
		ht.BulkLoad(hib);
		/*
		 * ��jvm������һ���رյĹ��ӣ���jvm�رյ�ʱ�򣬻�ִ��ϵͳ���Ѿ����õ�����ͨ������addShutdownHook��ӵĹ��ӣ�
		 * ��ϵͳִ������Щ���Ӻ�jvm�Ż�رա�������Щ���ӿ�����jvm�رյ�ʱ������ڴ������������ٵȲ���
		 * */
		Runtime.getRuntime().addShutdownHook(
			new Thread("relase-shutdown-hook") {
				@Override
				public void run() {
					// �ͷ����ӳ���Դ
					ht.relaseEnd();
				}
			}
		);
	}
}
