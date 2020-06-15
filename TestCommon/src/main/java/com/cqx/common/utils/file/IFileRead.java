package com.cqx.common.utils.file;

import java.io.IOException;

/**
 * IFileRead
 *
 * @author chenqixu
 */
public interface IFileRead {
    void run(String content) throws IOException;

    void tearDown() throws IOException;
}
