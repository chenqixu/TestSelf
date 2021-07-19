package com.cqx.common.utils.file;

/**
 * 文件数据记录
 * <pre>
 *     offset：数据位置
 *     value：数据
 * </pre>
 *
 * @author chenqixu
 */
public class FileRecord {
    private long offset;
    private String value;

    public FileRecord(long offset, String value) {
        setOffset(offset);
        setValue(value);
    }

    @Override
    public String toString() {
        return "offset : " + getOffset() + " , value : " + getValue();
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
