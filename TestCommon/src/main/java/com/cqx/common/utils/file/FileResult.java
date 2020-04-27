package com.cqx.common.utils.file;

import java.util.ArrayList;
import java.util.List;

/**
 * FileResult
 *
 * @author chenqixu
 */
public abstract class FileResult<T> extends FileCount {
    private List<T> fileresult = new ArrayList<>();

    protected void addFileresult(T t) {
        fileresult.add(t);
    }

    public List<T> getFileresult() {
        return fileresult;
    }

    protected void clearFileresult() {
        fileresult.clear();
    }
}
