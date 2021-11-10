package com.cqx.download.http;

import com.cqx.download.yaoqi.FileUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * StreamDeal
 *
 * @author chenqixu
 */
public interface StreamDeal {
    Object deal(InputStream inputStream, FileUtil fileUtil) throws IOException;
}
