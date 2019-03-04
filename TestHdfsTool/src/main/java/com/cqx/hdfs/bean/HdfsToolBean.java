package com.cqx.hdfs.bean;

import com.cqx.common.option.CmdOpImpl;
import com.cqx.common.option.CmdOpImpl.CmdIOp;

/**
 * hdfs工具bean
 *
 * @author chenqixu
 */
@CmdOpImpl
public class HdfsToolBean {
    String hadoop_user;
    String path;
    String conf_path;
    String type;
    String readCode;
    boolean isFlush = false;
    int limitcnt = 100000;
    String retryseq = "0";

    public String getHadoop_user() {
        return hadoop_user;
    }

    @CmdIOp(opt = "h", longOpt = "hadoopuser", description = "hadoopuser")
    public void setHadoop_user(String hadoop_user) {
        this.hadoop_user = hadoop_user;
    }

    public String getPath() {
        return path;
    }

    @CmdIOp(opt = "p", longOpt = "path", description = "path")
    public void setPath(String path) {
        this.path = path;
    }

    public String getConf_path() {
        return conf_path;
    }

    @CmdIOp(opt = "c", longOpt = "confpath", description = "confpath")
    public void setConf_path(String conf_path) {
        this.conf_path = conf_path;
    }

    public String getType() {
        return type;
    }

    @CmdIOp(opt = "t", longOpt = "type", description = "type")
    public void setType(String type) {
        this.type = type;
    }

    public boolean isFlush() {
        return isFlush;
    }

    @CmdIOp(opt = "f", longOpt = "flush", description = "flush", required = false)
    public void setFlush(boolean flush) {
        isFlush = flush;
    }

    public int getLimitcnt() {
        return limitcnt;
    }

    @CmdIOp(opt = "lc", longOpt = "limitcnt", description = "limitcnt", required = false)
    public void setLimitcnt(int limitcnt) {
        this.limitcnt = limitcnt;
    }

    public String getReadCode() {
        return readCode;
    }

    @CmdIOp(opt = "rc", longOpt = "readcode", description = "readcode")
    public void setReadCode(String readCode) {
        this.readCode = readCode;
    }

    public String getRetryseq() {
        return retryseq;
    }

    @CmdIOp(opt = "rs", longOpt = "retryseq", description = "retryseq")
    public void setRetryseq(String retryseq) {
        this.retryseq = retryseq;
    }
}
