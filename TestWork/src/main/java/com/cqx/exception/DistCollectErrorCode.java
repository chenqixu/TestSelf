package com.cqx.exception;

import com.cqx.exception.base.ErrorCode;

/**
 * 分布式采集错误码
 * <pre>
 *  分布式采集错误码分段
 *  001-099：初始化与公共部分的错误
 *  100-199：扫描守护线程的错误
 *  200-249：扫描服务端的错误
 *  250-299：扫描客户端的错误
 *  300-399：扫描线程的错误
 *  400-499：采集线程的错误
 *  500-599：合并线程的错误
 * </pre>
 * 注意：本页代码勿格式化
 *
 * @author huangxw
 * @date 2018-10-29
 */
public enum DistCollectErrorCode implements ErrorCode {
    COMMON_ERROR("cdc-001", "XXXXX YYYYYY."),

    // 扫描服务端的错误
    S_MESSAGE_FAILURE("cdc-200", "扫描服务端接收异常报文."),
    S_NULL_MESSAGE("cdc-201", "扫描服务端接收空报文."),
    S_MESSAGE_TYPE_ERROR("cdc-203", "扫描服务端接收报文的通信类型异常."),
    S_QUERY_FILE_ERROR("cdc-204", "扫描服务端应答文件查询中发生异常."),
    // 扫描客户端的错误
    C_MESSAGE_FAILURE("cdc-250", "扫描客户端接收异常报文."),
    C_NULL_MESSAGE("cdc-251", "扫描客户端接收空报文."),
    C_MESSAGE_TYPE_ERROR("cdc-252", "扫描客户端接收报文的通信类型异常."),
    C_CONNECT_ERROR("cdc-253", "扫描服务客户端连接失败."),
    // 扫描线程的错误
    ADD_FILE_ERROR("cdc-300", "添加扫描文件队列失败."),
    SCAN_FTP_ERROR("cdc-301", "扫描FTP源目录失败."),
    // 采集线程的错误
    COLLECTION_PUTMESSAGE_ERROR("cdc-400", "往文件队列写数据异常."),
    COLLECTION_READLINE_ERROR("cdc-401", "读取记录失败."),
    COLLECTION_MOVEFILE_ERROR("cdc-402", "将文件移动往错误目录失败."),
    COLLECTION_DEALFILE_ERROR("cdc-403", "文件处理异常(isFileExist|createFileReader)."),
    COLLECTION_RELEASEFILEREADER_ERROR("cdc-404", "文件流释放异常."),
    COLLECTION_CLEANFILE_ERROR("cdc-405", "清理文件操作异常."),
    COLLECTION_NULLFILE_ERROR("cdc-406", "空文件异常."),
    COLLECTION_FTP_ERROR("cdc-407", "FTP异常."),
    COLLECTION_FILENOTFIND_ERROR("cdc-408", "文件不存在异常."),
    COLLECTION_READIO_ERROR("cdc-409", "读取文件IO异常."),
    COLLECTION_GETFILELOCK_ERROR("cdc-410", "获取文件锁异常."),
    COLLECTION_FILEISLOCK_ERROR("cdc-411", "文件已经被锁定异常."),
    COLLECTION_LOCKFILE_ERROR("cdc-412", "文件加锁异常."),
    COLLECTION_RELEASELOCKFILE_ERROR("cdc-413", "文件释放锁异常."),
    COLLECTION_UPDATELOCKFILEPOS_ERROR("cdc-414", "更新锁中的文件位置异常."),
    // 合并线程的错误
    MERGE_FILECLOSE_ERROR("cdc-500", "文件流释放异常."),
    MERGE_MAXFILENAME_ERROR("cdc-501", "输出文件名超过允许的最大长度."),
    MERGE_APPENDFILE_ERROR("cdc-502", "输出文件无法进行续写，IO异常."),
    MERGE_INITZKNODE_ERROR("cdc-503", "初始化zookeeper节点异常."),
    MERGE_PERSISTENCEZKDATA_ERROR("cdc-504", "持久化数据到zookeeper异常."),
    MERGE_REMOVEZKDATA_ERROR("cdc-505", "从zookeeper移除持久化的数据异常."),
    MERGE_GETZKDATA_ERROR("cdc-506", "从zookeeper获取数据异常."),
    MERGE_INITHDFS_ERROR("cdc-507", "初始化分布式文件系统异常."),
    MERGE_RENAMETMPFILE_ERROR("cdc-508", "重命名Tmp文件异常."),
    MERGE_PARSERFILEDATE_ERROR("cdc-509", "解析周期异常."),
    MERGE_FILEIO_ERROR("cdc-510", "文件写入发生IO异常."),
    MERGE_FILEAPPEND_ERROR("cdc-511", "文件续写发生异常.");

    private final String code;
    private final String desc;

    private DistCollectErrorCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public String toString() {
        return String.format("Code:[%s], Describe:[%s]", this.code, this.desc);
    }
}