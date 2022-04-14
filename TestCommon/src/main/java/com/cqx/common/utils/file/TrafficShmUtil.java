package com.cqx.common.utils.file;

/**
 * 基于共享内存的异步无锁IPC类库<br>
 * <pre>
 *     使用sun.misc.Unsafe和FileChannel提供共享内存机制的纯Java实现
 *
 *     共享内存是进程间通信的有效机制。 内存映射文件提供动态内存管理功能，
 *     允许应用程序以与将虚拟地址空间的物理内存共享段相同的方式访问磁盘上的文件。
 *
 *     使用非阻塞算法，实现多生产者/单消费者并发队列，可用于构建具有高吞吐量和低延迟的实时系统。
 *
 *     数据对齐方式为4字节对齐，字节顺序为大端模式。
 *
 *     提供一个OAOO（ONCE-AND-ONLY-ONCE）保证的FIFO队列。光标只能向前传送，一旦消息成功传递，
 *     消息就是AUTOMATIC ACKNOWLEDGMENT，这意味着一旦接收者接收到消息，就会确认消息。
 * </pre>
 *
 * @author chenqixu
 */
public class TrafficShmUtil {
    io.traffic.shm.async.Queue queue;
}
