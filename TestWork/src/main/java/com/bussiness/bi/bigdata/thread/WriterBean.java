package com.bussiness.bi.bigdata.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.bussiness.bi.bigdata.time.TimeHelper;
import com.newland.storm.component.etl.hdfs.common.AbstractHDFSWriter;
import org.apache.hadoop.fs.Path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WriterBean implements Comparable<WriterBean> {
    // 文件数据周期时间，会持久化到zookeeper
    String fileDataDate;
    // 路径+名称，会持久化到zookeeper
    String pathAndFileName;
    // 文件路径
    @JSONField(serialize = false)
    Path pathForNextFile;
    // 写入writer
    @JSONField(serialize = false)
    AbstractHDFSWriter abstractHDFSWriter;
    @JSONField(serialize = false)
    long startTime = System.currentTimeMillis();
    @JSONField(serialize = false)
    long usedTime;

    public WriterBean() {
    }

    public WriterBean(Path pathForNextFile, String fileDataDate) {
        this.pathForNextFile = pathForNextFile;
        this.fileDataDate = fileDataDate;
        this.pathAndFileName = pathForNextFile.getParent() + "/" + pathForNextFile.getName();
    }

    public WriterBean(String pathAndFileName, String fileDataDate) {
        this.pathAndFileName = pathAndFileName;
        this.fileDataDate = fileDataDate;
    }

    public WriterBean(AbstractHDFSWriter abstractHDFSWriter) {
        this.pathAndFileName = abstractHDFSWriter.getFilePath();
        this.fileDataDate = abstractHDFSWriter.getDataDate();
        this.abstractHDFSWriter = abstractHDFSWriter;
        this.pathForNextFile = new Path(abstractHDFSWriter.getFilePath());
    }

    public Path getPathForNextFile() {
        return pathForNextFile;
    }

    public void setPathForNextFile(Path pathForNextFile) {
        this.pathForNextFile = pathForNextFile;
    }

    public String getFileDataDate() {
        return fileDataDate;
    }

    public void setFileDataDate(String fileDataDate) {
        this.fileDataDate = fileDataDate;
    }

    public AbstractHDFSWriter getAbstractHDFSWriter() {
        return abstractHDFSWriter;
    }

    public void setAbstractHDFSWriter(AbstractHDFSWriter abstractHDFSWriter) {
        this.abstractHDFSWriter = abstractHDFSWriter;
    }

    /**
     * 在只知道路径+文件名的情况下，返回其中的文件名
     *
     * @return
     */
    public String getFileName() {
        return new Path(pathAndFileName).getName();
    }

    public String toString() {
        return "[pathandfilename]" + pathAndFileName + ",[fileDataDate]" + fileDataDate;
    }


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getUsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    public void setUsedTime(long usedTime) {
        this.usedTime = usedTime;
    }

    /**
     * 从List中移除自己
     *
     * @param srclist
     * @return
     */
    public List<WriterBean> removeFromList(List<WriterBean> srclist) {
        List<WriterBean> destlist = new ArrayList<WriterBean>(srclist);
        // Collections.copy(destlist, srclist);// 注意：这里不能使用copy，会提示Source does
        // not fit in dest，原因是ArrayList没有改变size
        if (destlist != null) {
            Iterator<WriterBean> it = destlist.iterator();
            while (it.hasNext()) {
                if (it.next().getPathAndFileName().equals(this.getPathAndFileName())) {
                    it.remove();
                    break;
                }
            }
        }
        return destlist;
    }

    public String getPathAndFileName() {
        return pathAndFileName;
    }

    public void setPathAndFileName(String pathAndFileName) {
        this.pathAndFileName = pathAndFileName;
    }

    @Override
    public int compareTo(WriterBean o) {
        // 比较规则
        return TimeHelper.timeComparison(this.fileDataDate, o.fileDataDate);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }
}
