package com.cqx.download.http;

import com.cqx.download.yaoqi.FileUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * StringStreamDeal
 *
 * @author chenqixu
 */
public class StringStreamDeal implements StreamDeal {

    @Override
    public Object deal(InputStream inputStream, FileUtil fileUtil) throws IOException {
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            inputStreamReader = new InputStreamReader(
                    inputStream, "utf-8");
            bufferedReader = new BufferedReader(
                    inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
        } finally {
            if (bufferedReader != null) bufferedReader.close();
            if (inputStreamReader != null) inputStreamReader.close();
        }
        return buffer.toString();
    }
}
