package com.newland.bi.bigdata.bean;

/**
 * HashCodeFile
 *
 * @author chenqixu
 */
public class HashCodeFile {
    private String FileName;
    private String Cycle;
    private int DataIndex;

    public HashCodeFile(String FileName, String Cycle) {
        this.FileName = FileName;
        this.Cycle = Cycle;
    }

    /**
     * 示例：S04002_20190813120000_001
     *
     * @param FileName
     */
    public HashCodeFile(String FileName) {
        this.FileName = FileName;
        this.Cycle = FileName.split("_", -1)[1];
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getCycle() {
        return Cycle;
    }

    public void setCycle(String cycle) {
        Cycle = cycle;
    }

    public int getDataIndex() {
        return DataIndex;
    }

    public void setDataIndex(int dataIndex) {
        DataIndex = dataIndex;
    }
}
